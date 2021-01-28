package org.acme.jms;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.acme.jms.support.ArtemisTestResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jms.*;

/**
 * @author leandrosoares
 */
@QuarkusTest
@QuarkusTestResource(ArtemisTestResource.class)
public class QuarkusExampleTest {

    @Inject
    ConnectionFactory connectionFactory;

    private static final Logger logger = LoggerFactory.getLogger(QuarkusExampleTest.class);
    private static final String QUEUE_NAME = "QUARKUS";


    @Test
    @Order(3)
    public void firstConsumeThenProduce()  {
        boolean result = false;
        try {
            consume();
            produce();
            result = true;
        } catch (Exception e) {
            logger.error("Error: {}",e);
        }
        Assertions.assertTrue(result);

    }

    @Test
    @Order(4)
    public void firstProduceThenConsume()  {
        boolean result = false;
        try {
            produce();
            consume();
            result = true;
        } catch (Exception e) {
            logger.error("Error: {}",e);
        }
        Assertions.assertTrue(result);

    }


    private void produce() throws Exception {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            context.createProducer().send(context.createQueue(QUEUE_NAME), "Hello world from Quarkus!");
        }
    }

    private void consume() throws Exception {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue(QUEUE_NAME));
            Message message = consumer.receiveNoWait();

            if (message == null) {
                logger.warn("No message found...That's OK: probably running consumer before producer");
            } else {
                logger.warn(message.getBody(String.class));
            }
        }
    }

}
