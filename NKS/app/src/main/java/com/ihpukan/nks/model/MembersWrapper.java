package com.ihpukan.nks.model;

import java.util.List;

public class MembersWrapper extends AbstractErrorModel {

    public List<User> members;

    @Override
    public String toString() {
        return members != null ? "members: " + members.size() : "not found members";
    }

}
