package com.example.frank.final_project.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Frank on 2018/4/12.
 */

public abstract class User implements Serializable {

    private String Id;
    private

    public User(int Id, String name, String password, String phone, String email, String security_question, String security_answer){
        this.Id = Id;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.security_question = security_question;
        this.security_answer = security_answer;
//        this.messages = messages;
    }


}
