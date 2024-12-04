package com.example.salesbuddy.model;

import java.io.Serializable;

public class ItemSale implements Serializable {
    private String description;

    public ItemSale(){}
    public ItemSale(String desc){
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
