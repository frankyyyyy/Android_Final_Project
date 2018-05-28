package com.example.frank.final_project.Model;

/**
 * Created by Frank on 2018/4/12.
 */

public class Message {

    public enum Status {Read, UnRead}

    private String senderId;
    private String sender;
    private String time;
    private String content;
    private String status;

    public Message(){

    }

    public Message(String senderId, String sender, String time, String content, String status){
        this.senderId = senderId;
        this.sender = sender;
        this.time = time;
        this.content = content;
        this.status = status;
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

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
