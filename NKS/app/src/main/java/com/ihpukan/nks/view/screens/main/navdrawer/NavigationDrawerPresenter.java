package com.ihpukan.nks.view.screens.main.navdrawer;

import com.ihpukan.nks.common.PreferenceManager;
import com.ihpukan.nks.model.ChannelWrapper;
import com.ihpukan.nks.model.GroupsWrapper;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.module.network.RestApiInterface;
import com.ihpukan.nks.view.screens.main.navdrawer.NavigationDrawerContract;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
            viewNavDrawer.loadChannelsComplete(channelWrapper.channels);
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
            viewNavDrawer.loadGroupsComplete(groupsWrapper.groups);
        }

        @Override
        public void onError(Throwable e) {
            viewNavDrawer.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };
}
