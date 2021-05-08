package com.example.demo.camelroutes;

import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DemoRouter extends RouteBuilder implements InitializingBean {

  @Autowired
  DemoService demoService;

  @Qualifier
  private String test;


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
    from("file:inbox?noop=true")
           .to("file:outbox");

    from("stream:in?promptMessage=Enter something:")
                            .to("file:data/outbox");
  }
}
