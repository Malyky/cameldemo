package com.example.demo.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Konzert {

    @XmlElement
    private int id;

    @XmlElement
    private String stadt;

    @XmlElement
    private String kuenstler;

    @XmlElement
    private Stadion stadion;

    private Date validationDate;


    public Konzert(int id, String stadt, String kuenstler, Stadion stadion) {
        this.id = id;
        this.stadt = stadt;
        this.kuenstler = kuenstler;
        this.stadion = stadion;
    }

    public Konzert() {
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

    public String getKuenstler() {
        return kuenstler;
    }

    public void setKuenstler(String kuenstler) {
        this.kuenstler = kuenstler;
    }

    @Override
    public String toString() {
        return "Konzert{" +
            "id=" + id +
            ", stadt='" + stadt + '\'' +
            ", kuenstler='" + kuenstler + '\'' +
            ", stadion=" + stadion +
            ", validationDate=" + validationDate +
            '}';
    }
}
