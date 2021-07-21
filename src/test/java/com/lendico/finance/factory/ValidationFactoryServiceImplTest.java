package com.lendico.finance.factory;

import com.lendico.finance.exception.GenericClientRuntimeException;
import com.lendico.finance.model.GeneratePlanRequestDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.time.LocalDateTime;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RunWith(JUnit4.class)
public class ValidationFactoryServiceImplTest {

    private static final Double LOAN_AMOUNT = 5000.0;
    private static final Double NOMINAL_RATE = 5.0;
    private static final Integer DURATION = 24;

    private ValidationFactoryServiceImpl validationFactoryService = new ValidationFactoryServiceImpl(
            Validation.buildDefaultValidatorFactory().getValidator());

    @DisplayName("Test bean validation success")
    @Test
    public void testValidateBeanValid() {

        validationFactoryService.validObject(GeneratePlanRequestDto.builder()
                .loanAmount(LOAN_AMOUNT)
                .nominalRate(NOMINAL_RATE)
                .duration(DURATION)
                .startDate(LocalDateTime.now())
                .build());

        Assert.assertTrue("Always True", true);
    }

    @DisplayName("Test bean validation failure and verify ConstraintViolationException")
    @Test(expected = ConstraintViolationException.class)
    public void testWhenBeanValidationFailure() {

        validationFactoryService.validObject(GeneratePlanRequestDto.builder()
                .loanAmount(null)
                .nominalRate(NOMINAL_RATE)
                .duration(DURATION)
                .startDate(LocalDateTime.now())
                .build());
    }

    @DisplayName("Test bean validation failure with null object and verify GenericClientRuntimeException")
    @Test(expected = GenericClientRuntimeException.class)
    public void testWhenBeanValidationWithNullObject() {

        validationFactoryService.validObject(null);
    }
}
