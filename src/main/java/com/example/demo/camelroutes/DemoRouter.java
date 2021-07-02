package com.example.demo.camelroutes;

import com.example.demo.entity.Book;
import com.example.demo.entity.Freundschaftspiel;
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

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private DemoValidatorProcessor demoValidatorProcessor;

    @Autowired
    private DemoCustomerName demoCustomerName;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private DemoFreundschaftsspielValidator demoFreundschaftsspielValidator;

    @Override
    public void afterPropertiesSet() throws Exception {

    }
    public void setDemoService(DemoService demoService) {
        this.demoService = demoService;
    }

    @Override
    public void configure() throws Exception {

        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        jacksonDataFormat.setPrettyPrint(true);
        JacksonXMLDataFormat xmlDataFormat = new JacksonXMLDataFormat();



        /**
         * First Step File Transfer
         */
//        from("file:inbox?noop=true")
//                .routeId("File Transfer Route")
//
//                .log("RouteMessageTransfer on Route + ${routeId} ")
//                .log("Body: ${body}")
//        .to("file:outbox");

//        /**
//         * Second Step Xpath
//         */
//
//        from("file:inbox?noop=true")
//                .routeId("File Transfer Route #2")
//                .log("Body: ${body}")
//                .setHeader("arenaName", xpath("//stadion/name/text()"))
//                .to("file:outbox?fileName=${headers.arenaName}.xml");

        /**
         * Third Step Choice/When
         */

//        from("file:inbox?noop=true")
//                .routeId("File Transfer Route #2")
//                .setHeader("arenaName", xpath("//stadion/name/text()"))
//                .choice()
//                    .when(xpath("//stadion/ueberdacht='false'"))
//                    .log("Stadion ${header.arenaName} hat kein Dach. Wetter sollte nachgeschaut werden")
//                    .to("file:outbox?fileName=${headers.arenaName}.xml")
//                    .otherwise()
//                    .log("Stadion ${header.arenaName} hat ein Dach. Wetter ist unwichtig")



        /**
         * 4. Step Marshalling & Validations
         */

//        from("file:inbox?noop=true")
//                .routeId("File Transfer Route #2")
//                .setHeader("arenaName", xpath("//stadion/name/text()"))
//                .choice()
//                    .when(xpath("//stadion/ueberdacht='false'"))
//                    .log("Stadion ${header.arenaName} hat kein Dach. Wetter sollte nachgeschaut werden")
//                    .unmarshal()
//                    .jacksonxml(Freundschaftspiel.class)
//                .log("Body: ${body}")
//                .process(exchange -> exchange.getIn().getBody())
//                .process(demoFreundschaftsspielValidator) // geht auch mit Methodenangabe (camel hat algorithmus wie es richtige Methode findet)
//                .log("ValidationDate: ${body.validationDate}")
//                .marshal(jacksonDataFormat)
//                .to("file:outbox?fileName=${header.arenaName}.json")
//                 .end();


        /**
         * 4. Step ActiveMQ?
         */

        from("file:inbox?noop=true")
                .routeId("File Transfer Route #2")
                .setHeader("arenaName", xpath("//stadion/name/text()"))
                .choice()
                .when(xpath("//stadion/ueberdacht='false'"))
                .log("Stadion ${header.arenaName} hat kein Dach. Wetter sollte nachgeschaut werden")
                .unmarshal()
                .jacksonxml(Freundschaftspiel.class)
                .log("Body: ${body}")
                .process(exchange -> exchange.getIn().getBody())
                .process(demoFreundschaftsspielValidator) // geht auch mit Methodenangabe (camel hat algorithmus wie es richtige Methode findet)
                .log("ValidationDate: ${body.validationDate}")
                .marshal(jacksonDataFormat)
                .log("Produce to ActiveMQ")
                .to("activemq:wetter")
                .end();



        /**
         * 5. Step ActiveMQ Consumption
         *
         */

//        from("activemq:wetter")
//        .log("Consumed from ActiveMQ: ${body}")
//        .end();


        /**
         * 6. Step Http Componente
         *
         */

        from("activemq:wetter")
        .log("Consumed from ActiveMQ: ${body}")
                .setHeader("stadtName", jsonpath("$.stadt"))
                .to("direct:weather")
        .end();


        from("direct:weather")
                .log("Getting Weather is called")
                .toD("http://www.mapquestapi.com/geocoding/v1/address/?key=GdVZCathGlyA31NJOAATcYAK4Gl2ASW6&location=${header.stadtName}&maxResults=1")
                .to("log:DEBUG?showBody=true")
                .log("Body: ${body}")
                .setHeader("longtitude", jsonpath("$..displayLatLng.lng"))
                .setHeader("latitude", jsonpath("$..displayLatLng.lat"))
                .log("Headers: Longtitude:  ${header.longtitude}, Latitude: ${header.latitude}")
                .log("How is the weather in ${header.stadtName}?")
                .toD("http://api.openweathermap.org/data/2.5/weather?lat=${header.latitude}&lon=${header.longtitude}&units=metric&appid=da6608691938e4a522e1fe5da3e5a175")
                .to("log:DEBUG?showBody=true")
                .log("Body: ${body}")
                .setHeader("DescriptionWeather", jsonpath("$.weather..description"))
                .setHeader("Temp", jsonpath("$.main.temp"))
                .log("Description: ${header.DescriptionWeather} and Temp: ${header.Temp}");


        /**
         * 6. Step Rest Componente
         *
         */
        restConfiguration().component("servlet")
                .host("localhost")
                .port(8080)
                .contextPath("/demo/*")
                .bindingMode(RestBindingMode.auto);


        rest("/api")
                .get("/weather/{stadt}")
                .route()
                .routeId("GetWeather")

                .setHeader("stadtName", simple("${header.stadt}"))

                .log("Getting Weather is called")
                .toD("http://www.mapquestapi.com/geocoding/v1/address/?bridgeEndpoint=true&key=GdVZCathGlyA31NJOAATcYAK4Gl2ASW6&location=${header.stadtName}&maxResults=1")
                .to("log:DEBUG?showBody=true")
                .log("Body: ${body}")
                .setHeader("longtitude", jsonpath("$..displayLatLng.lng"))
                .setHeader("latitude", jsonpath("$..displayLatLng.lat"))
                .log("Headers: Longtitude:  ${header.longtitude}, Latitude: ${header.latitude}")
                .log("How is the weather in ${header.stadtName}?")
                .toD("http://api.openweathermap.org/data/2.5/weather?bridgeEndpoint=true&lat=${header.latitude}&lon=${header.longtitude}&units=metric&appid=da6608691938e4a522e1fe5da3e5a175")
                .to("log:DEBUG?showBody=true")
                .log("Body: ${body}")
                .setHeader("DescriptionWeather", jsonpath("$.weather..description"))
                .setHeader("Temp", jsonpath("$.main.temp"))
                .log("Description: ${header.DescriptionWeather} and Temp: ${header.Temp}")
                .endRest();



//
//                .process(demoValidatorProcessor)
//                .process(exchange -> exchange.getIn().setHeader("headerValue", 1))
//                .choice()
//                .when(simple("${in.header.headerValue} < 5 "))
//                .log("Header value < 5 => ${in.header.headerValue} : ${exchangeId}")
//                .when(xpath("//name='motor'"))
//                .log("Header value > 5 => ${in.header.headerValue} and XPath name = Motor : ${exchangeId} ")
//                .end()
//                .unmarshal()
//                .jacksonxml(Order.class)
//                .bean(demoCustomerName, "setNameAndTimestamp")
//                .choice()
//                .when(simple("${body.name} == 'motor'"))
//                .to("direct:motorRoute")
//                .otherwise()
//                .to("direct:nonMotorRoute")
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
//
//        restConfiguration().component("servlet")
//                .host("localhost")
//                .port(8080)
//                .contextPath("/demo/*")
//                .bindingMode(RestBindingMode.auto);


//        rest("/api")
//                .get("/")
//                .route()
//                .routeId("Get")
//                .to("log:DEBUG?showBody=true&showHeaders=true")
//                .setHeader(Exchange.HTTP_PATH, simple("/posts/5"))
//                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
//                .to("http://jsonplaceholder.typicode.com?bridgeEndpoint=true")
//                .convertBodyTo(String.class)
//                .log("BODY:  ${body}")
//                .to("log:DEBUG?showBody=true")
//                .endRest()

//                .post("/post")
//                .type(Order.class)
//                .route()
//                .routeId("Post")
//                .to("log:DEBUG?showBody=true")
//                .endRest();

//                url -X POST --header "Content-Type: application/json" -d '{"name":"motor", "amount":"5"}'  http://localhost:8080/demo/api/post

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

    public DemoValidatorProcessor getDemoProcessor() {
        return demoValidatorProcessor;
    }

    public void setDemoProcessor(DemoValidatorProcessor demoValidatorProcessor) {
        this.demoValidatorProcessor = demoValidatorProcessor;
    }
}
