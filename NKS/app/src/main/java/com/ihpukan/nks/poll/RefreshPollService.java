package com.ihpukan.nks.poll;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.WorkerThread;

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

public class RefreshPollService extends Service {
    public static final int MSG_RATE = 1;
    public static final int MSG_DO = 2;
    public static final int UPDATE_MESSAGES = 0;
    public static final int NOTIFY_MESSAGES = 1;
    public static final int MSG_REGISTER_CLIENT = 9991;
    public static final int MSG_UNREGISTER_CLIENT = 9992;
    private static String mName;

    private static Timer timer = new Timer();
    private static long rate = 120000L;

    static ArrayList<Messenger> mClients = new ArrayList<>(); // Keeps track of all current registered clients.
    final Messenger mMessenger = new Messenger(new RefreshPollService.IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.

    public RefreshPollService(String name) {
        super();
        mName = name;
    }

    public RefreshPollService() {
        super();
        mName = "RefreshPollService";
    }

    private static class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    //Log.i("MyService", "Client registered: "+msg.replyTo);
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    //Log.i("MyService", "Client un-registered: "+msg.replyTo);
                    mClients.remove(msg.replyTo);
                    break;
                default:
                    //super.handleMessage(msg);
                    onReceiveMessage(msg);
            }
        }
    }

    public void onCreate() {
        super.onCreate();

        onStartService();

        ////
       /* HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);*/
        ////

        //Log.i("MyService", "Service Started.");
    }

    //@Override
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
        timer = new Timer();

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

    //@Override
    @WorkerThread
    protected void onHandleIntent(Intent intent)
    {
        //Log.d("RefreshPollService", "About to execute MyTask");
        //send(Message.obtain(null, MSG_COUNTER, counter, 0));
        //alarmListener.onRefreshMessageAlarm();
    }

    //@Override
    public void onStopService() {
        if (timer != null) {timer.cancel();}
        //counter = 0;
    }

    //@Override
    public static void onReceiveMessage(Message msg) {
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

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //mServiceLooper.quit();

        onStopService();

        //Log.i("MyService", "Service Stopped.");
    }

    protected static void send(Message msg) {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                //Log.i("MyService", "Sending message to clients: "+msg);
                mClients.get(i).send(msg);
            }
            catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                //Log.e("MyService", "Client is dead. Removing from list: "+i);
                mClients.remove(i);
            }
        }
    }

}
