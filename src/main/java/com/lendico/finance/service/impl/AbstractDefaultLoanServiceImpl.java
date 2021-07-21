package com.lendico.finance.service.impl;

import com.lendico.finance.exception.GenericClientRuntimeException;
import com.lendico.finance.exception.GenericServerRuntimeException;
import com.lendico.finance.factory.ValidationFactoryServiceImpl;
import com.lendico.finance.model.BorrowerPaymentDto;
import com.lendico.finance.model.GeneratePlanRequestDto;
import com.lendico.finance.model.GeneratePlanResponseDto;
import com.lendico.finance.service.LoanService;
import com.lendico.finance.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public abstract class AbstractDefaultLoanServiceImpl implements LoanService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDefaultLoanServiceImpl.class);

    private static final Integer NO_OF_MONTH_IN_YEAR = 12;

    private AtomicReference<Double> initialOutstandingPrincipal;
    private AtomicReference<Double> interest;
    private AtomicReference<Double> principal;
    private AtomicReference<Double> remainingOutstandingPrincipal;
    private AtomicReference<LocalDateTime> paymentDate;

    private ValidationFactoryServiceImpl validationFactoryService;

    public AbstractDefaultLoanServiceImpl(ValidationFactoryServiceImpl validationFactoryService) {

        this.validationFactoryService = validationFactoryService;
    }

    /**
     * This method in charge of generating the pre-calculated loan repayment plans for the given input
     *
     * @param generatePlanRequestDto
     * @return
     */
    @Override
    public GeneratePlanResponseDto generatePlan(final GeneratePlanRequestDto generatePlanRequestDto) {

        try {

            // Validating the input parameter
            this.validationFactoryService.validObject(generatePlanRequestDto);

            // Calculating annual & monthly interest from Nominal Rate
            Double annualInterest = generatePlanRequestDto.getNominalRate() / 100;

            Double monthlyInterest = annualInterest / NO_OF_MONTH_IN_YEAR;

            // Calculating annuity
            Double annuity = this.calculateAnnuity(generatePlanRequestDto.getLoanAmount(),
                    monthlyInterest, generatePlanRequestDto.getDuration());

            if(annuity.equals(0.0)) {

                throw new GenericClientRuntimeException("Annuity calculated as zero. There is no plan available for the given input");
            }

            LOG.debug("Calculated annuity: {}", annuity);

            return GeneratePlanResponseDto
                    .builder()
                    .borrowerPayments(this.getBorrowerPaymentList(generatePlanRequestDto, annuity, annualInterest))
                    .build();

        } catch (GenericClientRuntimeException | ConstraintViolationException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while generating pre-calculated plan",exception);

            throw new GenericServerRuntimeException("Unexpected error occurred", exception);
        }
    }

    /**
     * This method is in charge of building the Borrower payment list
     *
     * @param generatePlanRequestDto
     * @param annuity
     * @param annualInterest
     * @return
     */
    private List<BorrowerPaymentDto> getBorrowerPaymentList(final GeneratePlanRequestDto generatePlanRequestDto,
                                                            final Double annuity, final Double annualInterest) {

        // Initializing the response parameters

        this.initialOutstandingPrincipal = new AtomicReference<>(0.0);
        this.interest = new AtomicReference<>(0.0);
        this.principal = new AtomicReference<>(0.0);
        this.remainingOutstandingPrincipal = new AtomicReference<>(0.0);
        this.paymentDate = new AtomicReference<>(generatePlanRequestDto.getStartDate());

        // Building Borrower Payment List

        return IntStream.range(0, generatePlanRequestDto.getDuration())
                        .mapToObj(payment -> {

                            if(payment == 0) {

                                // Setting the loan amount for the first payment

                                this.initialOutstandingPrincipal.set(generatePlanRequestDto.getLoanAmount());

                            } else {

                                // Setting the remaining outstanding principal as initial outstanding principal

                                this.initialOutstandingPrincipal.set(this.remainingOutstandingPrincipal.get());

                                // Setting the payment by adding one month to the previous payment date

                                this.paymentDate.set(CommonUtil.addMonth(this.paymentDate.get(), 1,
                                        generatePlanRequestDto.getStartDate().getDayOfMonth()));
                            }

                            this.interest.set(this.calculateInterest(annualInterest, this.initialOutstandingPrincipal.get()));

                            this.principal.set(this.calculatePrincipal(annuity, this.interest.get()));

                            // Calculating Remaining Outstanding Principal by subtracting principal from initial Outstanding Principal

                            this.remainingOutstandingPrincipal.set(this.initialOutstandingPrincipal.get() - this.principal.get());

                            // Applying round function in the final output

                            return BorrowerPaymentDto.builder()
                                    .borrowerPaymentAmount(CommonUtil.round(annuity))
                                    .date(paymentDate.get())
                                    .initialOutstandingPrincipal(CommonUtil.round(this.initialOutstandingPrincipal.get()))
                                    .interest(CommonUtil.round(this.interest.get()))
                                    .principal(CommonUtil.round(this.principal.get()))
                                    .remainingOutstandingPrincipal(CommonUtil.round(this.remainingOutstandingPrincipal.get()))
                                    .build();
                        })
                        .collect(Collectors.toList());
    }

    /**
     * This method is charge of calculating the amount using the formula
     *
     * Annuity = (Loan Amount * Monthly interest rate) / (1 - (1 + Monthly interest rate) ^ - Duration)
     *
     * Ex: Annuity = (5000 * 0.0041) / (1 - (1 + 0.0041) ^ - 24) = 219.36
     *
     * @param loanAmount
     * @param monthlyInterestRate
     * @param duration
     * @return
     */
    protected abstract Double calculateAnnuity(final Double loanAmount, final Double monthlyInterestRate, final Integer duration);

    /**
     * This method is charge of calculating the interest using below formula
     *
     * Interest = (Rate * Days in Month * Initial Outstanding Principal) / Days in Year
     *
     * Ex. Interest = (0.05 * 30 * 5000.00) / 360 = 20.83 €
     *
     * @param annualInterestRate
     * @param initialOutstandingPrincipal
     * @return
     */
    protected abstract Double calculateInterest(final Double annualInterestRate, final Double initialOutstandingPrincipal);

    /**
     * This method is charge of calculating the principal using below formula
     *
     * Principal = Annuity - Interest
     *
     * Ex. Principal = 219.36 - 20.83 = 198.53 €
     *
     * @param annuity
     * @param interest
     * @return
     */
    protected abstract Double calculatePrincipal(final Double annuity, final Double interest);
}
