package com.ihpukan.nks.poll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by vGinkeO on 2017/08/25.
 */

public class RefreshAlarmServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmUpdater = new Intent(context, RefreshPollService.class);

        context.startService(alarmUpdater);
        //Log.d("AlarmReceiver", "Called context.startService from AlarmReceiver.onReceive");
    }
}
