package com.example.frank.final_project.Model;

import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public class Store {

    public enum SaleType {Individual, Retail};

    private String name;
    private int rank;
    private boolean status;
    private SaleType saleType;
    private String address;
    private List<Cake> menu;
    private List<Order> orders;

    public Store(){

    }

    public Store(String name, int rank, boolean status, SaleType saleType, String address, List<Cake> menu, List<Order> orders){
        this.name = name;
        this.rank = rank;
        this.status = status;
        this.saleType = saleType;
        this.address = address;
        this.menu = menu;
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
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

    public List<Cake> getMenu() {
        return menu;
    }

    public void setMenu(List<Cake> menu) {
        this.menu = menu;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
