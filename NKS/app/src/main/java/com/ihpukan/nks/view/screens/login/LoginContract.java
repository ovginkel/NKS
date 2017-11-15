package com.ihpukan.nks.view.screens.login;

import com.ihpukan.nks.view.base.AbstractBaseView;

public interface LoginContract {

    interface View extends AbstractBaseView<Presenter> {

        void showProgress();

        void hideProgress();

        void successLogin();

        void showErrorMessage(String message);

        void showErrorMessageInWebView(String html);

        void loadUrlToWebView(String url);

        void getTeam();

    }

    interface Presenter {

        void doGrabLogin(String url);
        void doGrabbedToken(String aToken);

        void loginTokenGrab(String teamName);

        boolean needLogin();

        String extractToken(String url);

    }

}
