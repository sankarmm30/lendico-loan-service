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
    private AtomicReference<LocalDateTime> startDate;

    private ValidationFactoryServiceImpl validationFactoryService;

    public AbstractDefaultLoanServiceImpl(ValidationFactoryServiceImpl validationFactoryService) {

        this.validationFactoryService = validationFactoryService;
    }

    @Override
    public GeneratePlanResponseDto generatePlan(final GeneratePlanRequestDto generatePlanRequestDto) {

        try {

            // Validating the input parameter
            this.validationFactoryService.validObject(generatePlanRequestDto);

            // Calculating annual interest from Nominal Rate
            Double annualInterest = generatePlanRequestDto.getNominalRate() / 100;

            // Calculating annuity
            Double annuity = this.calculateAnnuity(generatePlanRequestDto.getLoanAmount(),
                    ((generatePlanRequestDto.getNominalRate() / 100) / NO_OF_MONTH_IN_YEAR),
                    generatePlanRequestDto.getDuration());

            if(annuity.equals(0.0)) {

                throw new GenericClientRuntimeException("Annuity calculated as zero. There is no plan available for the given input");
            }

            LOG.info("Calculated annuity: {}", annuity);

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

        initialOutstandingPrincipal = new AtomicReference<>(0.0);
        interest = new AtomicReference<>(0.0);
        principal = new AtomicReference<>(0.0);
        remainingOutstandingPrincipal = new AtomicReference<>(0.0);
        startDate = new AtomicReference<>(generatePlanRequestDto.getStartDate());

        // Building Borrower Payment List

        return IntStream.range(0, generatePlanRequestDto.getDuration())
                        .mapToObj(e -> {

                            if(initialOutstandingPrincipal.get().equals(0.0)) {

                                initialOutstandingPrincipal.set(generatePlanRequestDto.getLoanAmount());

                            } else {

                                initialOutstandingPrincipal.set(remainingOutstandingPrincipal.get());

                                startDate.set(CommonUtil.addMonth(startDate.get(), 1,
                                        generatePlanRequestDto.getStartDate().getDayOfMonth()));
                            }

                            interest.set(this.calculateInterest(annualInterest, initialOutstandingPrincipal.get()));
                            principal.set(this.calculatePrincipal(annuity, interest.get()));
                            remainingOutstandingPrincipal.set(CommonUtil.round(initialOutstandingPrincipal.get() - principal.get()));

                            if(principal.get() > initialOutstandingPrincipal.get()) {

                                principal.set(initialOutstandingPrincipal.get());
                            }

                            return BorrowerPaymentDto.builder()
                                    .borrowerPaymentAmount(
                                            annuity > CommonUtil.round(principal.get() + interest.get()) ?
                                                    CommonUtil.round(principal.get() + interest.get()) : annuity)
                                    .date(startDate.get())
                                    .initialOutstandingPrincipal(initialOutstandingPrincipal.get())
                                    .interest(interest.get())
                                    .principal(principal.get())
                                    .remainingOutstandingPrincipal(
                                            remainingOutstandingPrincipal.get() < 0 ? 0.0 : remainingOutstandingPrincipal.get())
                                    .build();
                        })
                        .collect(Collectors.toList());
    }

    protected abstract Double calculateAnnuity(final Double loanAmount, final Double monthlyInterestRate, final Integer duration);

    protected abstract Double calculateInterest(final Double annualInterestRate, final Double initialOutstandingPrincipal);

    protected abstract Double calculatePrincipal(final Double annuity, final Double interest);
}
