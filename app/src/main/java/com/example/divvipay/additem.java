package com.divvipay.app;

public class additem {
    String name;
    String date;
    String group_id;
    String amount;

    public additem(String name, String date,String group_id,String amount) {
        this.name = name;
        this.date = date;
        this.group_id=group_id;
        this.amount=amount;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
