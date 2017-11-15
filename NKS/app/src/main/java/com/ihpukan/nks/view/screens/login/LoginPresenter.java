package com.ihpukan.nks.view.screens.login;

import com.ihpukan.nks.common.Constants;
import com.ihpukan.nks.common.PreferenceManager;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.module.network.RestApiInterface;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View loginView;
    private PreferenceManager preferenceManager;
    private Retrofit retrofit;
    private String token;

    public LoginPresenter(LoginContract.View loginView, PreferenceManager preferenceManager, Retrofit retrofit) {
        this.loginView = loginView;
        this.preferenceManager = preferenceManager;
        this.retrofit = retrofit;
    }


    @Override
    public void doGrabLogin(String url) {
        this.token = extractToken(url);
        preferenceManager.saveToken(this.token);
        retrofit.create(RestApiInterface.class).getAuthUser(token).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(grabAccessObserver);
    }

    @Override
    public void doGrabbedToken(String aToken) {
        preferenceManager.saveToken(aToken);
        retrofit.create(RestApiInterface.class).getAuthUser(aToken).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(grabAccessObserver);
    }

    private Observer<User> grabAccessObserver = new Observer<User>() {
        @Override
        public void onSubscribe(Disposable d) {
            loginView.showProgress();
        }

        @Override
        public void onNext(User user) {
            if (user.hasError()) {
                loginView.showErrorMessage(user.getErrorMessage());
                preferenceManager.clearData();
            } else {
                loginView.successLogin();
            }
        }

        @Override
        public void onError(Throwable e) {
            loginView.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    @Override
    public void loginTokenGrab(String teamName) {
        loginView.loadUrlToWebView(String.format(Constants.LOGIN_URL,teamName));
    }

    @Override
    public boolean needLogin() {
        //Test Token load
        //preferenceManager.saveToken(Constants.XOXSTOKEN);

        return !preferenceManager.hasToken();

    }

    @Override
    public String extractToken(String url) {
        return url.substring(url.indexOf("token=") + "token=".length(), url.lastIndexOf("&"));
    }
}
