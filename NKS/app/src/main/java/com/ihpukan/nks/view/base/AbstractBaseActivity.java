package com.ihpukan.nks.view.base;

import android.os.Bundle;
import androidx.annotation.Nullable; //import android.support.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; //import android.support.v7.app.AppCompatActivity;

public abstract class AbstractBaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpComponent();
    }

    protected abstract void setUpComponent();

}
