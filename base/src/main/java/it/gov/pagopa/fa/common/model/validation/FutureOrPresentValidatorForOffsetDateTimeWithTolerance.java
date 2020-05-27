package it.gov.pagopa.fa.common.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;
import java.time.OffsetDateTime;

public class FutureOrPresentValidatorForOffsetDateTimeWithTolerance
        implements ConstraintValidator<FutureOrPresentWithTolerance, OffsetDateTime> {

    private long tollerance;


    @Override
    public void initialize(FutureOrPresentWithTolerance constraintAnnotation) {
        tollerance = constraintAnnotation.tolerance();
    }


    @Override
    public boolean isValid(OffsetDateTime value, ConstraintValidatorContext context) {
        boolean result = true;

        if (value != null) {
            final OffsetDateTime now = OffsetDateTime.now(context.getClockProvider().getClock());
            result = value.isAfter(now.minus(Duration.ofMinutes(tollerance)));
        }

        return result;
    }

}
