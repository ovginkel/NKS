package com.ihpukan.nks.view.screens.main.navdrawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihpukan.nks.R;
import com.ihpukan.nks.R2;
import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.view.base.AbstractBaseFragment;
import com.ihpukan.nks.view.screens.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavigationDrawerFragment extends AbstractBaseFragment implements NavigationDrawerContract.View {

    @BindView(R2.id.profile_image)
    ImageView profileImage; //OvG: Convert to just ImageView if dropping CircleImageView

    @BindView(R2.id.profile_name)
    TextView profileName;

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.textViewImsRecycler)
    TextView imsRecycler;

    @BindView(R2.id.textViewChannelsRecycler)
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
        if
                (
                (user == null)
                        ?
                        true
                        :
                        ( user.profile == null )
                )
        {
            return;
        }
        profileName.setText(user.profile.real_name);
        setUserMail(user.profile.email);
        if( user.profile.image_192 == null )
        {
            Picasso.with(getContext()).
                    load(R.drawable.glitch_crab_n).
                    into(profileImage);
        }
        else {
            Picasso.with(getContext()).
                    load(user.profile.image_192).
                    into(profileImage);
        }
    }

    @OnClick(R2.id.textViewAllUsers)
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
