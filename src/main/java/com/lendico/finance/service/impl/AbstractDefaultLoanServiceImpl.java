package com.lendico.finance.service.impl;

import com.lendico.finance.constant.GlobalConstant;
import com.lendico.finance.model.BorrowerPaymentDto;
import com.lendico.finance.model.GeneratePlanRequestDto;
import com.lendico.finance.model.GeneratePlanResponseDto;
import com.lendico.finance.service.LoanService;
import org.decimal4j.util.DoubleRounder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
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

    @Override
    public GeneratePlanResponseDto generatePlan(GeneratePlanRequestDto generatePlanRequestDto) {

        Double annualInterest = generatePlanRequestDto.getNominalRate() / 100;

        Double annuity = calculateAnnuity(generatePlanRequestDto.getLoanAmount(),
                ((generatePlanRequestDto.getNominalRate() / 100) / NO_OF_MONTH_IN_YEAR),
                generatePlanRequestDto.getDuration());

        LOG.info("Calculated Annuity: {}", annuity);

        AtomicReference<Double> initialOutstandingPrincipal = new AtomicReference<>(0.0);
        AtomicReference<Double> interest = new AtomicReference<>(0.0);
        AtomicReference<Double> principal = new AtomicReference<>(0.0);
        AtomicReference<Double> remainingOutstandingPrincipal = new AtomicReference<>(0.0);

        List<BorrowerPaymentDto> borrowerPayments =
                IntStream.range(0, generatePlanRequestDto.getDuration())
                        .mapToObj(e -> {

                            if(initialOutstandingPrincipal.get().equals(0.0)) {

                                initialOutstandingPrincipal.set(generatePlanRequestDto.getLoanAmount());
                            } else {

                                initialOutstandingPrincipal.set(remainingOutstandingPrincipal.get());
                            }

                            interest.set(this.calculateInterest(annualInterest, initialOutstandingPrincipal.get()));
                            principal.set(this.calculatePrincipal(annuity, interest.get()));
                            remainingOutstandingPrincipal.set(DoubleRounder.round(initialOutstandingPrincipal.get() - principal.get(),
                                    GlobalConstant.NO_OF_PRECISION));

                            if(principal.get() > initialOutstandingPrincipal.get()) {

                                principal.set(initialOutstandingPrincipal.get());
                            }

                            return BorrowerPaymentDto.builder()
                                    .borrowerPaymentAmount(
                                            annuity > DoubleRounder.round(principal.get() + interest.get(), 2) ?
                                                    DoubleRounder.round(principal.get() + interest.get(), 2) : annuity)
                                    .date(new Date())
                                    .initialOutstandingPrincipal(initialOutstandingPrincipal.get())
                                    .interest(interest.get())
                                    .principal(principal.get())
                                    .remainingOutstandingPrincipal(
                                            remainingOutstandingPrincipal.get() < 0 ? 0.0 : remainingOutstandingPrincipal.get())
                                    .build();
                        })
                        .collect(Collectors.toList());

        return GeneratePlanResponseDto
                .builder()
                .borrowerPayments(borrowerPayments)
                .build();
    }

    protected abstract Double calculateAnnuity(final Double loanAmount, final Double monthlyInterestRate, final Integer duration);

    protected abstract Double calculateInterest(final Double annualInterestRate, final Double initialOutstandingPrincipal);

    protected abstract Double calculatePrincipal(final Double annuity, final Double interest);
}
