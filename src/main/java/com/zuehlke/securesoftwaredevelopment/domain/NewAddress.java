package com.zuehlke.securesoftwaredevelopment.domain;

public class NewAddress {
    private String name;
    private int userId;
    private Integer id;

    public NewAddress(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "NewAddress{" +
                "name='" + name + '\'' +
                ", userId=" + userId +
                ", id=" + id +
                '}';
    }
}
