package com.example.demo;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
//@SpringBootApplication
@SpringBootApplication(exclude = ActiveMQAutoConfiguration.class)
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        System.getProperty("java.class.path");
        SpringApplication.run(DemoApplication.class, args);
//        CamelContext context = new DefaultCamelContext();
//        context.addRoutes(new RouteBuilder() {
//                public void configure() {
//                 //from("file:inbox?noop=true")
//                  //       .to("file:outbox");
//
//
//                    from("stream:in?promptMessage=Enter something:")
//                            .to("file:data/outbox");
//                }
//
//
//        });
//        context.start();
//        Thread.sleep(10000);
//        context.stop();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }

   /* @Value("${cameldemo.api.path}")
    String customContextPath;
*/
//    @Bean
//    ServletRegistrationBean servletRegistrationBean() {
//        ServletRegistrationBean servlet = new ServletRegistrationBean
//                (new CamelHttpTransportServlet(), contextPath+"/*");
//        servlet.setName("CamelServlet");
//        return servlet;
//    }

}
