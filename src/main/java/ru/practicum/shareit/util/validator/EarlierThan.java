package ru.practicum.shareit.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = EarlierValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EarlierThan {

    String message() default "{value} must be earlier {earlierThan}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    String value();
    String earlierThan();
}
