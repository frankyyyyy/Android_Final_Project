package com.example.frank.final_project.Model;

import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public class Store {

    public enum SaleType {Individual, Retail}

    private String name;
    private float rate;
    private boolean status;
    private String saleType;
    private String address;

    public Store(){

    }

    public Store(String name, float rate, boolean status, String saleType, String address){
        this.name = name;
        this.rate = rate;
        this.status = status;
        this.saleType = saleType;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rank) {
        this.rate = rate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getSaltType() {
        return saleType;
    }

    public void setSaltType(String saleType) {
        this.saleType = saleType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
