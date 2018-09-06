package com.ihpukan.nks.view.screens.main.users;

import android.app.Activity;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.ihpukan.nks.R;
import com.ihpukan.nks.common.PreferenceManager;
import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.MembersWrapper;
import com.ihpukan.nks.model.OpenIMWrapper;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.module.network.RestApiInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.internal.Collection;
import retrofit2.Retrofit;

import static com.ihpukan.nks.model.Channel.ALL_USERS_CHANNEL;

public class UsersPresenter implements UsersContract.Presenter {

    private RestApiInterface retrofit;
    private UsersContract.View viewMain;
    private PreferenceManager preferenceManager;
    private LruCache<String, User> usersCache;
    private List<User> currentUsers = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<String> idUsers = new ArrayList<>();
    private String queryUser = "";
    private Channel currentChannel;

    public UsersPresenter(UsersContract.View viewMain, Retrofit retrofit, PreferenceManager preferenceManager) {
        this.viewMain = viewMain;
        this.retrofit = retrofit.create(RestApiInterface.class);
        this.preferenceManager = preferenceManager;
    }

    @Override
    public void onChannelClick(AppCompatActivity activity, Channel channel) {
        currentChannel = channel;
        //currentActivity = activity;

        if (channel.name.equals(ALL_USERS_CHANNEL)) {
            activity.getSupportActionBar().setTitle(R.string.all_users_title);
        } else {
            activity.getSupportActionBar().setTitle(channel.name);
        }

        if (channel.name.equals(ALL_USERS_CHANNEL)) {
            loadUsers(activity);
        } else {

            loadUsers(activity, channel.members);
        }
    }

    @Override
    public void loadAllUsers() {
        retrofit.getAllUsers(preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(membersLoadObserver);
    }

    private void loadUsers(final Activity activity, List<String> idUsers) {
        queryUser = "";
        if (usersCache == null) return;
        if (idUsers == null || idUsers.isEmpty()) {
            viewMain.displayUsers(null);
            return;
        }
        currentUsers = new ArrayList<>();
        Observable.just(idUsers).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                viewMain.showProgressBar();
            }

            @Override
            public void onNext(List<String> value) {
                for (String id : value) {
                    User user = usersCache.get(id);
                    if (user != null) {
                        currentUsers.add(user);
                    }
                    else //not in cache
                    {
                        for (java.util.Iterator<User> i = allUsers.iterator(); i.hasNext();) {
                            User usr = i.next();
                            if(user.id == id)
                            {
                                currentUsers.add(usr);
                                usersCache.put(usr.id,usr);
                            }
                        }
                    }
                }
                sortUsers(currentUsers);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                viewMain.hideProgressBar();
                viewMain.hideBackIcon();
                viewMain.displayUsers(currentUsers);
            }
        });
    }

    private void loadUsers(Activity activity) { //OvG: Not functioning properly
        queryUser = "";
        if (usersCache == null) return;
        viewMain.showProgressBar();
        currentUsers = new ArrayList<>(usersCache.size());
        currentUsers.addAll(usersCache.snapshot().values());
        allUsers = new ArrayList<>();
        allUsers.addAll(currentUsers);
        sortUsers(currentUsers);
        viewMain.hideProgressBar();
        viewMain.hideBackIcon();
        //viewMain.displayUsers(currentUsers);
        idUsers.clear();
        for (java.util.Iterator<User> i = allUsers.iterator(); i.hasNext();) { //Required for user load fix
            User usr = i.next();
            idUsers.add(usr.id);
        }
        this.loadUsers(activity, idUsers); //Required for user load fix
    }

    public void openIM(final Activity activity, final String userid) {
        //
        retrofit.openIM(preferenceManager.getToken(),userid).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<OpenIMWrapper>() {
            @Override
            public void onSubscribe(Disposable d) {
                viewMain.showProgressBar();
            }

            @Override
            public void onNext(OpenIMWrapper imWrapper) {
                Toast.makeText(activity, R.string.im_activated, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                viewMain.showErrorMessage(e.getMessage());
            }

            @Override
            public void onComplete() {
                viewMain.hideProgressBar(); viewMain.hideBackIcon();
            }
        });
    }

    @Override
    public void searchUser(final String query) {
        if (queryUser.equals(query)) return;
        queryUser = query;
        Matcher<User> matcher = new Matcher<User>() {
            public boolean matches(User user) {
                return user.profile.real_name.toUpperCase().contains(query.toUpperCase());
            }
        };
        List<User> searchResult = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            for (User user : currentUsers) {
                if (matcher.matches(user)) {
                    searchResult.add(user);
                }
            }
            viewMain.displayUsers(searchResult);
        } else {
            viewMain.displayUsers(currentUsers);
        }
    }

    interface Matcher<T> {
        boolean matches(T t);
    }

    private void sortUsers(List<User> users) {
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                if (user1.profile.real_name.equals(user2.profile.real_name)) {
                    return 0;
                }
                if (user1.profile.real_name == null) {
                    return -1;
                }
                if (user2.profile.real_name == null) {
                    return 1;
                }
                return user1.profile.real_name.compareTo(user2.profile.real_name);
            }
        });
    }

    private Observer<MembersWrapper> membersLoadObserver = new Observer<MembersWrapper>() {
        @Override
        public void onSubscribe(Disposable d) {
            viewMain.showProgressBar();
        }

        @Override
        public void onNext(MembersWrapper membersWrapper) {
            if(membersWrapper!=null?(membersWrapper.members!=null?membersWrapper.members.size()>0:false):false) {
                usersCache = new LruCache<>(membersWrapper.members.size());
                idUsers = new ArrayList<>();
                currentUsers.clear();
                if(currentChannel != null?(currentChannel.members == null):false) {
                    currentChannel.members = new ArrayList<String>();
                }
                for (int i = 0; i < membersWrapper.members.size(); i++) {
                    User user = membersWrapper.members.get(i);

                    if ((currentChannel != null) ? (currentChannel.members != null) : false) {
                        if (currentChannel.members.contains(user.id)) {
                            usersCache.put(user.id, user);
                            idUsers.add(user.id);
                            currentUsers.add(user);
                        }
                        else if(currentChannel.name.equals(ALL_USERS_CHANNEL))
                        {
                            usersCache.put(user.id, user);
                            idUsers.add(user.id);
                            currentUsers.add(user);
                        }
                    } else {
                        if(currentChannel != null)
                        {
                            currentChannel.members.add(user.id);
                        }
                        usersCache.put(user.id, user);
                        idUsers.add(user.id);
                        currentUsers.add(user);
                    }
                }
            }
            else
            {
                idUsers = new ArrayList<>();
            }
        }

        @Override
        public void onError(Throwable e) {
            viewMain.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {
            /*if(currentUsers.size()>0)
            {
                sortUsers(currentUsers);
                viewMain.displayUsers(currentUsers);
            }*/
            viewMain.hideProgressBar(); viewMain.hideBackIcon();
        }
    };

}
