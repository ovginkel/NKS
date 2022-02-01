package com.ihpukan.nks.view.screens.main.navdrawer;

import androidx.appcompat.app.AppCompatActivity;

import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.view.base.AbstractBaseView;

import java.util.List;

public interface NavigationDrawerContract {

    interface View extends AbstractBaseView<NavigationDrawerContract.Presenter> {

        void showErrorMessage(String message);

        void loadProfileComplete(User user);

        void loadChannelsComplete(List<Channel> channels);

        /*void onJoinChannel(final Channel channel);*/

        void loadGroupsComplete(List<Channel> channels);

    }

    interface Presenter {

        void loadProfile();

        void loadChannels();

        void joinChannel(final Activity activity, final Channel channel);

        void loadGroups();

    }

}
