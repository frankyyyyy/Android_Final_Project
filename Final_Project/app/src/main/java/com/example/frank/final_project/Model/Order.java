package com.example.frank.final_project.Model;

import java.util.List;

/**
 * Created by Frank on 2018/5/6.
 */

public class Order {

    private int id;
    private String customerId;
    private String storeId;
    private CompleteStatus completeStatus;
    private List<Cake> cakes;
    private DeliveryMethod deliveryMethod;

    public CompleteStatus getCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(CompleteStatus completeStatus) {
        this.completeStatus = completeStatus;
    }

    private enum CompleteStatus{
        MAKING, DElIVERING, COMPLETED
    }

    private enum DeliveryMethod {
        PICKUP, DELIVERY
    }

    public Order(int id, String customerId, String storeId, List<Cake> cakes, CompleteStatus completeStatus, DeliveryMethod deliveryMethod){
        this.id = id;
        this.customerId = customerId;
        this.storeId = storeId;
        this.cakes = cakes;
        this.completeStatus = completeStatus;
        this.deliveryMethod = deliveryMethod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public List<Cake> getCakes() {
        return cakes;
    }

    public void setCakes(List<Cake> cakes) {
        this.cakes = cakes;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

}
