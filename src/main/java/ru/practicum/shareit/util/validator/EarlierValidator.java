package ru.practicum.shareit.util.validator;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

@Component
public class EarlierValidator implements ConstraintValidator<EarlierThan, BookingDtoIn> {

    private String startField;
    private String endField;

    public void initialize(EarlierThan annotation) {
        startField = annotation.value();
        endField = annotation.earlierThan();
    }

    @SneakyThrows
    public boolean isValid(BookingDtoIn value, ConstraintValidatorContext context) {
        Field declaredField = value.getClass().getDeclaredField(startField);
        declaredField.setAccessible(true);
        LocalDateTime start = (LocalDateTime)declaredField.get(value);
        declaredField.setAccessible(false);

        declaredField = value.getClass().getDeclaredField(endField);
        declaredField.setAccessible(true);
        LocalDateTime end = (LocalDateTime)declaredField.get(value);
        declaredField.setAccessible(false);

        return start.isBefore(end);
    }
}
