package com.ihpukan.nks.view.screens.main.users;

import android.app.Activity;

import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.view.base.AbstractBaseView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public interface UsersContract {

    interface View extends AbstractBaseView<UsersContract.Presenter> {

        void showErrorMessage(String message);

        void showProgressBar();

        void showBackIcon();

        void hideProgressBar();

        void hideBackIcon();

        void displayUsers(List<User> users);

        void updateViews();
    }

    interface Presenter {

        void onChannelClick(AppCompatActivity activity, Channel channel);

        void loadAllUsers();

        void searchUser(String query);

        void openIM(final Activity activity, final String userid);
    }

}
