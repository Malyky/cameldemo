package com.example.demo.camelroutes;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoBean {

    @Qualifier
    private String testo;

    public DemoBean() {
    }

    public DemoBean(String testo) {
        this.testo = testo;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}
