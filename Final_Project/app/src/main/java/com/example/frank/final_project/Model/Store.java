package com.example.frank.final_project.Model;

import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public class Store {

    public enum SaleType {Individual, Retail};

    private String name;
    private float rate;
    private boolean status;
    private SaleType saleType;
    private String address;

    public Store(){

    }

    public Store(String name, float rate, boolean status, SaleType saleType, String address){
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

    public SaleType getSaltType() {
        return saleType;
    }

    public void setSaltType(SaleType saleType) {
        this.saleType = saleType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
