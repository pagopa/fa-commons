package it.gov.pagopa.fa.common.connector.jpa;

import eu.sia.meda.connector.jpa.JPAConnectorImpl;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@EnableJpaRepositories(
        repositoryBaseClass = JPAConnectorImpl.class,
        basePackages = {"it.gov.pagopa.fa"}
)
public abstract class BaseJpaIntegrationTest {
}