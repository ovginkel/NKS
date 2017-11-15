package com.ihpukan.nks.common;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ihpukan.nks.view.base.AbstractBaseActivity;
import com.ihpukan.nks.view.base.AbstractBaseFragment;

public class NavigationHelper {

    public static void addFragment(FragmentManager fragmentManager, AbstractBaseFragment fragment, @IdRes int idContainer) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(idContainer, fragment);
        ft.commit();
    }

    public static void gotoActivity(Activity currentActivity, Class<? extends AbstractBaseActivity> activity, boolean needFinish, boolean clearStack) {
        Intent intent = new Intent(currentActivity, activity);
        if (clearStack) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        currentActivity.startActivity(intent);
        if (needFinish) {
            currentActivity.finish();
        }
    }

}
