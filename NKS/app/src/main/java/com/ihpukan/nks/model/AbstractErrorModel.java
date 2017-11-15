package com.ihpukan.nks.model;

import android.text.TextUtils;

public abstract class AbstractErrorModel {

    public boolean ok;

    public String stuff;

    public String error;

    public String warning;

    public boolean hasError() {
        return !TextUtils.isEmpty(error) || !TextUtils.isEmpty(warning);
    }

    public String getErrorMessage() {
        if(!TextUtils.isEmpty(error)) return error;
        if(!TextUtils.isEmpty(warning)) return warning;
        if(!TextUtils.isEmpty(stuff)) return stuff;
        return "";
    }

}
