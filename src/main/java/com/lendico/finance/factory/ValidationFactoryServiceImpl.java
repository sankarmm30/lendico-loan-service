package com.lendico.finance.factory;

import com.lendico.finance.exception.GenericClientRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * This factory service provides method for validating the bean in programmatically
 */
@Service("validationFactoryService")
public class ValidationFactoryServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationFactoryServiceImpl.class);

    private Validator validator;

    public ValidationFactoryServiceImpl(final Validator validator) {

        this.validator = validator;
    }

    /**
     * This method in charge of validating the given object and throws ConstraintViolationException if in case of any violation.
     *
     *
     * @param object
     */
    public void validObject(Object object) {

        if (object != null) {

            Set<ConstraintViolation<Object>> violations = validator.validate(object);

            violations.parallelStream().map(ConstraintViolation::getMessage).forEach(LOG::error);

            if (!violations.isEmpty()) {

                throw new ConstraintViolationException(violations);
            }
        } else {

            throw new GenericClientRuntimeException("The object to be validated must not be null.");
        }
    }
}
