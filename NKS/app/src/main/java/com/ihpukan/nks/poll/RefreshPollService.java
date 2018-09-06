package com.ihpukan.nks.poll;

import android.content.Intent;
import android.os.Message;

import com.ihpukan.nks.service.AbstractService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vGinkeO on 2017/08/25.
 */

/*
public class RefreshPollService extends IntentService {
   public OnRefreshAlarmListener alarmListener;
   public RefreshPollService() {
       super("RefreshPollService");
   }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d("RefreshPollService", "About to execute MyTask");
        //alarmListener.onRefreshMessageAlarm();
    }

}*/

public class RefreshPollService extends AbstractService {
    public static final int MSG_RATE = 1;
    public static final int MSG_DO = 2;
    public static final int UPDATE_MESSAGES = 0;
    public static final int NOTIFY_MESSAGES = 1;

    private Timer timer = new Timer();
    private long rate = 120000L;

    public RefreshPollService() {
        super("RefreshPollService");//"AbstractService"
    }

    @Override
    public void onStartService() {
        // Increment counter and send to activity every 250ms
        /*timer.scheduleAtFixedRate(new TimerTask(){
            public void run() {
                try {
                    counter += incrementby;
                    send(Message.obtain(null, MSG_COUNTER, counter, 0));
                }
                catch (Throwable t) { }
            }, 0L, 250L);
        }*/

        timer.schedule( /*AtFixedRate heaps up...*/
                new TimerTask(){
            public void run() {
                try {
                    //Update messages
                    send(Message.obtain(null, MSG_DO, UPDATE_MESSAGES, 0));
                } catch (Throwable t) {
                }
            }
            }, 1000L, rate );

        timer.schedule( /*AtFixedRate heaps up...*/
                new TimerTask(){
                    public void run() {
                        try {
                            //Notify of new messages
                            send(Message.obtain(null, MSG_DO, NOTIFY_MESSAGES, 0));
                        } catch (Throwable t) {
                        }
                    }
                }, 1000L, rate/6 );
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        //Log.d("RefreshPollService", "About to execute MyTask");
        //send(Message.obtain(null, MSG_COUNTER, counter, 0));
        //alarmListener.onRefreshMessageAlarm();
    }

    @Override
    public void onStopService() {
        if (timer != null) {timer.cancel();}
        //counter = 0;
    }

    @Override
    public void onReceiveMessage(Message msg) {
        if (msg.what == MSG_RATE) {
            if(msg.arg1>10000L && msg.arg1<1800000)
            {
                rate = msg.arg1;

                timer.cancel();

                timer.schedule( /*AtFixedRate heaps up...*/
                        new TimerTask(){
                            public void run() {
                                try {
                                    //Update messages
                                    send(Message.obtain(null, MSG_DO, UPDATE_MESSAGES, 0));
                                } catch (Throwable t) {
                                }
                            }
                        }, 1000L, rate );

                timer.schedule( /*AtFixedRate heaps up...*/
                        new TimerTask(){
                            public void run() {
                                try {
                                    //Notify of new messages
                                    send(Message.obtain(null, MSG_DO, NOTIFY_MESSAGES, 0));
                                } catch (Throwable t) {
                                }
                            }
                        }, 1000L, rate/6 );
            }
        }
    }
}
