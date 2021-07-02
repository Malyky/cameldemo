package com.example.demo.camelroutes;

import com.example.demo.entity.Freundschaftspiel;
import com.example.demo.util.FileReaderUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

@Component
public class DemoFreundschaftsspielValidator implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        Freundschaftspiel body = (Freundschaftspiel) exchange.getIn().getBody();

        body.setValidationDate(new Date());

    }
}
