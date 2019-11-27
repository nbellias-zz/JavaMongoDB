/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author nmpellias
 */
public class Address implements Serializable {

    private String roadName;
    private int number;
    private String area;
    private int zip;
    private String city;
    private String country;

    public Address() {
    }

    public Address(String roadName, int number, String area, int zip, String city, String country) {
        this.roadName = roadName;
        this.number = number;
        this.area = area;
        this.zip = zip;
        this.city = city;
        this.country = country;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Address{" + "roadName=" + roadName + ", number=" + number + ", area=" + area + ", zip=" + zip + ", city=" + city + ", country=" + country + '}';
    }

}


