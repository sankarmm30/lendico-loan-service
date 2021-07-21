package com.lendico.finance.service.impl;

import com.lendico.finance.factory.ValidationFactoryServiceImpl;
import com.lendico.finance.service.LoanService;
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
    @Override
    public Double calculateAnnuity(final Double loanAmount, final Double monthlyInterestRate, final Integer duration) {

        return ((loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, - duration)));
    }

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
    @Override
    public Double calculateInterest(Double annualInterestRate, Double initialOutstandingPrincipal) {

        return (annualInterestRate * NO_OF_DAYS_IN_MONTH * initialOutstandingPrincipal) / NO_OF_DAYS_IN_YEAR;
    }

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
    @Override
    public Double calculatePrincipal(final Double annuity, final Double interest) {

        return annuity - interest;
    }
}
