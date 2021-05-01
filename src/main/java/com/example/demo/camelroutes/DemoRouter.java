package com.example.demo.camelroutes;

import org.apache.camel.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DemoRouter {

  @Autowired
  DemoService demoService;

  public DemoRouter(DemoService demoService) {
    this.demoService = demoService;
  }

  public void runService(){
    demoService.helloRouting(5);
  }

  public DemoService getDemoService() {
    return demoService;
  }

  public void setDemoService(DemoService demoService) {
    this.demoService = demoService;
  }
}
