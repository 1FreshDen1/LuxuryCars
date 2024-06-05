package com.example.coursework;

import java.util.List;

public class Order {
    private String orderId;
    private int orderNumber;
    private List<CarAd> carAds;

    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order(String orderId, int orderNumber, List<CarAd> carAds) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.carAds = carAds;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<CarAd> getCarAds() {
        return carAds;
    }

    public void setCarAds(List<CarAd> carAds) {
        this.carAds = carAds;
    }
}
