package com.example.frank.final_project.Model;

import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public class Chef extends User {

    private String Id;
    private String name;
    private String phone;
    private Store store;
    private String certificateUrl;
    private Contact contact;

    public Chef(){

    }

    public Chef(String Id, String name, String phone, Store store, String certificateUrl, Contact contact) {
        super(Id, name, phone);
        this.store = store;
        this.certificateUrl = certificateUrl;
        this.contact = contact;
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

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
