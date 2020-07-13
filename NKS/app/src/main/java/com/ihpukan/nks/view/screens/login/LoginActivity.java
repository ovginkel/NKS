package com.ihpukan.nks.view.screens.login;

import android.os.Bundle;

import com.ihpukan.nks.R;
import com.ihpukan.nks.R2;
import com.ihpukan.nks.NKSApplication;
import com.ihpukan.nks.common.NavigationHelper;
import com.ihpukan.nks.common.PreferenceManager;
import com.ihpukan.nks.view.base.AbstractBaseActivity;
import com.ihpukan.nks.view.screens.main.MainActivity;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class LoginActivity extends AbstractBaseActivity {

    @Inject
    Retrofit retrofit;

    private LoginPresenter loginPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R2.layout.activity_login);
        LoginFragment fragment = new LoginFragment();
        this.loginPresenter = new LoginPresenter(fragment, PreferenceManager.get(this), retrofit);
        NavigationHelper.addFragment(getSupportFragmentManager(), fragment, R.id.container);
        fragment.setPresenter(loginPresenter);
    }

    @Override
    protected void setUpComponent() {
        ((NKSApplication) getApplication()).getComponent().inject(this);
    }

    public void gotoMainActivity() {
        NavigationHelper.gotoActivity(this, MainActivity.class, true, false);
    }
}
