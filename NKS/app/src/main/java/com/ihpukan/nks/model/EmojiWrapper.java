package com.ihpukan.nks.model;

import java.util.Map;

public class EmojiWrapper extends AbstractErrorModel {

    public Map<String,String> emoji;

    @Override
    public String toString() {
        return emoji.size()>0 ? "emojis: " + emoji.size() : "no emojis";
    }

}
