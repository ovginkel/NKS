package com.ihpukan.nks.view.screens.main.imdrawer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihpukan.nks.R2;
import com.ihpukan.nks.model.IM;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IMSAdapter extends RecyclerView.Adapter<IMSAdapter.ViewHolder> {

    private List<IM> ims;
    public OnIMClickListener clickListener;

    public IMSAdapter(OnIMClickListener clickListener) {
        this(null, clickListener);
    }

    public IMSAdapter(List<IM> ims, OnIMClickListener clickListener) {
        this.clickListener = clickListener;
        addIMs(ims);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R2.layout.item_holder_im, parent, false);
        //ViewHolder vh = new ViewHolder(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final IM im = ims.get(position);
        String imName = im.member.profile.real_name;
        if(im.member.profile.real_name.contains("mpdm-")) {
            imName = imName.replace("mpdm-", "");
        }
        holder.textView.setText(imName);
        /*holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onIMMessageClick(im);
            }
        });*/
        //if(im.is_member){
        holder.imageViewIMMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onIMMessageClick(im);
            }
        });


        //}
        //holder.imageViewIMMessage.setVisibility(im.is_member ? View.VISIBLE : View.INVISIBLE);
        //holder.imageViewPrivateIM.setVisibility(TextUtils.isEmpty(im.is_im) ? View.VISIBLE : View.INVISIBLE);
    }

    public void addIMs(List<IM> ims) {
        if (this.ims != null) {
            this.ims.addAll(ims);
        } else {
            this.ims = ims;
        }
        sortIMs();
        this.notifyDataSetChanged();
    }

    private void sortIMs() {
        if (this.ims == null) return;
        Collections.sort(this.ims, new Comparator<IM>() {
            @Override
            public int compare(IM im1, IM im2) {
                if (im1.member.profile.real_name.equals(im2.member.profile.real_name)) {
                    return 0;
                }
                if (im1.member.profile.real_name == null) {
                    return -1;
                }
                if (im2.member.profile.real_name == null) {
                    return 1;
                }
                return im1.member.profile.real_name.compareTo(im2.member.profile.real_name);
            }
        });
    }

    public void clearIMs() {
        if (ims == null) return;
        ims.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ims != null ? ims.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.textViewIMName)
        TextView textView;
        @BindView(R2.id.imageViewIMMessage)
        ImageView imageViewIMMessage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
