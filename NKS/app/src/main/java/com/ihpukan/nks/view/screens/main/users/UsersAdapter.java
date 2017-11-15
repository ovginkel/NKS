package com.ihpukan.nks.view.screens.main.users;

import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihpukan.nks.R;
import com.ihpukan.nks.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> users;
    private OnOpenIMClickListener clickIMListener;

    public UsersAdapter(OnOpenIMClickListener clickIMListener) {
        this(null, clickIMListener);
    }

    public UsersAdapter(List<User> users, OnOpenIMClickListener clickIMListener) {
        this.clickIMListener = clickIMListener;
        addUsers(users);
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_card_view, parent, false);
        //UsersAdapter.ViewHolder vh = new UsersAdapter.ViewHolder(view);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.textViewName.setText(user.profile.real_name);
        holder.textViewPhone.setText(user.profile.phone);

        Linkify.addLinks( holder.textViewPhone, Linkify.PHONE_NUMBERS );

        holder.imageViewOpenIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickIMListener.onOpenIMClick(user);
            }
        });

        holder.imageViewProfile.setImageResource(R.drawable.default_profile);
        Picasso.with(holder.itemView.getContext()).
                load(user.profile.image_48).
                into(holder.imageViewProfile);


    }

    public void addUsers(List<User> users) {
        if (this.users != null) {
            this.users.clear();
            this.users.addAll(users);
        } else {
            this.users = users;
        }
        this.notifyDataSetChanged();
    }

    public void clearUsers() {
        if (users == null){
            this.notifyDataSetChanged();
            return;
        }
        users.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.realName)
        TextView textViewName;
        @BindView(R.id.phone)
        TextView textViewPhone;
        @BindView(R.id.imageViewProfile) //R.id.imageViewProfile
        ImageView imageViewProfile;
        @BindView(R.id.imageViewOpenIm)
        ImageView imageViewOpenIm;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
