package it.gov.pagopa.fa.common.model.validation;

import java.lang.annotation.*;

/**
 * Annotation created to link the value of the associated variable
 * to another property of the same class
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Documented
@Repeatable(NotNullIfPropertyEqualTo.List.class)
public @interface NotNullIfPropertyEqualTo {
    /**
     * Name of property to check the value
     */
    String property();

    /**
     * Value of the property to be checked
     */
    String value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD,ElementType.PARAMETER})
    @Documented
    @interface List{
        NotNullIfPropertyEqualTo[] value();
    }
}
