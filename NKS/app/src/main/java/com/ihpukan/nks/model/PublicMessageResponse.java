package com.ihpukan.nks.model;

public class PublicMessageResponse extends AbstractErrorModel {

    public static String PM_RESPONSE = "PM_RESPONSE";

    //public boolean ok; //OvG: Already in AbstractErrorModel
    public String ts; //timestamp
    public String channel; //channel ID
    public Message message;

    @Override
    public String toString() {
        return String.valueOf(ok);
    }
}