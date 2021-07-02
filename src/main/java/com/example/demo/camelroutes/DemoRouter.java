package com.example.demo.camelroutes;

import com.example.demo.entity.Book;
import com.example.demo.entity.Order;
import com.example.demo.entity.StarwarsPeople;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
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

    @Autowired
    private ProducerTemplate producerTemplate;

    public void runService() {
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
        //jacksonDataFormat.enableFeature();

        JacksonXMLDataFormat xmlDataFormat = new JacksonXMLDataFormat();


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
                .to("file:outbox?fileName=${exchange.fromRouteId}_${id}.json");

        from("direct:nonMotorRoute")
                .log("This is nonMotor Route")
                .log("Body does not contain Motor, but is ${body.name} ")
                .marshal(jacksonDataFormat)
                .to("file:outbox?fileName=${exchange.fromRouteId}_${id}.json");

//     from("file://target/inbox")
//                  .to("file://target/outbox");

//    from("stream:in?promptMessage=Enter something:")
//            .log("StreamTest")
//            .process(exchange -> exchange.getIn())
//                            .to("file:data/outbox?fileName=${body}");

        //getCamelContext().addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));

        from("direct:in")
                .log("test")
                .to("stream:out");

        restConfiguration().component("servlet")
                .host("localhost")
                .port(8080)
                .contextPath("/demo/*")
                .bindingMode(RestBindingMode.auto);


        rest("/api")
                .get("/")
                .route()
                .routeId("Get")
                .to("log:DEBUG?showBody=true&showHeaders=true")
                .setHeader(Exchange.HTTP_PATH, simple("/posts/5"))
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .to("http://jsonplaceholder.typicode.com?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .log("BODY:  ${body}")
                .to("log:DEBUG?showBody=true")
                .endRest()

                .post("/post")
                .type(Order.class)
                .route()
                .routeId("Post")
                .to("log:DEBUG?showBody=true")
                .endRest();

                //url -X POST --header "Content-Type: application/json" -d '{"name":"motor", "amount":"5"}'  http://localhost:8080/demo/api/post

//                .get("/starwars/films")
//               // .type(StarwarsPeople.class)
//                .route()
//                .routeId("GetFilms")
//                .setHeader(Exchange.HTTP_PATH, simple("/people/1"))
//                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
//                .to("http://swapi.dev/api?bridgeEndpoint=true")
//                .convertBodyTo(String.class) // convert body to String, to help Jackson serialise https://stackoverflow.com/questions/48798907/jsonmappingexception-with-apache-camel/48801071
//                .unmarshal(jacksonDataFormat)
//                .convertBodyTo(StarwarsPeople.class)
//                //   .log("${body}")
//                .to("log:DEBUG?showBody=true")
//        .endRest();

        from("direct:usehttp")
                .log("Use Http")
                .setHeader(Exchange.HTTP_PATH, simple("/posts/5"))
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .to("http://jsonplaceholder.typicode.com")
                .convertBodyTo(String.class)
                .log("BODY:  ${body}")
                .to("log:DEBUG?showBody=true");

        from("direct:httpWithHeader")
                .log("Use Http")
              //  .setHeader(Exchange.HTTP_PATH, simple("/comments?postId=2"))
                .setHeader(Exchange.HTTP_PATH, simple("/posts/${headers.postId}"))
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .to("http://jsonplaceholder.typicode.com")
                .unmarshal().json(JsonLibrary.Jackson, Book.class)
                //.convertBodyTo(String.class)
                //.unmarshal(jacksonDataFormat)
               // .jacksonxml()
                .log("BODY:  ${body}")
                .to("log:DEBUG?showBody=true");


        from("direct:weather")
                .log("Weather is called")
                .toD("http://www.mapquestapi.com/geocoding/v1/address/?key=GdVZCathGlyA31NJOAATcYAK4Gl2ASW6&location=${header.city}&maxResults=1")
                .to("log:DEBUG?showBody=true")
                .log("Body: ${body}")
                .setHeader("longtitude", jsonpath("$..displayLatLng.lng"))
                .setHeader("latitude", jsonpath("$..displayLatLng.lat"))
                .log("Headers: Longtitude:  ${header.longtitude}, Latitude: ${header.latitude}")
                .log("How is the weather in ${header.city}?")
                .toD("http://api.openweathermap.org/data/2.5/weather?lat=${header.latitude}&lon=${header.longtitude}&units=metric&appid=da6608691938e4a522e1fe5da3e5a175")
                .to("log:DEBUG?showBody=true")
                .log("Body: ${body}")
                .setHeader("DescriptionWeather", jsonpath("$.weather..description"))
                .setHeader("Temp", jsonpath("$.main.temp"))
                .log("Description: ${header.DescriptionWeather} and Temp: ${header.Temp}");







//
//    from("file:outbox?noop=true")
        //             .routeId("ActiveMQProducer")
//            .log("Produce to ActiveMQ ${id}")
//            .to("activemq:bestellung");
//
//    from("activemq:bestellung")
        //         .routeId("ActiveMQConsumer")
//            .log("Consume from ActiveMQ ${id}");
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
