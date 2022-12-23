package com.myboard.aop.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = CheckExistValidator.class)
public @interface CheckExist {

    EntityType type();

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
