package pl.marekstrejczek.jmsconsumer;

import org.apache.camel.spring.SpringCamelContext;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import pl.marekstrejczek.jmsconsumer.aop.ThrowExceptionAspectBean;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by maro on 2014-06-03.
 */
public class IntegrityTestSteps implements ApplicationContextAware {
    private static final Logger log = Logger.getLogger(IntegrityTestSteps.class);

    private ApplicationContext ac;
    private JmsTemplate jmsTemplate;
    private JdbcTemplate jdbcTemplate;

    public IntegrityTestSteps(final JmsTemplate jmsTemplate, final JdbcTemplate jdbcTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeStory
    public void beforeStory() throws Exception {
        log.info("Starting up Camel...");
        startCamel();
        log.info("Preparing DB...");
        prepareDb();
        log.info("Purging queues...");
        purgeQueues();
        TimeUnit.SECONDS.sleep(3);
    }

    @AfterStory
    public void afterStory() throws Exception {
        log.info("Stopping Camel...");
        stopCamel();
    }

    @BeforeScenario
    public void beforeScenario() {
        log.info("Setting JMS receive timeout to 1 second");
        jmsTemplate.setReceiveTimeout(1000);
    }

    @AfterScenario
    public void afterScenario() throws Exception {
        log.info("Deleting data...");
        cleanDb();
        log.info("Purging queues...");
        purgeQueues();
        log.info("Disabling exception thrower...");
        ThrowExceptionAspectBean exceptionInjector = (ThrowExceptionAspectBean)ac.getBean("throwExceptionAspectBean");
        exceptionInjector.setExceptionFlag(false);
    }

    @Given("Application is up and running")
    public void applicationUpAndRunning() {
          log.info(">>> Given: Application is up and running");
    }

    @When("I put a message on the queue")
    public void putMessageOnQueue() throws InterruptedException {
        log.info(">>> When: I put a message on the queue");
        jmsTemplate.send("TEST.QUEUE", new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                return session.createTextMessage("!!!test");
            }
        });
        TimeUnit.SECONDS.sleep(3);
    }

    @When("processing is set to fail")
    public void injectProcessingFailure() {
        log.info(">>> When: processing is set to fail");
        ThrowExceptionAspectBean exceptionInjector = (ThrowExceptionAspectBean)ac.getBean("throwExceptionAspectBean");
        exceptionInjector.setExceptionFlag(true);
    }

    @Then("value is stored in database")
    public void checkMessageStoredInDatabase() {
        log.info(">>> Then: value is stored in database");
        assertEquals(1, SimpleJdbcTestUtils.countRowsInTable(new SimpleJdbcTemplate(jdbcTemplate), "message"));
    }

    @Then("no message is left on the queue")
    public void checkNoMessageLeftOnQueue() {
        log.info(">>> Then: no message is left on the queue");
        assertNull(jmsTemplate.receive("TEST.QUEUE"));
        assertNull(jmsTemplate.receive("ActiveMQ.DLQ"));
    }

    @Then("value is not stored in database")
    public void checkMessageNotStoredInDatabase() {
        log.info(">>> Then: value is not stored in database");
        assertEquals(0, SimpleJdbcTestUtils.countRowsInTable(new SimpleJdbcTemplate(jdbcTemplate), "message"));
    }

    @Then("outgoing message is sent")
         public void checkOutgoingMessageSent() {
        log.info(">>> Then: outgoing message is sent");
        assertNotNull(jmsTemplate.receive("OUT.QUEUE"));
    }

    @Then("outgoing message is not sent")
    public void checkOutgoingMessageNotSent() {
        log.info(">>> Then: outgoing message is not sent");
        assertNull(jmsTemplate.receive("OUT.QUEUE"));
    }

    @Then("message lands on DLQ")
    public void checkMessageLandedOnDLQ() {
        log.info(">>> Then: message lands on DLQ");
        assertNotNull(jmsTemplate.receive("ActiveMQ.DLQ"));
    }

    private void startCamel() throws Exception {
        SpringCamelContext springCamelContext = (SpringCamelContext) ac.getBean("camel1");
        springCamelContext.start();
    }

    private void prepareDb() {
        SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(jdbcTemplate), ac, "sql/create.sql", false);
    }

    private void cleanDb() {
        SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(jdbcTemplate), ac, "sql/delete.sql", false);
    }

    private void purgeQueues() {
        log.info("Setting JMS receive timeout to 100ms");
        jmsTemplate.setReceiveTimeout(100);
        while (jmsTemplate.receive("TEST.QUEUE") != null) {
            log.info("Discarded a message from TEST.QUEUE");
        };
        while (jmsTemplate.receive("ActiveMQ.DLQ") != null) {
            log.info("Discarded a message from ActiveMQ.DLQ");
        };
        while (jmsTemplate.receive("OUT.QUEUE") != null) {
            log.info("Discarded a message from OUT.QUEUE");
        };
    }

    private void stopCamel() throws Exception {
        SpringCamelContext springCamelContext = (SpringCamelContext) ac.getBean("camel1");
        springCamelContext.stop();
    }


    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    public void setJmsTemplate(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
