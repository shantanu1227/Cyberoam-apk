package com.cyberoamclient;

import java.util.ArrayList;

/**
 * Created by Shantanu on 7/1/13.
 */
public class ItemList {
    ArrayList<String> status = new ArrayList<String>();
    ArrayList<String> message = new ArrayList<String>();
    ArrayList<String> logoutmessage = new ArrayList<String>();


    public ArrayList<String> getStatus() {
        return status;
    }
    public void setItem(String item) {
        this.status.add(item);
    }
    public ArrayList<String> getMessage() {
        return message;
    }
    public void setManufacturer(String manufacturer) {
        this.message.add(manufacturer);
    }
    public ArrayList<String> getLogoutmessage() {
        return logoutmessage;
    }
    public void setModel(String model) {
        this.logoutmessage.add(model);
    }

}
