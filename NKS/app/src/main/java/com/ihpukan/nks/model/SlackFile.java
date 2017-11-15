package com.ihpukan.nks.model;

/**
 * Created by vginkeo on 2017/09/04.
 */

public class SlackFile {

    public String id; //"F2147483862",
    public Integer created; // 1356032811,
    public Integer timestamp; //" : 1356032811,
    public String name; //"file.htm",
    public String title; //"My HTML file",
    public String mimetype;  //"text\/plain",
    public String filetype; //"text",
    public String pretty_type; //"Text",
    public String user; //"U2147483697",
    public String mode; //"hosted",
    public Boolean editable; //true,
    public Boolean is_external; //false,
    public String external_type; //"",
    public String username; //"",
    public Integer size; //12345,
    public String url_private; //"https:\/\/slack.com\/files-pri\/T024BE7LD-F024BERPE\/1.png",
    public String url_private_download; // ": "https:\/\/slack.com\/files-pri\/T024BE7LD-F024BERPE\/download\/1.png",
    public String thumb_64; //": "https:\/\/slack-files.com\/files-tmb\/T024BE7LD-F024BERPE-c66246\/1_64.png",
    public String thumb_80; //": "https:\/\/slack-files.com\/files-tmb\/T024BE7LD-F024BERPE-c66246\/1_80.png",
    public String thumb_360; //": "https:\/\/slack-files.com\/files-tmb\/T024BE7LD-F024BERPE-c66246\/1_360.png",
    public String thumb_360_gif; //"https:\/\/slack-files.com\/files-tmb\/T024BE7LD-F024BERPE-c66246\/1_360.gif",
    public Integer thumb_360_w; //100,
    public Integer thumb_360_h; //100,
    public String thumb_480; //"https:\/\/slack-files.com\/files-tmb\/T024BE7LD-F024BERPE-c66246\/1_480.png",
    public Integer thumb_480_w; //480,
    public Integer thumb_480_h; //480,
    public String thumb_160; //"https:\/\/slack-files.com\/files-tmb\/T024BE7LD-F024BERPE-c66246\/1_160.png",
    public String permalink; //"https:\/\/tinyspeck.slack.com\/files\/cal\/F024BERPE\/1.png",
    public String permalink_public; //" : "https:\/\/tinyspeck.slack.com\/T024BE7LD-F024BERPE-3f9216b62c",
    public String edit_link; //"https:\/\/tinyspeck.slack.com\/files\/cal\/F024BERPE\/1.png/edit",
    public String preview; //"&lt;!DOCTYPE html&gt;\n&lt;html&gt;\n&lt;meta charset='utf-8'&gt;",
    public String preview_highlight; //"&lt;div class=\"sssh-code\"&gt;&lt;div class=\"sssh-line\"&gt;&lt;pre&gt;&lt;!DOCTYPE html...",
    public Integer lines; //123,
    public Integer lines_more; //118,
    public Boolean is_public; //true,
    public Boolean public_url_shared; //false,
    public Boolean display_as_bot; //" : false,
    public ChannelList channels; //["C024BE7LT", ...],
    public ChannelList groups; //["G12345", ...],
    public ChannelList ims; //["D12345", ...],
    //"initial_comment{...},
    //"num_stars7,
    //    "is_starredtrue,
    //    "pinned_to["C024BE7LT", ...],
    //"reactions[
    //{
    //    "name"astonished",
    //        "count3,
    //        "users[ "U1", "U2", "U3" ]
    //},
    //{
    //    "name"facepalm",
    //        "count1034,
    //        "users[ "U1", "U2", "U3", "U4", "U5" ]
    //}
    public Integer comments_count; //1
}
