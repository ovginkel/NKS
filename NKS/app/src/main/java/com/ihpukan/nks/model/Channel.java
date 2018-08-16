package com.ihpukan.nks.model;

import java.util.List;
import java.util.ArrayList;

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

    public Channel(Channel another) {
        this.id = another.id;
        this.name = another.name;
        this.is_channel = another.is_channel;
        this.created = another.created;
        this.creator = another.creator;
        this.is_archived = another.is_archived;
        this.is_general = another.is_general;
        this.is_member = another.is_member;
        this.last_read = another.last_read;
        this.unread_count = another.unread_count;
        this.unread_count_display = another.unread_count_display;
        this.members = new ArrayList<String>(another.members);
    }

    public Channel(){};
}
