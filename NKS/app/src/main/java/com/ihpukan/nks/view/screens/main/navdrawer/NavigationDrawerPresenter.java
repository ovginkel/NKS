package com.ihpukan.nks.view.screens.main.navdrawer;

import android.app.Activity;
import android.widget.Toast;

import com.ihpukan.nks.R;
import com.ihpukan.nks.common.PreferenceManager;
import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.ChannelJoin;
import com.ihpukan.nks.model.ChannelList;
import com.ihpukan.nks.model.ChannelWrapper;
import com.ihpukan.nks.model.GroupsWrapper;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.module.network.RestApiInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.internal.Collection;
import retrofit2.Retrofit;

public class NavigationDrawerPresenter implements NavigationDrawerContract.Presenter {

    private RestApiInterface retrofit;
    private NavigationDrawerContract.View viewNavDrawer;
    private PreferenceManager preferenceManager;

    public NavigationDrawerPresenter(NavigationDrawerContract.View viewNavDrawer, Retrofit retrofit,
                                     PreferenceManager preferenceManager) {
        this.viewNavDrawer = viewNavDrawer;
        this.retrofit = retrofit.create(RestApiInterface.class);
        this.preferenceManager = preferenceManager;
    }

    @Override
    public void loadProfile() {
        retrofit.getAuthUser(preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(profileLoadObserver);
    }

    @Override
    public void loadChannels() {
        retrofit.getChannels(preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(channelsLoadObserver);
    }

    @Override
    public void loadGroups() {
        retrofit.getGroups(preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(groupsLoadObserver);
    }

    private Observer<ChannelJoin> channelJoinObserver = new Observer<ChannelJoin>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(ChannelJoin channelJoin) {

        }

        @Override
        public void onError(Throwable e) {
            viewNavDrawer.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    private Observer<User> profileLoadObserver = new Observer<User>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(User user) {
            viewNavDrawer.loadProfileComplete(user);
        }

        @Override
        public void onError(Throwable e) {
            viewNavDrawer.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    private Observer<ChannelWrapper> channelsLoadObserver = new Observer<ChannelWrapper>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(ChannelWrapper channelWrapper) {
            ChannelWrapper nonArchived = new ChannelWrapper();
            nonArchived.channels = new ArrayList<>();
            if(channelWrapper.channels!=null) {
                int channelCount = channelWrapper.channels.size();

                for (int ci = 0; ci < channelCount; ci++) {
                    Channel ciChan = new Channel((Channel) channelWrapper.channels.toArray()[ci]);
                    //List<Channel> cList;
                    //cList = new ArrayList<>();

                    if ((ciChan.is_archived) == false) {
                        nonArchived.channels.add(new Channel(ciChan));
                    }
                }
            }
            viewNavDrawer.loadChannelsComplete(nonArchived.channels);
        }

        @Override
        public void onError(Throwable e) {
            viewNavDrawer.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    private Observer<GroupsWrapper> groupsLoadObserver = new Observer<GroupsWrapper>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(GroupsWrapper groupsWrapper) {
            GroupsWrapper nonArchived = new GroupsWrapper();
            int channelCount = groupsWrapper.groups.size();
            nonArchived.groups = new ArrayList<>();
            for(int ci = 0; ci<channelCount; ci++)
            {
                Channel ciChan = new Channel((Channel)groupsWrapper.groups.toArray()[ci]);
                //List<Channel> cList;
                //cList = new ArrayList<>();

                if((ciChan.is_archived) == false)
                {
                    nonArchived.groups.add(new Channel(ciChan));
                }
            }
            viewNavDrawer.loadGroupsComplete(nonArchived.groups);
        }

        @Override
        public void onError(Throwable e) {
            viewNavDrawer.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    public void joinChannel(final Activity activity, final Channel channel) {
        //
        retrofit.joinChannel(preferenceManager.getToken(),channel.id).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ChannelJoin>() {
            @Override
            public void onSubscribe(Disposable d)
            {
            }

            @Override
            public void onNext(ChannelJoin channelJoin)
            {
                //Toast.makeText(activity, R.string.channel_activated, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e)
            {
                viewNavDrawer.showErrorMessage(e.getMessage());
            }

            @Override
            public void onComplete()
            {
            }
        });
    }
}
