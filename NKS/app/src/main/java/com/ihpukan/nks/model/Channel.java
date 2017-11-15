package com.ihpukan.nks.model;

import java.util.List;


public class Channel {

    public static String ALL_USERS_CHANNEL = "ALL_USERS_CHANNEL";

    public String id;
    public String name;
    public String is_channel;
    public int created;
    public String creator;
    public boolean is_archived;
    public boolean is_general;
    public boolean is_member;
    public String last_read;
    public int unread_count;
    public int unread_count_display;
    public List<String> members;

    @Override
    public String toString() {
        return name;
    }
}
