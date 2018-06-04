package com.example.frank.final_project.Model;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public abstract class User implements Serializable {

    public enum Role {CHEF, CUSTOMER}

    private String id;
    private String name;
    private String phone;
    private String headPhotoUri;

    public User(){

    }

    public User(String id, String name, String phone, String headPhotoUri){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.headPhotoUri = headPhotoUri;
    }
}
