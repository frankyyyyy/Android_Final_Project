package com.example.frank.final_project.Model;

import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public class Customer extends User {

    private String Id;
    private String name;
    private String phone;
    private List<Order> orders;
    private String postalAddress;

    public Customer(){

    }

    public Customer(String Id, String name, String phone, List<String> chefIds, List<Order> orders, String postalAddress) {
        super(Id, name, phone);
        this.orders = orders;
        this.postalAddress = postalAddress;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
