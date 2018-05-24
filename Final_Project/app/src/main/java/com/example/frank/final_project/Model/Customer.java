package com.example.frank.final_project.Model;

import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public class Customer extends User {

    private String Id;
    private String name;
    private String phone;
    private String postalAddress;
    private String contact;

    public Customer(){

    }

    public Customer(String Id, String name, String phone, List<String> chefIds, String postalAddress, String contact) {
        super(Id, name, phone);
        this.postalAddress = postalAddress;
        this.contact = contact;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
