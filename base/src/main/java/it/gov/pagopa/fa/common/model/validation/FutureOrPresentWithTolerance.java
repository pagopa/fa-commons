package it.gov.pagopa.fa.common.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.FutureOrPresent;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Constraint(validatedBy = FutureOrPresentValidatorForOffsetDateTimeWithTolerance.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(FutureOrPresentWithTolerance.List.class)
public @interface FutureOrPresentWithTolerance {

    /**
     * The tolerance for the comparision
     *
     * @return a tolerance ecpressed as minutes
     */
    long tolerance();

    String message() default "{javax.validation.constraints.FutureOrPresentWithTolerance.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@link FutureOrPresent} annotations on the same element.
     *
     * @see FutureOrPresent
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        FutureOrPresentWithTolerance[] value();
    }
}
