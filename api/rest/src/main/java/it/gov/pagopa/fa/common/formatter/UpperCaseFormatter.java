package it.gov.pagopa.fa.common.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class UpperCaseFormatter implements Formatter<String> {
    @Override
    public String parse(String s, Locale locale) throws ParseException {
        return s.toUpperCase();
    }

    @Override
    public String print(String s, Locale locale) {
        return s.toUpperCase();
    }
}
