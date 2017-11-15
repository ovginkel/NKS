package com.ihpukan.nks.model;

public class IM {
    public String id; //"D024BFF1M",
    public boolean is_im; //true,
    public String user; //"USLACKBOT",
    public String created; //1372105335,
    public String is_user_deleted; //false
    public User member;

    @Override
    public String toString() {
        return id;
    }
}
