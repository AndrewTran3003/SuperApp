package com.example.andrewtran.superapp.models;


public class Talk {
private String msg;
private String usr;
private Talk(){

}

    public Talk(String msg, String usr) {
        this.msg = msg;
        this.usr = usr;
    }

    public String getMsg() {
        return msg;
    }

    public String getUsr() {
        return usr;
    }
}
