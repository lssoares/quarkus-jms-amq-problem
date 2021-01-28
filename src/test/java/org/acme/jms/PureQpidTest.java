package org.acme.jms;

/**
 * @author leandrosoares
 */

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.acme.jms.support.ArtemisTestResource;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

@QuarkusTest
@QuarkusTestResource(ArtemisTestResource.class)
public class PureQpidTest {

    private static final Logger logger = LoggerFactory.getLogger(PureQpidTest.class);
    private static final String QUEUE_NAME = "QPID";

    @ConfigProperty(name = "quarkus.qpid-jms.url")
    String jmsUrl;

    @Test
    @Order(1)
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
    @Order(2)
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
        final JmsConnectionFactory factory = new JmsConnectionFactory(jmsUrl);

        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession();
        Queue queue = session.createQueue(QUEUE_NAME);


        MessageProducer messageProducer = session.createProducer(queue);

        TextMessage message = session.createTextMessage("Hello world from Qpid!");
        messageProducer.send(message);

        connection.close();

    }

    private void consume() throws Exception {
        final JmsConnectionFactory factory = new JmsConnectionFactory("amqp://localhost:5672");

        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession();
        Queue queue = session.createQueue(QUEUE_NAME);

        MessageConsumer messageConsumer = session.createConsumer(queue);
        TextMessage message = (TextMessage)messageConsumer.receiveNoWait();
        if (message == null) {
            logger.warn("No message found...That's OK: probably running consumer before producer");
        } else {
            logger.warn(message.getText());
        }

        connection.close();
    }
}
