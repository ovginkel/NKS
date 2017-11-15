package com.ihpukan.nks.model;

public class OpenIMWrapper extends AbstractErrorModel {

    public boolean no_op;
    public boolean already_open;
    public IM channel;

    @Override
    public String toString() {
        return channel != null ? "imchannel: " + channel.id : "no found imchannel";
    }

}
