package com.ihpukan.nks.poll;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.ihpukan.nks.service.AbstractService;

import java.util.Calendar;
import java.util.TimeZone;

public class RefreshPollService extends AbstractService {
    public static final int MSG_RATE = 1;
    public static final int MSG_DO = 2;
    public static final int UPDATE_MESSAGES = 0;
    public static final int NOTIFY_MESSAGES = 1;

    //private Timer timer = new Timer();
    //private long rate = 120000L;
    private static final long REPEAT_TIME = 1000 * 60;

    private RefreshAlarmServiceReceiver br;

    private Handler handler = new Handler();

    public RefreshPollService() {
        super("RefreshPollService");//"AbstractService"
    }

    @Override
    public void onStartService() {

        setRecurringAlarm(getApplicationContext());
    }

    private void cancelRecurringAlarm(Context context)
    {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.ihpukan.nks"), PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void setRecurringAlarm(Context context) {

        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getDefault());
        updateTime.set(Calendar.HOUR_OF_DAY, 12);
        updateTime.set(Calendar.MINUTE, 30);

        br = new RefreshAlarmServiceReceiver() {

            @Override
            public void onReceive(Context c, Intent i) {

                onRefreshMessageAlarm();

            }

        };

        registerReceiver(br, new IntentFilter("com.ihpukan.nks") );

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.ihpukan.nks"), PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), REPEAT_TIME , pendingIntent); //

        //Log.d("MyActivity", "Set alarmManager.setRepeating to: " + updateTime.getTime().toString());

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try{
                send(Message.obtain(null, MSG_DO, NOTIFY_MESSAGES, 0));
            }
            catch(Exception e){
                // added try catch block to be sure of uninterupted execution
            }
      /* and here comes the "trick" */
            //handler.postDelayed(this, 5000);
        }
    };

    public void onRefreshMessageAlarm() {
        send(Message.obtain(null, MSG_DO, UPDATE_MESSAGES, 0));
        handler.postDelayed(runnable, 8000); //Notify 8 seconds later
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
    }

    @Override
    public void onStopService() {
        //if (timer != null) {timer.cancel();}
        cancelRecurringAlarm(getApplicationContext());
        //counter = 0;
    }

    @Override
    public void onReceiveMessage(Message msg) {
        /*if (msg.what == MSG_RATE) {
        }*/
    }
}
