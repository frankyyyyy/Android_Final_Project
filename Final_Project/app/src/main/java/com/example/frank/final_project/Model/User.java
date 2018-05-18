package com.example.frank.final_project.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public abstract class User implements Serializable {

    public enum role{CHEF, CUSTOMER};

    private String Id;
    private String name;
    private String phone;

    public User(){

    }

    public User(String Id, String name, String phone){
        this.Id = Id;
        this.name = name;
        this.phone = phone;
    }
}
