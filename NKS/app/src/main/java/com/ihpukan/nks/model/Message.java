package com.ihpukan.nks.model;

import java.util.List;

public class Message {
    public String type;
    public String ts;
    public String user;
    public String text;
    public boolean is_starred;
    public List<Reaction> reactions;
    public String username;
    public SlackIcon icons;
    public List<Attachment> attachments;
    public List<SlackFile> files;
    public User member;

    public String subtype;
    public String bot_id;
    public boolean markdwn;

    @Override
    public String toString() {
        return ts;
    }
}
