package com.ihpukan.nks.view.screens.main.navdrawer;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihpukan.nks.R;
import com.ihpukan.nks.model.Channel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ViewHolder> {

    private List<Channel> channels;
    public OnChannelClickListener clickListener;

    public ChannelsAdapter(OnChannelClickListener clickListener) {
        this(null, clickListener);
    }

    public ChannelsAdapter(List<Channel> channels, OnChannelClickListener clickListener) {
        this.clickListener = clickListener;
        addChannels(channels);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_holder_channel, parent, false);
        //ViewHolder vh = new ViewHolder(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Channel channel = channels.get(position);
        String channelName = channel.name;
        if(channel.name.contains("mpdm-")) {
            channelName = channelName.replace("mpdm-", "");
        }
        holder.textView.setText(channelName);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onChannelClick(channel);
            }
        });
        //if(channel.is_member){
        holder.imageViewChannelMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onChannelMessageClick(channel);
            }
        });
        //}
        //holder.imageViewChannelMessage.setVisibility(channel.is_member ? View.VISIBLE : View.INVISIBLE);
        holder.imageViewPrivateChannel.setVisibility(TextUtils.isEmpty(channel.is_channel) ? View.VISIBLE : View.INVISIBLE);

    }

    public void addChannels(List<Channel> channels) {
        if (this.channels != null) {
            this.channels.addAll(channels);
        } else {
            this.channels = channels;
        }
        sortChannels();
        this.notifyDataSetChanged();
    }

    private void sortChannels() {
        if (this.channels == null) return;
        Collections.sort(this.channels, new Comparator<Channel>() {
            @Override
            public int compare(Channel channel1, Channel channel2) {
                if (channel1.name.equals(channel2.name)) {
                    return 0;
                }
                if (channel1.name == null) {
                    return -1;
                }
                if (channel2.name == null) {
                    return 1;
                }
                return channel1.name.compareTo(channel2.name);
            }
        });
    }

    public void clearChannels() {
        if (channels == null) return;
        channels.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return channels != null ? channels.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageViewPrivateChannel)
        ImageView imageViewPrivateChannel;
        @BindView(R.id.textViewChannelName)
        TextView textView;
        @BindView(R.id.imageViewChannelMessage)
        ImageView imageViewChannelMessage;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
