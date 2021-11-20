package com.example.demo.camel.route;

import com.example.demo.camel.processor.DemoFreundschaftsspielValidator;
import com.example.demo.camel.processor.DemoValidatorProcessor;
import com.example.demo.entity.Book;
import com.example.demo.entity.Konzert;
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
import org.springframework.stereotype.Component;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;

@Component
public class DemoRoute extends RouteBuilder implements InitializingBean, CamelContextAware {

    JacksonXMLDataFormat xmlDataFormat = new JacksonXMLDataFormat();
    JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private DemoValidatorProcessor demoValidatorProcessor;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private DemoFreundschaftsspielValidator demoFreundschaftsspielValidator;

    @Override
    public void afterPropertiesSet() throws Exception {
        jacksonDataFormat.setPrettyPrint(true);
    }

    @Override
    public void configure() throws Exception {


        /**
         * First Step File Transfer
         */
    /*    from("file:inbox?noop=true")
            .routeId("File Transfer Route")
            .log("RouteMessageTransfer on Route + ${routeId} ")
            .log("Body: ${body}")
        .to("file:outbox");*/

//        /**
//         * Second Step Xpath
//         */
//
/*        from("file:inbox?noop=true")
            .routeId("File Transfer Route #2")
            .log("Body: ${body}")
            .setHeader("arenaName", xpath("//stadion/name/text()"))
            .to("file:outbox?fileName=${headers.arenaName}.xml");
        */

        /**
         * Third Step Choice/When
         */

  /*      from("file:inbox?noop=true")
            .routeId("File Transfer Route #2")
            .log("Body: ${body}")
            .setHeader("arenaName", xpath("//stadion/name/text()"))
            .choice()
                .when(xpath("//stadion/ueberdacht='false'"))
                    .log("Stadion ${header.arenaName} hat kein Dach.")
                    .to("file:outbox?fileName=${headers.arenaName}.xml")
                .otherwise()
                    .log("Stadion ${header.arenaName} hat KEIN Dach.");*/

/*        from("file:inbox?noop=true")
            .routeId("File Transfer Route #2")
            .setHeader("arenaName", xpath("//stadion/name/text()"))
            .choice()
                .when(xpath("//stadion/ueberdacht='false'"))
                    .log("Stadion ${header.arenaName} hat kein Dach.")
                    .unmarshal().jacksonxml(Konzert.class)
                    .process(demoFreundschaftsspielValidator)
                    .marshal().json()
                    .log("Staedt: ${body}")
                    .to(activemq("staedte"))
            //.to("file:outbox?fileName=${headers.arenaName}.json")
                .otherwise()
                    .log("Stadion ${header.arenaName} hat KEIN Dach.");*/

        /**
         * 4. Step Marshalling & Validations
         */
/*        from("file:inbox?noop=true")
                .routeId("File Transfer Route #4")
                .setHeader("arenaName", xpath("//stadion/name/text()"))
                .choice()
                    .when(xpath("//stadion/ueberdacht='false'"))
                        .log("Stadion ${header.arenaName} hat kein Dach. Wetter sollte nachgeschaut werden")
                        .unmarshal().jacksonxml(Konzert.class)
                        .log("Body: ${body}")
                        .process(demoFreundschaftsspielValidator) // geht auch mit Methodenangabe (camel hat algorithmus wie es richtige Methode findet)
                        .log("ValidationDate: ${body.validationDate}")
                        .marshal(jacksonDataFormat)
                        .to("file:outbox?fileName=${header.arenaName}.json")
                .end();*/

        /**
         * 4. Step ActiveMQ?
         */

        from(file("inbox?noop=true"))
            .routeId("File Transfer Route #2")
            .setHeader("arenaName", xpath("//stadion/name/text()"))
            .choice()
                .when(xpath("//stadion/ueberdacht='false'"))
                    .log("Stadion ${header.arenaName} hat kein Dach. Wetter sollte nachgeschaut werden")
                    .unmarshal()
                    .jacksonxml(Konzert.class)
                    .log("Body: ${body}")
                    .process(demoFreundschaftsspielValidator) // geht auch mit Methodenangabe (camel hat algorithmus wie es richtige Methode findet)
                    .log("ValidationDate: ${body.validationDate}")
                    .marshal(jacksonDataFormat)
                    .log("Produce to ActiveMQ")
                    .to("activemq:staedte")
            .end();



        /**
         * 5. Step ActiveMQ Consumption
         *
         */

/*        from("activemq:staedte")
        .log("Consumed from ActiveMQ: ${body}")
        .end();*/


        /**
         * 6. Step Http Componente
         *
         */

        from(activemq("staedte"))
            .routeId("ActiveMQ Staedte Consumption")
            .log("Consumed from ActiveMQ: ${body}")
            .setHeader("stadtName", jsonpath("$.stadt"))
            .to(direct("weather"))
        .end();
//
//
        from(direct("weather"))
            .routeId("Weather")
            .log("Getting Weather is called with body ${header.stadtName} and ${body}")
            .toD(http("www.mapquestapi.com/geocoding/v1/address/?key=GdVZCathGlyA31NJOAATcYAK4Gl2ASW6&location=${header.stadtName}&maxResults=1"))
            .convertBodyTo(String.class)
            .setHeader("longtitude", jsonpath("$..latLng.lng"))
            .setHeader("latitude", jsonpath("$..displayLatLng.lat"))
            .log("Headers: Longtitude:  ${header.longtitude}, Latitude: ${header.latitude}")
            .log("How is the weather in ${header.stadtName}?")
            .toD(http("api.openweathermap.org/data/2.5/weather?lat=${header.latitude}&lon=${header.longtitude}&units=metric&appid=da6608691938e4a522e1fe5da3e5a175"))
            .to("log:DEBUG?showBody=true")
            .log("Body: ${body}")
            .setHeader("DescriptionWeather", jsonpath("$.weather..description"))
            .setHeader("Temp", jsonpath("$.main.temp"))
            .log("Description: ${header.DescriptionWeather} and Temp: ${header.Temp}");
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
