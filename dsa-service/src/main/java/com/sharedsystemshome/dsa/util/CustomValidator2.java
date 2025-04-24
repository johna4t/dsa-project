package com.sharedsystemshome.dsa.util;

import jakarta.validation.*;
import org.springframework.stereotype.Component;

import java.util.Collections;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomValidator2 implements ConstraintValidator<ValidatedEntity, Object> {

    private Validator validator;

    @Override
    public void initialize(ValidatedEntity constraintAnnotation) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public boolean isValid (Object object, ConstraintValidatorContext context){

        Set<ConstraintViolation<Object>> violations = validator.validate(object);

        if(!violations.isEmpty()){
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(" "));
            throw new BusinessValidationException("OOOOH WEE! " + errorMessage);
/*            Set<String> violationMessages = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
            throw new BusinessValidationException(String.join(" ", violationMessages));*/

        }

        return true;
    }
}
