package com.ihpukan.nks.view.screens.main.navdrawer;

import com.ihpukan.nks.model.Channel;

public interface OnChannelClickListener {

    void onChannelClick(Channel channel);

    void onChannelMessageClick(Channel channel);
    void onChannelsCloseClick();



}
