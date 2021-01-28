# Quarkus Qpid JMS Quickstart Problem replication

This project was created over the "Quarkus Qpid JMS Quickstart" available on
git clone https://github.com/amqphub/quarkus-qpid-jms-quickstart.git

Its purpose is to show how we're obliged to create a queue before producing
messages to it.

## Symptom
If a message is sent to a queue that does not exist on the JMS Broker, that the address becomes 
registered as MULTICAST, and as soon as someone tries to create e queue consumer over it, 
the following error arises:

`org.apache.qpid.jms.provider.ProviderException: Address *<queue name>* is not configured for queue support [condition = amqp:illegal-state]`

The following source code is available:

### Junit Tests

There are 2 classes for tests:

1) **PureQpidTest** that relies on creating a Jms Qpid Connection Factory manually.


2) **QuarkusExampleTest** that relies on following the Quarkus example.

Each class has a the following two tests: 
1) **firstConsumeThenProduce** method is for creating a consumer and then a producer.
   

2) **firstProduceThenConsume** method is for creating first the producer and then the consumer


QuarkusExampleTest.firstProduceThenConsume is the test that fails and shows the problem.


### The example source code (not used by junit tests)

There's no need to mess with this code, as junit tests are self explaining.

**Changes made**:

**PriceConsumer** and **PriceProducer** class were changed to no startup within the application
**PriceConsumer** was changed so it has a delay of 5seconds to start, so problem can arise.



### Embedded Artemis
There's an embedded artemis shipped so tests can be executed. 
So if you stick with JUnit, the source code, maven and Java is all you need. 

For experimenting the example source code, please use an external Artemis.
