package com.lendico.finance.service;

import com.lendico.finance.exception.GenericClientRuntimeException;
import com.lendico.finance.factory.ValidationFactoryServiceImpl;
import com.lendico.finance.model.BorrowerPaymentDto;
import com.lendico.finance.model.GeneratePlanRequestDto;
import com.lendico.finance.model.GeneratePlanResponseDto;
import com.lendico.finance.service.impl.LoanServiceImpl;
import org.decimal4j.util.DoubleRounder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RunWith(JUnit4.class)
public class LoanServiceImplTest {

    private static final Double LOAN_AMOUNT = 5000.0;
    private static final Double NOMINAL_RATE = 5.0;
    private static final Integer DURATION = 24;

    private ValidationFactoryServiceImpl validationFactoryService = new ValidationFactoryServiceImpl(
            Validation.buildDefaultValidatorFactory().getValidator());

    @InjectMocks
    private LoanService loanService = new LoanServiceImpl(validationFactoryService);

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("Test successful plan generation with valid loan details")
    @Test
    public void testGeneratePlanValid() {

        GeneratePlanResponseDto genericResponseDto = loanService.generatePlan(
                GeneratePlanRequestDto.builder()
                        .loanAmount(LOAN_AMOUNT)
                        .nominalRate(NOMINAL_RATE)
                        .duration(DURATION)
                        .startDate(LocalDateTime.now())
                        .build());

        Assert.assertNotNull(genericResponseDto);
        Assert.assertNotNull(genericResponseDto.getBorrowerPayments());
        Assert.assertFalse(genericResponseDto.getBorrowerPayments().isEmpty());
        Assert.assertEquals(24, genericResponseDto.getBorrowerPayments().size());

        List<BorrowerPaymentDto> borrowerPaymentList = genericResponseDto.getBorrowerPayments();

        // Verifying the total principal
        double totalPrincipal = DoubleRounder.round(borrowerPaymentList
                .stream()
                .mapToDouble(BorrowerPaymentDto::getPrincipal)
                .sum(), 0);

        Assert.assertEquals(5000.0, totalPrincipal, 0);

        // Verifying the total interest
        double totalInterest = borrowerPaymentList
                .stream()
                .mapToDouble(BorrowerPaymentDto::getInterest)
                .sum();

        Assert.assertEquals(264.56, totalInterest, 0);

        // Verifying the total payment
        double totalPayment = borrowerPaymentList
                .stream()
                .mapToDouble(BorrowerPaymentDto::getBorrowerPaymentAmount)
                .sum();

        Assert.assertEquals(5264.56, totalPayment, 0.08);
    }

