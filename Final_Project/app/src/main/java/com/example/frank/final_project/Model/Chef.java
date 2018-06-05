package com.example.frank.final_project.Model;

/**
 * Created by Frank on 2018/4/12.
 */

public class Chef extends User {

    private String id;
    private String name;
    private String phone;
    private String headPhotoUri;
    private boolean storeStatus;
    private Store store;
    private String certificateUri;
    private Contact contact;

    public Chef(){

    }

    public Chef(String id, String name, String phone, String headPhotoUri, boolean storeStatus, Store store, String certificateUri, Contact contact) {
        super(id, name, phone, headPhotoUri);
        this.storeStatus = storeStatus;
        this.store = store;
        this.certificateUri = certificateUri;
        this.contact = contact;
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

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getCertificateUri() {
        return certificateUri;
    }

    public void setCertificateUri(String certificateUri) {
        this.certificateUri = certificateUri;
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

    public boolean getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(boolean storeStatus) {
        this.storeStatus = storeStatus;
    }
}
