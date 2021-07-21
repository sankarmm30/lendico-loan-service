package com.lendico.finance.service.impl;

import com.lendico.finance.factory.ValidationFactoryServiceImpl;
import com.lendico.finance.service.LoanService;
import com.lendico.finance.util.CommonUtil;
import org.springframework.stereotype.Service;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Service("loanService")
public class LoanServiceImpl extends AbstractDefaultLoanServiceImpl implements LoanService {

    private static final Integer NO_OF_DAYS_IN_MONTH = 30;
    private static final Integer NO_OF_DAYS_IN_YEAR = 360;

    public LoanServiceImpl(ValidationFactoryServiceImpl validationFactoryService) {

        super(validationFactoryService);
    }

    @Override
    public Double calculateAnnuity(final Double loanAmount, final Double monthlyInterestRate, final Integer duration) {

        return CommonUtil.round(
                ((loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, - duration))));
    }

    @Override
    public Double calculateInterest(Double annualInterestRate, Double initialOutstandingPrincipal) {

        return CommonUtil.round(
                (annualInterestRate * NO_OF_DAYS_IN_MONTH * initialOutstandingPrincipal) / NO_OF_DAYS_IN_YEAR);
    }

    @Override
    public Double calculatePrincipal(final Double annuity, final Double interest) {

        return CommonUtil.round(annuity - interest);
    }
}
