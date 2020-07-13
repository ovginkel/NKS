package com.ihpukan.nks.view.base;

import androidx.fragment.app.Fragment; //import android.support.v4.app.Fragment;

public abstract class AbstractBaseFragment extends Fragment{
    public static String userMail;

    public static String getUserMail() {
        return userMail;
    }

    public static void setUserMail(String email) {
        userMail = email;
    }

}
