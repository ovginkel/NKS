package com.ihpukan.nks.model;

import java.util.List;

public class ChannelWrapper extends AbstractErrorModel {

    public List<Channel> channels;

    @Override
    public String toString() {
        return channels != null ? "channels: " + channels.size() : "not found channels";
    }
}
