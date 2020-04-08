package com.divvipay.app;

import java.util.ArrayList;

public class item {

    private  int id;
    private  String name;
    private String amount;
    private  String note;
    private  String time;



    public item(int id, String name, String amount, String note) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.note = note;

    }

    public item(String note, String amount, String name,String time){
        this.name = name;
        this.amount = amount;
        this.note = note;
        this.time=time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}