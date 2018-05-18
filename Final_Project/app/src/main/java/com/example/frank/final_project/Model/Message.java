package com.example.frank.final_project.Model;

/**
 * Created by Frank on 2018/4/12.
 */

public class Message {

    private String sender;
    private String time;
    private String content;
    public Message(String sender, String time, String content){
        this.sender = sender;
        this.time = time;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
