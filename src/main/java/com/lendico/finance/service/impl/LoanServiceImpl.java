package com.lendico.finance.service.impl;

import com.lendico.finance.constant.GlobalConstant;
import com.lendico.finance.service.LoanService;
import org.decimal4j.util.DoubleRounder;
import org.springframework.stereotype.Service;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Service("loanService")
public class LoanServiceImpl extends AbstractDefaultLoanServiceImpl implements LoanService {

    private static final Integer NO_OF_DAYS_IN_MONTH = 30;
    private static final Integer NO_OF_DAYS_IN_YEAR = 360;

    @Override
    public Double calculateAnnuity(final Double loanAmount, final Double monthlyInterestRate, final Integer duration) {

        return DoubleRounder.round(
                ((loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, - duration))),
                GlobalConstant.NO_OF_PRECISION);
    }

    @Override
    public Double calculateInterest(Double annualInterestRate, Double initialOutstandingPrincipal) {

        return DoubleRounder.round(
                (annualInterestRate * NO_OF_DAYS_IN_MONTH * initialOutstandingPrincipal) / NO_OF_DAYS_IN_YEAR,
                GlobalConstant.NO_OF_PRECISION);
    }

    @Override
    public Double calculatePrincipal(final Double annuity, final Double interest) {

        return DoubleRounder.round(annuity - interest, GlobalConstant.NO_OF_PRECISION);
    }
}
