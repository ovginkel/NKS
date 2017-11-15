package com.ihpukan.nks.view.screens.main.imdrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihpukan.nks.R;
import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.IM;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.view.base.AbstractBaseFragment;
import com.ihpukan.nks.view.screens.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class IMNavigationDrawerFragment extends AbstractBaseFragment implements IMNavigationDrawerContract.View {

    @BindView(R.id.profile_image)
    CircleImageView profileImage; //OvG: Convert to just ImageView if dropping CircleImageView

    @BindView(R.id.profile_name)
    TextView profileName;

    @BindView(R.id.recyclerViewIM)
    RecyclerView recyclerViewIM;

    @BindView(R.id.textViewImsRecycler)
    TextView imsRecycler;

    @BindView(R.id.textViewChannelsRecycler)
    TextView channelsRecycler;

    private IMSAdapter adapter;
    private IMNavigationDrawerContract.Presenter imNavigationDrawerPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.bind(this, view);
        recyclerViewIM.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new IMSAdapter((MainActivity) getActivity());
        recyclerViewIM.setAdapter(adapter);

        imsRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clickListener.onIMSCloseClick();
            }
        });

        channelsRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clickListener.onIMSCloseClick();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.clearIMs();
        if(imNavigationDrawerPresenter!=null) { //Prevent Null Pointer Exception
            imNavigationDrawerPresenter.loadProfile();
            imNavigationDrawerPresenter.loadIMs();
        }
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

    @OnClick(R.id.textViewAllUsers) //Keep this active
    public void onAllUsersClick(View view) {
        Channel channel = new Channel();
        channel.name = Channel.ALL_USERS_CHANNEL;
        ((MainActivity) getActivity()).onChannelClick(channel);
    }

    @Override
    public void setPresenter(IMNavigationDrawerContract.Presenter presenter) {
        imNavigationDrawerPresenter = presenter;
    }

    @Override
    public void showErrorMessage(String message) {
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadIMsComplete(List<IM> ims) {
        adapter.addIMs(ims);
        //imNavigationDrawerPresenter.loadGroups();
    }


}
