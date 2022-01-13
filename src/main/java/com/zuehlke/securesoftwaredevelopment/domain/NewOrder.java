package com.zuehlke.securesoftwaredevelopment.domain;


import java.util.Arrays;

public class NewOrder {
    Integer id;
    Integer restaurantId;
    String comment;
    Integer address;
    FoodItem[] items;


    public NewOrder(Integer restaurantId, String comment, Integer address, FoodItem[] items) {
        this.restaurantId = restaurantId;
        this.comment = comment;
        this.items = items;
        this.address = address;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public FoodItem[] getItems() {
        return items;
    }

    public void setItems(FoodItem[] items) {
        this.items = items;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        if (items != null) {
            for (FoodItem foodItem : items) {
                foodItem.setDeliveryId(id);
            }
        }
    }


    @Override
    public String toString() {
        return "NewOrder{" +
                "id=" + id +
                ", restaurantId=" + restaurantId +
                ", comment='" + comment + '\'' +
                ", address=" + address +
                ", items=" + Arrays.toString(items) +
                '}';
    }

    public NewOrder(NewOrder newOrder) {
        this.id = newOrder.id;
        this.restaurantId = newOrder.restaurantId;
        this.comment = newOrder.comment;
        this.address = newOrder.address;
        this.items = newOrder.items;
    }
}