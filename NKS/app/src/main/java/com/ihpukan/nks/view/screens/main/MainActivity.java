package com.ihpukan.nks.view.screens.main;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ihpukan.nks.NKSApplication;
import com.ihpukan.nks.R;
import com.ihpukan.nks.R2;
import com.ihpukan.nks.common.CommonUtils;
import com.ihpukan.nks.common.DialogUtils;
import com.ihpukan.nks.common.IDialogClickListener;
import com.ihpukan.nks.common.NavigationHelper;
import com.ihpukan.nks.common.PreferenceManager;
import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.IM;
import com.ihpukan.nks.poll.RefreshPollService;
import com.ihpukan.nks.service.ServiceManager;
import com.ihpukan.nks.view.base.AbstractBaseActivity;
import com.ihpukan.nks.view.base.AbstractBaseFragment;
import com.ihpukan.nks.view.screens.login.LoginActivity;
import com.ihpukan.nks.view.screens.main.imdrawer.IMNavigationDrawerFragment;
import com.ihpukan.nks.view.screens.main.imdrawer.IMNavigationDrawerPresenter;
import com.ihpukan.nks.view.screens.main.imdrawer.OnIMClickListener;
import com.ihpukan.nks.view.screens.main.messages.MessagesFragment;
import com.ihpukan.nks.view.screens.main.messages.MessagesPresenter;
import com.ihpukan.nks.view.screens.main.navdrawer.NavigationDrawerFragment;
import com.ihpukan.nks.view.screens.main.navdrawer.NavigationDrawerPresenter;
import com.ihpukan.nks.view.screens.main.navdrawer.OnChannelClickListener;
import com.ihpukan.nks.view.screens.main.users.UsersFragment;
import com.ihpukan.nks.view.screens.main.users.UsersPresenter;

import java.io.File;

import javax.inject.Inject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerConst;
import retrofit2.Retrofit;

//import android.widget.FrameLayout;

