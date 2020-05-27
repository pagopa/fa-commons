package it.gov.pagopa.fa.common;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.BeforeClass;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

/**
 * Base class to use in order to configurate unit test
 */
public class BaseTest {

    public BaseTest() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeClass
    public static void configTest() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        ((Logger) LoggerFactory.getLogger("eu.sia")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger("it.gov.pagopa.fa")).setLevel(Level.DEBUG);
    }
}
