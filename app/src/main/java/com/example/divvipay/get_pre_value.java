package com.divvipay.app;

public  class get_pre_value {

       String money;
       String name;

       private get_pre_value(){}

    public get_pre_value (String name , String money )
    {
        this.money=money;
        this.name=name;
    }

    public String getMoney() {
        return money;
    }

    public String getName() {
        return name;
    }



}
