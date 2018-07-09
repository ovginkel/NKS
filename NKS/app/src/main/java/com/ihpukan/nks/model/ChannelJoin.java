package com.ihpukan.nks.model;

public class ChannelJoin {
    public Channel channel;
    public String warning;
    @Override
    public String toString() {
        return channel.name;
    }
}
