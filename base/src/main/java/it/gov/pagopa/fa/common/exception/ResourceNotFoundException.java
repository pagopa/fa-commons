package it.gov.pagopa.fa.common.exception;

import eu.sia.meda.exceptions.MedaDomainRuntimeException;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public abstract class ResourceNotFoundException extends MedaDomainRuntimeException {

    private static final String CODE = "resource.not-found.error";
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;


    public <K extends Serializable> ResourceNotFoundException(Class<?> resourceClass, K id) {
        this(getMessage(resourceClass, id));
    }

    public <K extends Serializable> ResourceNotFoundException(String message) {
        super(message, CODE, STATUS);
    }

    private static String getMessage(Class<?> resourceClass, Object id) {
        return String.format("Unable to find %s with id %s", resourceClass.getSimpleName(), id);
    }

}

