package com.ihpukan.nks.module;

import com.ihpukan.nks.module.app.AppModule;
import com.ihpukan.nks.module.network.NetworkModule;
import com.ihpukan.nks.view.screens.login.LoginActivity;
import com.ihpukan.nks.view.screens.main.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface Components {

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

}
