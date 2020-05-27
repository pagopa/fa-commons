package it.gov.pagopa.fa.common.connector.interceptor;

import eu.sia.meda.core.interceptors.BaseContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddAuthorizationHeaderInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        if (log.isDebugEnabled()) {
            log.debug("AddAuthorizationHeaderInterceptor.apply");
            log.debug("template = " + template);
        }

        try {
            String authorizationHeader = BaseContextHolder.getAuthorizationContext().getAuthorizationHeader();
            if (authorizationHeader != null) {
                template.header(AUTHORIZATION_HEADER_NAME);
                template.header(AUTHORIZATION_HEADER_NAME, authorizationHeader);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("error retrieving authorization header");
            }
        }
    }

}
