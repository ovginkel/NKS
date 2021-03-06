package com.ihpukan.nks.view.screens.main.users;

import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihpukan.nks.R;
import com.ihpukan.nks.R2;
import com.ihpukan.nks.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
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
        if(users != null) //Seemed to fixed user load problem
        {
            addUsers(users);
        }
        else
        {

        }
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R2.layout.item_user_card_view, parent, false);
        //UsersAdapter.ViewHolder vh = new UsersAdapter.ViewHolder(view);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.ViewHolder holder, int position) {
        final User user = users.get(position);
        String viewName = user.profile.real_name+(user.deleted?("\r\n("+holder.textViewName.getContext().getString(R.string.inactive_profile)+")"):"");
        holder.textViewName.setText(viewName);
        holder.textViewPhone.setText(user.profile.phone);
        holder.textViewEmail.setText(user.profile.email);

        Linkify.addLinks( holder.textViewPhone, Linkify.PHONE_NUMBERS );
        Linkify.addLinks( holder.textViewEmail, Linkify.EMAIL_ADDRESSES );

        holder.imageViewOpenIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickIMListener.onOpenIMClick(user);
            }
        });


        if(!user.deleted) {
            holder.imageViewProfile.setImageResource(R.drawable.default_profile);
            Picasso.with(holder.itemView.getContext()).
                    load(user.profile.image_48).
                    into(holder.imageViewProfile);
        }
        else
        {
            holder.imageViewProfile.setImageResource(R.drawable.glitch_crab_n);
        }


    }

    public void addUsers(List<User> musers) {
        if ((this.users != null)?(musers!=null):false) {
            this.users.clear();
            this.users.addAll(musers);
        } else {
            if(musers!=null) {
                this.users = new ArrayList<>(musers.size());
                this.users.addAll(musers);
            }
            else
            {
                this.users = new ArrayList<>();
            }
        }
        if(this.users != null ?musers!=null:false) {
            this.notifyDataSetChanged();
        }
    }

    public void clearUsers() {
        if (users == null){
            this.notifyDataSetChanged();
            return;
        }
        else
        {
            users.clear();
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.realName)
        TextView textViewName;
        @BindView(R2.id.phone)
        TextView textViewPhone;
        @BindView(R2.id.email)
        TextView textViewEmail;
        @BindView(R2.id.imageViewProfile) //R.id.imageViewProfile
        ImageView imageViewProfile;
        @BindView(R2.id.imageViewOpenIm)
        ImageView imageViewOpenIm;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