    @DisplayName("Test successful plan generation with valid loan details and verify all attributes")
    @Test
    public void testGeneratePlanValidAndVerifyAllAttributes() {

        GeneratePlanResponseDto genericResponseDto = loanService.generatePlan(
                GeneratePlanRequestDto.builder()
                        .loanAmount(1000.0)
                        .nominalRate(20.0)
                        .duration(3)
                        .startDate(LocalDateTime.of(2018, 1,1, 0, 0))
                        .build());

        Assert.assertNotNull(genericResponseDto);
        Assert.assertNotNull(genericResponseDto.getBorrowerPayments());
        Assert.assertFalse(genericResponseDto.getBorrowerPayments().isEmpty());
        Assert.assertEquals(3, genericResponseDto.getBorrowerPayments().size());

        List<BorrowerPaymentDto> borrowerPaymentList = genericResponseDto.getBorrowerPayments();

        // Verifying the total principal
        double totalPrincipal = DoubleRounder.round(borrowerPaymentList
                .stream()
                .mapToDouble(BorrowerPaymentDto::getPrincipal)
                .sum(), 0);

        Assert.assertEquals(1000.0, totalPrincipal, 0);

        // Verifying the total interest
        double totalInterest = borrowerPaymentList
                .stream()
                .mapToDouble(BorrowerPaymentDto::getInterest)
                .sum();

        Assert.assertEquals(33.52, totalInterest, 0);

        // Verifying the total payment
        double totalPayment = borrowerPaymentList
                .stream()
                .mapToDouble(BorrowerPaymentDto::getBorrowerPaymentAmount)
                .sum();

        Assert.assertEquals(1033.52, totalPayment, 0.01);

        BorrowerPaymentDto borrowerPaymentOne = borrowerPaymentList.get(0);

        Assert.assertEquals(344.51, borrowerPaymentList.get(0).getBorrowerPaymentAmount(), 0.00);
        Assert.assertEquals(1000, borrowerPaymentList.get(0).getInitialOutstandingPrincipal(), 0.00);
        Assert.assertEquals(16.67, borrowerPaymentList.get(0).getInterest(), 0.00);
        Assert.assertEquals(327.84, borrowerPaymentList.get(0).getPrincipal(), 0.00);
        Assert.assertEquals(672.16, borrowerPaymentList.get(0).getRemainingOutstandingPrincipal(), 0.00);
        Assert.assertEquals(LocalDateTime.of(2018, 1,1, 0, 0), borrowerPaymentList.get(0).getDate());

        Assert.assertEquals(344.51, borrowerPaymentList.get(1).getBorrowerPaymentAmount(), 0.00);
        Assert.assertEquals(672.16, borrowerPaymentList.get(1).getInitialOutstandingPrincipal(), 0.00);
        Assert.assertEquals(11.2, borrowerPaymentList.get(1).getInterest(), 0.00);
        Assert.assertEquals(333.3, borrowerPaymentList.get(1).getPrincipal(), 0.00);
        Assert.assertEquals(338.86, borrowerPaymentList.get(1).getRemainingOutstandingPrincipal(), 0.00);
        Assert.assertEquals(LocalDateTime.of(2018, 2,1, 0, 0), borrowerPaymentList.get(1).getDate());

        Assert.assertEquals(344.51, borrowerPaymentList.get(2).getBorrowerPaymentAmount(), 0.00);
        Assert.assertEquals(338.86, borrowerPaymentList.get(2).getInitialOutstandingPrincipal(), 0.00);
        Assert.assertEquals(5.65, borrowerPaymentList.get(2).getInterest(), 0.00);
        Assert.assertEquals(338.86, borrowerPaymentList.get(2).getPrincipal(), 0.00);
        Assert.assertEquals(0, borrowerPaymentList.get(2).getRemainingOutstandingPrincipal(), 0.00);
        Assert.assertEquals(LocalDateTime.of(2018, 3,1, 0, 0), borrowerPaymentList.get(2).getDate());
    }

    @DisplayName("Test generate plan with annuity as zero")
    @Test(expected = GenericClientRuntimeException.class)
    public void testGeneratePlanWithAnn() {

        loanService.generatePlan(
                GeneratePlanRequestDto.builder()
                        .loanAmount(0.1)
                        .nominalRate(NOMINAL_RATE)
                        .duration(DURATION)
                        .startDate(LocalDateTime.now())
                        .build());
    }

    @DisplayName("Test unsuccessful plan generation with invalid loan amount")
    @Test(expected = ConstraintViolationException.class)
    public void testGeneratePlanWithInvalidLoanAmount() {

        loanService.generatePlan(
                GeneratePlanRequestDto.builder()
                        .loanAmount(-1.0)
                        .nominalRate(NOMINAL_RATE)
                        .duration(DURATION)
                        .startDate(LocalDateTime.now())
                        .build());
    }

    @DisplayName("Test unsuccessful plan generation with invalid nominal rate")
    @Test(expected = ConstraintViolationException.class)
    public void testGeneratePlanWithInvalidNominalRate() {

        loanService.generatePlan(
                GeneratePlanRequestDto.builder()
                        .loanAmount(LOAN_AMOUNT)
                        .nominalRate(-1.0)
                        .duration(DURATION)
                        .startDate(LocalDateTime.now())
                        .build());
    }

    @DisplayName("Test unsuccessful plan generation with invalid duration")
    @Test(expected = ConstraintViolationException.class)
    public void testGeneratePlanWithInvalidDuration() {

        loanService.generatePlan(
                GeneratePlanRequestDto.builder()
                        .loanAmount(LOAN_AMOUNT)
                        .nominalRate(NOMINAL_RATE)
                        .duration(-1)
                        .startDate(LocalDateTime.now())
                        .build());
    }

    @DisplayName("Test unsuccessful plan generation with null start date")
    @Test(expected = ConstraintViolationException.class)
    public void testGeneratePlanWithInvalidStartDate() {

        loanService.generatePlan(
                GeneratePlanRequestDto.builder()
                        .loanAmount(LOAN_AMOUNT)
                        .nominalRate(NOMINAL_RATE)
                        .duration(DURATION)
                        .startDate(null)
                        .build());
    }
}
