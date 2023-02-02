package com.myboard.aop.valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Slf4j
@Component
public class NotEmptyValidator implements ConstraintValidator<NotEmptyList, List<String>> {

    @Override
    public void initialize(NotEmptyList constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        log.info("NotEmptyValidator");
        return !CollectionUtils.isEmpty(values);
    }
}