public class MainActivity extends AbstractBaseActivity implements OnIMClickListener, OnChannelClickListener,
        /*MenuItemCompat.OnActionExpandListener,*/ SearchView.OnQueryTextListener, TextView.OnEditorActionListener {

    @Inject
    Retrofit retrofit;
    @Inject
    Retrofit retrofitMessage;

    @BindView(R2.id.toolbar)
    Toolbar toolbar;

    @BindView(R2.id.navigation_drawer)
    DrawerLayout drawerLayout;

    @BindView(R2.id.left_drawer)
    ContentFrameLayout drawerContainer;

    SearchView searchView;

    ActionBarDrawerToggle drawerToggle;
    NavigationDrawerPresenter navigationDrawerPresenter;
    UsersPresenter mainPresenter;
    static MessagesPresenter messagePresenter;

    IMNavigationDrawerPresenter imNavigationDrawerPresenter;
    IMNavigationDrawerFragment imNavigationDrawerFragment;

    static Boolean mainPresenterActive = false;
    static Boolean messagePresenterActive = false;

    UsersFragment mainFragment;
    static public MessagesFragment messagesFragment;

    private ServiceManager service;

   static private NotificationManager mNotificationManager;
   static private Notification mNotification;

    boolean refreshIsActive = false;

    boolean searchIsActive = false;

    static Context thisActivityContext;
    static incomingRefreshPollServiceHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }
        thisActivityContext = this;
        mHandler = new incomingRefreshPollServiceHandler();
        setContentView(R2.layout.activity_main);

        ButterKnife.bind(this);

        setUpNavigation();
        setUpBar();

        NavigationDrawerFragment navigationDrawerFragment = new NavigationDrawerFragment();
        navigationDrawerPresenter = new NavigationDrawerPresenter(navigationDrawerFragment, retrofit, PreferenceManager.get(this));
        navigationDrawerFragment.setPresenter(navigationDrawerPresenter);
        NavigationHelper.addFragment(getSupportFragmentManager(), navigationDrawerFragment, R.id.left_drawer); //R.id.left_drawer

        //imNavigationDrawerFragment = new IMNavigationDrawerFragment();
        //imNavigationDrawerPresenter = new IMNavigationDrawerPresenter(imNavigationDrawerFragment, retrofit, PreferenceManager.get(this));
        //imNavigationDrawerFragment.setPresenter(imNavigationDrawerPresenter);
        //NavigationHelper.addFragment(getSupportFragmentManager(), imNavigationDrawerFragment, R.id.left_drawer); //R.id.left_drawer


        mainFragment = new UsersFragment();
        mainPresenter = new UsersPresenter(mainFragment, retrofit, PreferenceManager.get(this));
        mainFragment.setPresenter(mainPresenter);
        NavigationHelper.addFragment(getSupportFragmentManager(), mainFragment, R.id.main_container); //R.id.main_container

        this.service = new ServiceManager(this, RefreshPollService.class,mHandler);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        drawerLayout.openDrawer(drawerContainer);
    }

    static private class incomingRefreshPollServiceHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            // Receive message from service
            switch (msg.what) {
                case RefreshPollService.MSG_DO:
                    //textValue1.setText("Counter @ Service1: " + msg.arg1);
                    if(messagePresenterActive) {

                        if(msg.arg1==RefreshPollService.NOTIFY_MESSAGES) {
                            if (messagePresenter.numberOfNewMessages > 0) {

                                Intent notificationIntent = new Intent(messagesFragment.getContext(), MainActivity.class);
                                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent contentIntent = PendingIntent.getActivity(messagesFragment.getContext(),
                                        0, notificationIntent,
                                        PendingIntent.FLAG_CANCEL_CURRENT);

                                mNotification = new Notification.Builder(messagesFragment.getContext())
                                        .setContentTitle("NKS (" + (messagePresenter.numberOfNewMessages) + ")")
                                        .setContentText(Resources.getSystem().getString(R.string.messages_received))
                                        .setSmallIcon(R.drawable.new_message_icon_notify)
                                        .setSubText(Resources.getSystem().getString(R.string.nks_got)+" "+((messagePresenter.numberOfNewMessages>1)?(messagePresenter.numberOfNewMessages)+" "+Resources.getSystem().getString(R.string.multi_new_messages):Resources.getSystem().getString(R.string.single_new_message)) )
                                        .setContentIntent(contentIntent)
                                        .build();

                                mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                                mNotification.priority = Notification.PRIORITY_HIGH;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mNotification.category = Notification.CATEGORY_MESSAGE;
                                    mNotification.color = Notification.COLOR_DEFAULT;
                                }
                                mNotification.vibrate = new long[]{500, 200, 500, 200, 500};

                                mNotificationManager.notify("NKS", 1, mNotification);
                                messagePresenter.numberOfNewMessages = 0;
                            }
                        }
                        else if(msg.arg1==RefreshPollService.UPDATE_MESSAGES)
                        {
                            if(messagesFragment.presenter!=null) {
                                messagesFragment.onRefreshMessages();
                            }
                            else{ //Fix dead presenter
                                mainPresenterActive = false;
                                //if(messagePresenterActive == false) {
                                messagesFragment = new MessagesFragment();

                                //messagePresenter = new MessagesPresenter(messagesFragment, retrofitMessage, PreferenceManager.get(getApplicationContext()), AbstractBaseFragment.getUserMail()!=null?AbstractBaseFragment.getUserMail():"");
                                messagesFragment.setPresenter(messagePresenter);
                                FragmentManager fm = (messagesFragment.getActivity()!=null)?messagesFragment.getActivity().getSupportFragmentManager():null;
                                if(fm!=null) {
                                    NavigationHelper.addFragment(fm, messagesFragment, R.id.main_container);
                                }
                                //}
                                if(messagePresenter.currentChannel!=null)
                                {
                                    messagePresenter.onChannelClick((AppCompatActivity) thisActivityContext.getApplicationContext(), messagePresenter.currentChannel);
                                }
                                else if (messagePresenter.currentIM!=null)
                                {
                                    messagePresenter.onChannelClick((AppCompatActivity) thisActivityContext.getApplicationContext(), messagePresenter.currentChannel);
                                }
                                messagePresenterActive = true;
                            }
                        }

                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void setUpNavigation() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void setUpBar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        catch(java.lang.NullPointerException exception){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R2.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();/*MenuItemCompat.getActionView(searchItem);*/
        searchView.setOnQueryTextListener(this);
        int searchViewPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchPlateEditText = (EditText) searchView.findViewById(searchViewPlateId);
        if (searchPlateEditText != null) {
            searchPlateEditText.setOnEditorActionListener(this);
        }

        searchItem.setOnActionExpandListener((new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                if(messagePresenterActive && searchIsActive) {
                    //if(refreshIsActive){service.stop();refreshIsActive = false;}
                    searchIsActive = false;
                    messagePresenter.endSearchMessage();
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                if(messagePresenterActive && searchIsActive) {
                    //if(refreshIsActive){service.stop();refreshIsActive = false;}
                    searchIsActive = false;
                    messagePresenter.endSearchMessage();
                }
                return true;
            }
        })); /*MenuItemCompat.setOnActionExpandListener(searchItem, this);*/
        return true;
    }

    @Override
    protected void setUpComponent() {
        ((NKSApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(drawerContainer);
                break;
            case R.id.action_log_out:
                DialogUtils.displayChooseOkNoDialog(this, R.string.dialog_log_out_title, R.string.dialog_log_out_message,
                        new IDialogClickListener() {
                            @Override
                            public void onOK() {
                                PreferenceManager.get(MainActivity.this).clearData();
                                if(refreshIsActive){service.stop();refreshIsActive = false;}
                                //NavigationHelper.gotoActivity(MainActivity.this, LoginActivity.class, false, true);
                            }
                        });
                break;
            case R.id.action_log_in:
                DialogUtils.displayChooseOkNoDialog(this, R.string.dialog_log_in_title, R.string.dialog_log_in_message,
                        new IDialogClickListener() {
                            @Override
                            public void onOK() {
                                PreferenceManager.get(MainActivity.this).clearData();
                                if(refreshIsActive){service.stop();refreshIsActive = false;}
                                NavigationHelper.gotoActivity(MainActivity.this, LoginActivity.class, false, true);
                            }
                        });
                break;
            case R.id.action_refresh:
                if(messagePresenterActive)
                {
                    if(!refreshIsActive)
                    {
                        //setRecurringAlarm(this);
                        Toast.makeText(getApplicationContext(), getString(R.string.activated_refresh), Toast.LENGTH_SHORT).show();
                        refreshIsActive = true;
                        service.start();
                    }
                    else
                    {
                        //cancelRecurringAlarm(this);

                        Toast.makeText(getApplicationContext(), getString(R.string.deactivated_refresh), Toast.LENGTH_SHORT).show();
                        refreshIsActive = false;
                        service.stop();
                    }
                }

                break;
            case R.id.action_send_message:
                if(messagePresenterActive)
                {
                    //if(refreshIsActive){service.stop();refreshIsActive = false;}
                    messagesFragment.onSendClick();
                }
                break;
            case R.id.action_upload_file:
                if(messagePresenterActive)
                {
                    messagesFragment.onFileClick();
                }
                break;
            case R.id.action_upload_photo:
                if(messagePresenterActive)
                {
                    messagesFragment.onPhotoClick();
                }
                break;
            case R.id.action_exit:
                DialogUtils.displayChooseOkNoDialog(this, R.string.dialog_exit_title, R.string.dialog_exit_message,
                        new IDialogClickListener() {
                            @Override
                            public void onOK() {
                                //PreferenceManager.get(MainActivity.this).clearData();
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("Exit me", true);
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
            case R.id.action_search:
                if(mainPresenterActive)
                {
                    mainPresenter.searchUser(searchView.getQuery().toString());
                }
                else if(messagePresenterActive)
                {
                    //if(refreshIsActive){service.stop();refreshIsActive = false;}
                    messagePresenter.searchMessage(searchView.getQuery().toString());
                }

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(messagePresenterActive)
        {
            switch (requestCode)
            {
                //NINJA FILE PICKER
                case FilePickerConst.REQUEST_CODE_PHOTO:

                    if(resultCode== Activity.RESULT_OK && (data!=null?data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).size()>0:false))
                    {
                        final Activity activity = this;
                        DialogUtils.displayChooseOkNoDialog(activity, getString(R.string.confirm_upload)   , data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).get(0),
                                new IDialogClickListener() {
                                    @Override
                                    public void onOK() {
                                        messagePresenter.uploadFile(activity,data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).get(0));
                                    }
                                });
                    }
                    break;
                case FilePickerConst.REQUEST_CODE_DOC:
                    if(resultCode== Activity.RESULT_OK && (data!=null?data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS).size()>0:false))
                    {
                        final Activity activity = this;
                        DialogUtils.displayChooseOkNoDialog(activity, getString(R.string.confirm_upload)   , data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS).get(0),
                                new IDialogClickListener() {
                                    @Override
                                    public void onOK() {
                                        messagePresenter.uploadFile(activity,data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS).get(0));
                                    }
                                });

                    }
                    break;

                /*//No NONSENSE FILE PICKER
                case FILE_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        // Use the provided utility method to parse the result
                        final Activity activity = this;
                        List<Uri> files = Utils.getSelectedFilesFromResult(data);
                        for (Uri uri: files) {
                            messagePresenter.uploadFile(activity,uri.getPath());
                            // Do something with the result...
                            break;
                        }
                         */
            }
        }
        //addThemToView(photoPaths,filePaths);
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerContainer)) {
            drawerLayout.closeDrawer(drawerContainer);
        } else {
            if(searchIsActive)
            {
                messagePresenter.endSearchMessage();
                searchIsActive = false;
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onIMSCloseClick() {

        NavigationDrawerFragment navigationDrawerFragment = new NavigationDrawerFragment();
        navigationDrawerPresenter = new NavigationDrawerPresenter(navigationDrawerFragment, retrofit, PreferenceManager.get(this));
        navigationDrawerFragment.setPresenter(navigationDrawerPresenter);
        NavigationHelper.addFragment(getSupportFragmentManager(), navigationDrawerFragment, R.id.left_drawer); //R.id.left_drawer

    }

    @Override
    public void onChannelsCloseClick() {

        imNavigationDrawerFragment = new IMNavigationDrawerFragment();
        imNavigationDrawerPresenter = new IMNavigationDrawerPresenter(imNavigationDrawerFragment, retrofit, PreferenceManager.get(this));
        imNavigationDrawerFragment.setPresenter(imNavigationDrawerPresenter);
        NavigationHelper.addFragment(getSupportFragmentManager(), imNavigationDrawerFragment, R.id.left_drawer); //R.id.left_drawer

    }

    @Override
    public void onChannelClick(Channel channel) {
        drawerLayout.closeDrawer(drawerContainer);
        if(refreshIsActive){service.stop(); refreshIsActive = false;}
        //messagePresenter = null;
        //messagesFragment = null;
        messagePresenterActive = false;
        if(!mainPresenterActive) {
            //mainFragment = new UsersFragment();
            //mainPresenter = new UsersPresenter(mainFragment, retrofit, PreferenceManager.get(this));
            //mainFragment.setPresenter(mainPresenter);
            NavigationHelper.addFragment(getSupportFragmentManager(), mainFragment, R.id.main_container);
        }
        mainPresenter.onChannelClick(this, channel);
        if(!channel.name.equals(Channel.ALL_USERS_CHANNEL)) {
            navigationDrawerPresenter.joinChannel(this, channel);
        }
        mainPresenterActive = true;
    }
	
	@Override
    public void onChannelMessageClick(Channel channel) {
        drawerLayout.closeDrawer(drawerContainer);
        mainPresenterActive = false;
        //if(messagePresenterActive == false) {
            messagesFragment = new MessagesFragment();
            messagePresenter = new MessagesPresenter(messagesFragment, retrofitMessage, PreferenceManager.get(this), AbstractBaseFragment.getUserMail()!=null?AbstractBaseFragment.getUserMail():"");
            messagesFragment.setPresenter(messagePresenter);

            NavigationHelper.addFragment(getSupportFragmentManager(), messagesFragment, R.id.main_container);
        //}

        messagePresenter.onChannelClick(this, channel);
        messagePresenterActive = true;
    }

    //@Override
    //public void onRefreshMessageAlarm() {
    //    messagesFragment.onRefreshMessages();
    //}

    @Override
    public void onIMMessageClick(IM im) {
        drawerLayout.closeDrawer(drawerContainer);
        mainPresenterActive = false;
        //if(messagePresenterActive == false) {
            messagesFragment = new MessagesFragment();
            messagePresenter = new MessagesPresenter(messagesFragment, retrofitMessage, PreferenceManager.get(this), AbstractBaseFragment.getUserMail()!=null?AbstractBaseFragment.getUserMail():"");
            messagesFragment.setPresenter(messagePresenter);

            NavigationHelper.addFragment(getSupportFragmentManager(), messagesFragment, R.id.main_container);
        //}

        messagePresenter.onIMClick(this, im);
        messagePresenterActive = true;
    }

    /*@Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        if(messagePresenterActive && searchIsActive) {
                //if(refreshIsActive){service.stop();refreshIsActive = false;}
                searchIsActive = false;
                messagePresenter.endSearchMessage();
            }
        return true;
    }*/

    /*@Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        if(messagePresenterActive && searchIsActive) {
                //if(refreshIsActive){service.stop();refreshIsActive = false;}
            searchIsActive = false;
            messagePresenter.endSearchMessage();
        }
        return true;
    }*/

    @Override
    public boolean onQueryTextSubmit(String query) {
        CommonUtils.hideKeyboard(this);
        if(mainPresenterActive) {
            mainPresenter.searchUser(query.trim());
        }else if(messagePresenterActive) {
            //if(refreshIsActive){service.stop();refreshIsActive = false;}
            if(query.trim().length()<1 && searchIsActive==true)
            {
                searchIsActive = false;
                messagePresenter.endSearchMessage();
            }
            else if(query.trim().length()>0) {
                searchIsActive = true;
                messagePresenter.searchMessage(query.trim());
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(mainPresenterActive) {
            mainPresenter.searchUser(newText.trim());
        }else if(messagePresenterActive)
        {
            //if(refreshIsActive){service.stop();refreshIsActive = false;}
            if(newText.length()<1 && searchIsActive==true)
            {
                searchIsActive = false;
                messagePresenter.endSearchMessage();
            }
            else if(newText.length()>0) {
                searchIsActive = true;
                messagePresenter.searchMessage(newText.trim());
            }
        }
        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if(mainPresenterActive) {
                mainPresenter.searchUser(v.getText().toString().trim());
            }
            else if(messagePresenterActive)
            {
                //if(refreshIsActive){service.stop();refreshIsActive = false;}
                if(v.getText().toString().trim().length()<1 && searchIsActive==true)
                {
                    searchIsActive = false;
                    messagePresenter.endSearchMessage();
                }
                else if(v.getText().toString().trim().length()>0) {
                    searchIsActive = true;
                    messagePresenter.searchMessage(v.getText().toString().trim());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if(refreshIsActive){service.stop();refreshIsActive = false;}
        try { service.unbind(); }
        catch (Throwable t) { }
        try {
            //trimCache(getApplicationContext());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
}
