package com.ihpukan.nks;

import android.app.Application;

import com.ihpukan.nks.common.Constants;
import com.ihpukan.nks.module.DaggerComponents;
import com.ihpukan.nks.module.app.AppModule;
import com.ihpukan.nks.module.Components;
import com.ihpukan.nks.module.network.NetworkModule;


public class NKSApplication extends Application {

    private Components components;

    @Override
    public void onCreate() {
        super.onCreate();
        components = DaggerComponents.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule(Constants.API_URL))
                .build();
    }

    public Components getComponent() {
        return components;
    }


}
