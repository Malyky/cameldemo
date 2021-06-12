package com.example.demo.camelroutes;


import com.example.demo.entity.Order;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@Component
public class DemoCustomerName {

    public String shouldNotCallThis(Exchange exchange) throws Exception {

       throw new Exception("Should not be called");
    }
    public void xmlToJson(Exchange exchange) throws IOException, SAXException, ParserConfigurationException {

       Order order = (Order) exchange.getIn().getBody();

       order.setCustomerName("Consol");
    }
}
