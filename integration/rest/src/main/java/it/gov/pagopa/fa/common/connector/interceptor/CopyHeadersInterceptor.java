package it.gov.pagopa.fa.common.connector.interceptor;

import eu.sia.meda.core.interceptors.BaseContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class CopyHeadersInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        if (log.isDebugEnabled()) {
            log.debug("CopyHeadersInterceptor.apply");
            log.debug("template = " + template);
        }

        Map<String, String> headers = BaseContextHolder.getApplicationContext().getCopyHeader();
        if (headers != null) {
            for (Map.Entry<String, String> h : headers.entrySet()) {
                template.header(h.getKey());
                template.header(h.getKey(), h.getValue());
            }
        }
    }

}
