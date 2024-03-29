package com.ihpukan.nks.view.screens.main.messages;

import androidx.appcompat.app.AppCompatActivity;

import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.IM;
import com.ihpukan.nks.model.MembersWrapper;
import com.ihpukan.nks.model.Message;
import com.ihpukan.nks.view.base.AbstractBaseView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public interface MessagesContract {

    interface View extends AbstractBaseView<Presenter> {

        void showErrorMessage(String message);

        void showProgressBar();

        void showBackIcon();

        void hideProgressBar();

        void hideBackIcon();

        void displayMessages(List<Message> messages);

    }

    interface Presenter {

        void onChannelClick(AppCompatActivity activity, Channel channel);

        void onIMClick(AppCompatActivity activity, IM im);

        void loadAllMessages();

        void updateWithNewMessages();

        void loadAllUsers(); //For names/icons display purposes

        void sendMessage(Activity activity, String message);

        void searchMessage(String query);

        void endSearchMessage();

        void uploadFile(Activity activity, String filePath);

        MembersWrapper getMembersWrapper();
    }

}
