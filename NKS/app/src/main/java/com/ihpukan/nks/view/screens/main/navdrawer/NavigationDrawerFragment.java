package com.ihpukan.nks.view.screens.main.navdrawer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihpukan.nks.R;
import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.view.base.AbstractBaseFragment;
import com.ihpukan.nks.view.screens.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationDrawerFragment extends AbstractBaseFragment implements NavigationDrawerContract.View {

    @BindView(R.id.profile_image)
    CircleImageView profileImage; //OvG: Convert to just ImageView if dropping CircleImageView

    @BindView(R.id.profile_name)
    TextView profileName;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.textViewImsRecycler)
    TextView imsRecycler;

    @BindView(R.id.textViewChannelsRecycler)
    TextView channelsRecycler;

    private ChannelsAdapter adapter;
    private NavigationDrawerContract.Presenter navigationDrawerPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChannelsAdapter((MainActivity) getActivity());
        recyclerView.setAdapter(adapter);
        imsRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clickListener.onChannelsCloseClick();
            }
        });

        channelsRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clickListener.onChannelsCloseClick();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.clearChannels();
        if( navigationDrawerPresenter!=null) { //Prevent Null Pointer Exception
            navigationDrawerPresenter.loadProfile();
            navigationDrawerPresenter.loadChannels();
        }

}

    @Override
    public void setPresenter(NavigationDrawerContract.Presenter presenter) {
        navigationDrawerPresenter = presenter;
    }

    @Override
    public void showErrorMessage(String message) {
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadProfileComplete(User user) {
        if (user == null || user.profile == null) return;
        profileName.setText(user.profile.real_name);
        setUserMail(user.profile.email);
        Picasso.with(getContext()).
                load(user.profile.image_192).
                into(profileImage);
    }

    @OnClick(R.id.textViewAllUsers)
    public void onAllUsersClick(View view) {
        Channel channel = new Channel();
        channel.name = Channel.ALL_USERS_CHANNEL;
        ((MainActivity) getActivity()).onChannelClick(channel);
    }

    @Override
    public void loadChannelsComplete(List<Channel> channels) {
        adapter.addChannels(channels);
        navigationDrawerPresenter.loadGroups();
    }

   /* @Override
    public void onJoinChannel(final Channel channel) {
        navigationDrawerPresenter.joinChannel(getActivity(),channel);
    }*/

    @Override
    public void loadGroupsComplete(List<Channel> channels) {
        adapter.addChannels(channels);
    }
}
