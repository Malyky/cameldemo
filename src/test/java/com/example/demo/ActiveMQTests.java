package com.example.demo;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.apache.camel.test.junit4.TestSupport.deleteDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = DemoApplication.class,
        // turn on web during test on the defined 8080 port
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
// re-create Spring/Camel for each test
@MockEndpoints("output")
@DirtiesContext
class ActiveMQTests {

    @Autowired
    public CamelContext context;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    Environment environment;

    @EndpointInject(value = "mock:output")
    MockEndpoint mock;

    @BeforeEach
    public void setup() throws Exception {
            // delete directories so we have a clean start
            deleteDirectory("target/inbox");
            deleteDirectory("target/outbox");
          //  createRouteBuilder();
        }

        protected RouteBuilder createRouteBuilder() throws Exception {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("file://target/inbox?noop=true").to("mock:activemq");
                }
            };
        }


    @TestConfiguration
    static class Config {
        @Bean
        CamelContextConfiguration contextConfiguration() {
            return new CamelContextConfiguration() {
                @Override
                public void beforeApplicationStart(CamelContext camelContext) {
                    // configure Camel here
                }

                @Override
                public void afterApplicationStart(CamelContext camelContext) {
                    // Start your manual routes here

                }
            };
        }

        @Bean
        RouteBuilder routeBuilder() {
            return new RouteBuilder() {
                @Override
                public void configure() {
                    from("direct:start").to("mock:done");
                }
            };
        }

        // further beans ...
    }

    @Produce(value = "direct:start")
    private ProducerTemplate template;
    @EndpointInject(value = "mock:done")
    private MockEndpoint mockDone;

    @Test
    public void testCamelRoute() throws Exception {
        mockDone.expectedMessageCount(1);

        Map<String, Object> headers = new HashMap<>();
    //https://stackoverflow.com/questions/42275732/spring-boot-apache-camel-routes-testing
        template.sendBodyAndHeaders("test", headers);

        mockDone.assertIsSatisfied();
    }


        @Test
        public void activeMQtest() throws InterruptedException {
            String t = "test";
            assertEquals(t, "test");
        mock.expectedMessageCount(1);

        //        NotifyBuilder notifyBuilder = new NotifyBuilder(camelContext).whenDone(1).create();
        producerTemplate.sendBodyAndHeader("file://target/inbox", "Hello World", Exchange.FILE_NAME, "hello.txt");

        Thread.sleep(2000);
//        Assertions.assertTrue(notifyBuilder.matchesWaitTime());

        mock.assertIsSatisfied();
        
    }


    public void setContext(CamelContext context) {
        this.context = context;
    }
}
