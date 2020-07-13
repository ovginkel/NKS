package com.ihpukan.nks.view.screens.main.imdrawer;

import androidx.collection.LruCache; //import android.support.v4.util.LruCache;

import com.ihpukan.nks.common.PreferenceManager;
import com.ihpukan.nks.model.IM;
import com.ihpukan.nks.model.IMWrapper;
import com.ihpukan.nks.model.MembersWrapper;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.module.network.RestApiInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class IMNavigationDrawerPresenter implements IMNavigationDrawerContract.Presenter {

    private RestApiInterface retrofit;
    private IMNavigationDrawerContract.View viewNavDrawer;
    private PreferenceManager preferenceManager;

    private LruCache<String, IM> imCache;
    private List<IM> currentIMs;
    private List<String> idIMs = new ArrayList<>();

    public IMNavigationDrawerPresenter(IMNavigationDrawerContract.View viewNavDrawer, Retrofit retrofit,
                                     PreferenceManager preferenceManager) {
        this.viewNavDrawer = viewNavDrawer;
        this.retrofit = retrofit.create(RestApiInterface.class);
        this.preferenceManager = preferenceManager;
    }

    @Override
    public void loadIMs() {
        retrofit.getIMs(preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(imsLoadObserver);
    }

    private Observer<IMWrapper> imsLoadObserver = new Observer<IMWrapper>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(IMWrapper imWrapper) {
            //noinspection SimplifiableConditionalExpression,SimplifiableConditionalExpression
            if(imWrapper!=null?(imWrapper.ims!=null?imWrapper.ims.size()>0:false):false) {
                imCache = new LruCache<>(imWrapper.ims.size());
                idIMs = new ArrayList<>();
                for (IM im : imWrapper.ims) {
                    imCache.put(im.id, im);
                    idIMs.add(im.id);
                }
                loadAllUsers();
            }
        }

        @Override
        public void onError(Throwable e) {
            viewNavDrawer.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    @Override
    public void loadAllUsers() {
        retrofit.getAllUsers(preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(usersLoadObserver);
    }

    private Observer<MembersWrapper> usersLoadObserver = new Observer<MembersWrapper>() {
        @Override
        public void onSubscribe(Disposable d) {
            //viewMain.showProgressBar();
        }

        @Override
        public void onNext(MembersWrapper membersWrapper) {
            //usersCache = new LruCache<>(membersWrapper.members.size());
            currentIMs = new ArrayList<>();
            int memberCount = (membersWrapper!=null)?((membersWrapper.members!=null)?membersWrapper.members.size():0):0;
            for (int i = 0; i < memberCount; i++) {
                for(int j = 0; j < imCache.size(); j++)
                {
                    if(imCache.get(idIMs.get(j)).user!=null)
                    {
                        if(imCache.get(idIMs.get(j)).member==null) {
                            if (imCache.get(idIMs.get(j)).user.equalsIgnoreCase(membersWrapper.members.get(i).id)) {
                                IM im = imCache.get(idIMs.get(j));
                                im.member = membersWrapper.members.get(i);
                                if(!im.member.deleted) { //Do not list inactive profiles for im
                                    imCache.put(idIMs.get(j), im);
                                    currentIMs.add(im);
                                }
                            }
                        }
                    }

                }
            }
        }



        @Override
        public void onError(Throwable e) {
            viewNavDrawer.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {
            viewNavDrawer.loadIMsComplete(currentIMs); //viewNavDrawer.hideProgressBar();
        }
    };

    @Override
    public void loadProfile() {
        retrofit.getAuthUser(preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(profileLoadObserver);
    }

    private Observer<User> profileLoadObserver = new Observer<User>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(User user) {
            viewNavDrawer.loadProfileComplete(user);
        }

        @Override
        public void onError(Throwable e) {
            viewNavDrawer.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };
}
