package it.gov.pagopa.fa.common.exception;

import eu.sia.meda.exceptions.MedaDomainRuntimeException;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public abstract class ResourceExistsException extends MedaDomainRuntimeException {

    private static final String CODE = "resource.exists.error";
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;


    public <K extends Serializable> ResourceExistsException(Class<?> resourceClass, K id) {
        super(getMessage(resourceClass, id), CODE, STATUS);
    }

    private static String getMessage(Class<?> resourceClass, Object id) {
        return String.format("%s with id %s already exists", resourceClass.getSimpleName(), id);
    }

}
