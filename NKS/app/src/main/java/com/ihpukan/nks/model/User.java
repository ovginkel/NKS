package com.ihpukan.nks.model;

public class User extends AbstractErrorModel {

    public String id;
    public String name;
    public boolean deleted;
    public String color;
    public String team_id;
    public String status;
    public Profile profile;
    public boolean is_admin;
    public boolean is_owner;
    public boolean is_primary_owner;
    public boolean is_restricted;
    public boolean is_ultra_restricted;
    public int updated;
    public boolean has_2fa;
    public String two_factor_type;
    public boolean isContact;

}
