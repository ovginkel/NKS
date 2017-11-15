package com.ihpukan.nks.model;

import java.util.List;

public class GroupsWrapper extends AbstractErrorModel {

    public List<Channel> groups;

    @Override
    public String toString() {
        return groups != null ? "groups: " + groups.size() : "not found groups";
    }
}