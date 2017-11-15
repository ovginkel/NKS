package com.ihpukan.nks.model;

import java.util.List;

public class MessagesWrapper extends AbstractErrorModel {

    //public boolean ok; OvG: Already in AbstractErrorModel
    //public String ts;
    public List<Message> messages;
    public boolean has_more;

    @Override
    public String toString() {
        return messages != null ? "messages: " + messages.size() : "@string/not_found_messages";
    }

}
