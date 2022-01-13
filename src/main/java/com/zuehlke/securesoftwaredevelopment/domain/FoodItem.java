package com.zuehlke.securesoftwaredevelopment.domain;

public class FoodItem {
    private Integer id;
    private Integer foodId;
    private Integer amount;
    private Integer deliveryId;

    public FoodItem(Integer amount, Integer foodId) {
        this.amount = amount;
        this.foodId = foodId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public Integer getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + id +
                ", foodId=" + foodId +
                ", amount=" + amount +
                ", deliveryId=" + deliveryId +
                '}';
    }
}
