package com.example.demo.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Freundschaftspiel {

    @XmlElement
    private int id;

    @XmlElement
    private String stadt;

    @XmlElement
    private Sportart sportart;

    @XmlElement
    private Stadion stadion;

    private Date validationDate;


    public Freundschaftspiel(int id, String stadt, Sportart sportart, Stadion stadion) {
        this.id = id;
        this.stadt = stadt;
        this.sportart = sportart;
        this.stadion = stadion;
    }

    public Freundschaftspiel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStadt() {
        return stadt;
    }

    public void setStadt(String stadt) {
        this.stadt = stadt;
    }

    public Sportart getSportart() {
        return sportart;
    }

    public void setSportart(Sportart sportart) {
        this.sportart = sportart;
    }

    public Stadion getStadion() {
        return stadion;
    }

    public void setStadion(Stadion stadion) {
        this.stadion = stadion;
    }

    public Date getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(Date validationDate) {
        this.validationDate = validationDate;
    }
}
