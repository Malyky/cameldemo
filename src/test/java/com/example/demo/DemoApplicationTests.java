package com.example.demo;

import com.example.demo.camelroutes.DemoRouter;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.apache.camel.test.junit4.TestSupport.deleteDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = DemoApplication.class,
        // turn on web during test on the defined 8080 port
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
// re-create Spring/Camel for each test
@DirtiesContext
class DemoApplicationTests  {

    @Autowired
    CamelContext camelContext;

    @Autowired
    ProducerTemplate producerTemplate;


    @Before
    public void setUp() throws Exception {
        // delete directories so we have a clean start
        deleteDirectory("target/inbox");
        deleteDirectory("target/outbox");
    }
/*
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new DemoRouter();
    }*/


    @Test
    public void test(){
        String t = "test";
        assertEquals(t, "test");
    }

    @Test
    public void camelRouteTest() throws InterruptedException {
        String t = "test";
        assertEquals(t, "test");

        NotifyBuilder notifyBuilder = new NotifyBuilder(camelContext).whenDone(1).create();
        producerTemplate.sendBodyAndHeader("file://target/inbox", "Hello World", Exchange.FILE_NAME, "hello.txt");

        Assertions.assertTrue(notifyBuilder.matchesWaitTime());

        File target = new File("target/outbox/hello.txt");

        assertTrue("File is not here", target.exists());
    }

  /*  @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new AnnotationConfigApplicationContext(DemoApplication.class);
    }*/
}
