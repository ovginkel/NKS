package com.ihpukan.nks.model;

import java.util.List;

public class Attachment {
    public Integer id;
    public String text;
    public String pretext;
    public String fallback;
    public String title;
    public List<String> markdwn_in;
    public List<SlackAction> actions;
    public String callback_id;
    public String color;
    public String attachment_type;
    public Integer comments_count; //1
}
