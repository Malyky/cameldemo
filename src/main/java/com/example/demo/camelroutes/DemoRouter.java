package com.example.demo.camelroutes;

import com.example.demo.entity.Order;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DemoRouter extends RouteBuilder implements InitializingBean, CamelContextAware {

  @Autowired
  DemoService demoService;

  @Qualifier
  private String test;

  @Value("${random.int(11)}")
  private int randomNumber;

  @Autowired
  private CamelContext camelContext;

  @Autowired
  private DemoValidatorProcessor demoValidatorProcessor;

  @Autowired
  private DemoCustomerName demoCustomerName;

  public void runService(){
    demoService.helloRouting(5);
  }

  @Override
  public void afterPropertiesSet() throws Exception {

  }

  public DemoService getDemoService() {
    return demoService;
  }

  public void setDemoService(DemoService demoService) {
    this.demoService = demoService;
  }

  @Override
  public void configure() throws Exception {

    JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
    jacksonDataFormat.setPrettyPrint(true);
    jacksonDataFormat.enableFeature(SerializationFeature.WRAP_ROOT_VALUE);


    from("file:inbox?noop=true")
            .routeId("MessageID" + randomNumber)
            .log("RouteMessegaTransfer + RouteId ")
            .process(demoValidatorProcessor)
            .process(exchange -> exchange.getIn().setHeader("headerValue", randomNumber))
            .choice()
              .when(simple("${in.header.headerValue} < 5 "))
                .log("Header value < 5 => ${in.header.headerValue} : ${exchangeId}")
              .when(xpath("//name='motor'"))
                .log("Header value > 5 => ${in.header.headerValue} and XPath name = Motor : ${exchangeId} ")
            .end()
            .unmarshal()
            .jacksonxml(Order.class)
            .bean(demoCustomerName, "setNameAndTimestamp")
            .choice()
              .when(simple("${body.name} == 'motor'"))
                .to("direct:motorRoute")
              .otherwise()
                .to("direct:nonMotorRoute")
             ;

    from("direct:motorRoute")
            .log("This is Motor Route")
            .log("Body does not contain Motor, but is ${body.name} ")
            .marshal(jacksonDataFormat)
            .to("file:outbox?fileName=${exchange.fromRouteId}.json");

    from("direct:nonMotorRoute")
            .log("This is nonMotor Route")
            .log("Body does not contain Motor, but is ${body.name} ")
            .marshal(jacksonDataFormat)
            .to("file:outbox?fileName=${exchange.fromRouteId}.json");

//     from("file://target/inbox")
//                  .to("file://target/outbox");

//    from("stream:in?promptMessage=Enter something:")
//            .log("StreamTest")
//            .process(exchange -> exchange.getIn())
//                            .to("file:data/outbox?fileName=${body}");

    //getCamelContext().addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));

//
    from("file:outbox?noop=true")
            .log("Produce to ActiveMQ ${id}")
            .to("activemq:bestellung");

    from("activemq:bestellung")
            .log("Consume from ActiveMQ ${id}");
  }

  @Override
  public CamelContext getCamelContext() {
    return camelContext;
  }

  @Override
  public void setCamelContext(CamelContext camelContext) {
    this.camelContext = camelContext;
  }

  public int getRandomNumber() {
    return randomNumber;
  }

  public void setRandomNumber(int randomNumber) {
    this.randomNumber = randomNumber;
  }

  public DemoValidatorProcessor getDemoProcessor() {
    return demoValidatorProcessor;
  }

  public void setDemoProcessor(DemoValidatorProcessor demoValidatorProcessor) {
    this.demoValidatorProcessor = demoValidatorProcessor;
  }
}
