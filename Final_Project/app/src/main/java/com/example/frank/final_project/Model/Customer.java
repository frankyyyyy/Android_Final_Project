package com.example.frank.final_project.Model;

import android.net.Uri;

import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public class Customer extends User {

    private String id;
    private String name;
    private String phone;
    private String headPhotoUri;
    private String postalAddress;
    private Contact contact;

    public Customer(){

    }

    public Customer(String id, String name, String phone, String headPhotoUri, String postalAddress, Contact contact) {
        super(id, name, phone, headPhotoUri);
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
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getHeadPhotoUri() {
        return headPhotoUri;
    }

    public void setHeadPhotoUri(String headPhotoUri) {
        this.headPhotoUri = headPhotoUri;
    }
}
