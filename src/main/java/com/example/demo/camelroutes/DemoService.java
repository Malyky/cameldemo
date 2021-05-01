package com.example.demo.camelroutes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    public void helloRouting(int i){
        System.out.println("DemoRouter " + i);
    }
}
