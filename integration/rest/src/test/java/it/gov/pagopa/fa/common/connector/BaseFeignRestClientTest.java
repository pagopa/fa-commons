package it.gov.pagopa.fa.common.connector;

import eu.sia.meda.DummyConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {DummyConfiguration.class, FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
public abstract class BaseFeignRestClientTest {

}
