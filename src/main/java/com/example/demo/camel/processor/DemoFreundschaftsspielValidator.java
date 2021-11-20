package com.example.demo.camel.processor;

import com.example.demo.entity.Konzert;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DemoFreundschaftsspielValidator implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        Konzert body = (Konzert) exchange.getIn().getBody();

        body.setValidationDate(new Date());

    }
}
