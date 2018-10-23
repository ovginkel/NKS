package com.ihpukan.nks.model;

public class Profile extends AbstractErrorModel {

    public String avatar_hash;
    public String current_status;
    public String first_name;
    public String last_name;
    public String real_name;
    public String real_name_normalized;
    public String display_name;
    public String display_name_normalized;
    public String status_text;
    public String status_emoji;
    public String title;
    public String email;
    public String skype;
    public String phone;
    public String image_24;
    public String image_32;
    public String image_48;
    public String image_72;
    public String image_192;
    public String image_512;
    public String image_1024;
    public String image_original;

    @Override
    public String toString() {
        return real_name + " " + (email!=null?email:"");
    }

}
