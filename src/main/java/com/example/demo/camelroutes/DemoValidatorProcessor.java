package com.example.demo.camelroutes;

import com.example.demo.util.FileReaderUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DemoValidatorProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        GenericFile body = (GenericFile) exchange.getIn().getBody();

        File file = (File) body.getFile();
        if (file.exists()) {
            exchange.getIn().setHeader("bodyInHeader", FileReaderUtil.readFile(file));
        } else
        {
            // throw exception // done later
        }

    }
}
