package it.gov.pagopa.fa.common.exception;

public class NotNullIfException extends Exception {

    public NotNullIfException(String propertyRoot, String propertyAnnotation, String value) {
        super("The property " + propertyRoot +
                " must not be null if " + propertyAnnotation +
                " is equals to " + value);
    }
}
