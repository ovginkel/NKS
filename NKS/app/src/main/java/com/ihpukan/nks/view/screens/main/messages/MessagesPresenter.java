package com.ihpukan.nks.view.screens.main.messages;

import android.app.Activity;
import android.text.TextUtils;

import com.ihpukan.nks.R;
import com.ihpukan.nks.common.DialogUtils;
import com.ihpukan.nks.common.IDialogClickListener;
import com.ihpukan.nks.common.PreferenceManager;
import com.ihpukan.nks.emoji.Lookup;
import com.ihpukan.nks.model.Attachment;
import com.ihpukan.nks.model.Channel;
import com.ihpukan.nks.model.EmojiWrapper;
import com.ihpukan.nks.model.IM;
import com.ihpukan.nks.model.MembersWrapper;
import com.ihpukan.nks.model.Message;
import com.ihpukan.nks.model.MessagesWrapper;
import com.ihpukan.nks.model.PublicMessageResponse;
import com.ihpukan.nks.model.SlackFile;
import com.ihpukan.nks.model.UploadResponse;
import com.ihpukan.nks.module.network.RestApiInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


public class MessagesPresenter implements MessagesContract.Presenter {

    private RestApiInterface retrofit;
    private MessagesContract.View viewMain;
    private PreferenceManager preferenceManager;
    private LruCache<String, Message> messageCache;
    private List<String> idMessages = new ArrayList<>();
    private List<Message> currentMessages = new ArrayList<>();
    private List<Message> currentMessagesBU = new ArrayList<>();
    private String queryMessage = "";
    public Channel currentChannel;
    public IM currentIM;
    private String latestTS;
    private MembersWrapper myMembersWrapper;
    public int numberOfNewMessages = 0;
    final public String myUserMail;

    private AppCompatActivity currentActivity;

    public MessagesPresenter(MessagesContract.View viewMain, Retrofit retrofit, PreferenceManager preferenceManager, String userMail) {
        this.viewMain = viewMain;
        this.retrofit = retrofit.create(RestApiInterface.class);
        this.preferenceManager = preferenceManager;
        this.myUserMail = userMail;
    }

    @Override
    public void onChannelClick(AppCompatActivity activity, Channel channel) {
        currentChannel = channel;
        currentIM = null;
        currentActivity = activity;
        if(channel.name!=null) {
            activity.getSupportActionBar().setTitle(channel.name);
        }
        else
        {
            activity.getSupportActionBar().setTitle(" ");
        }
        if(Lookup.emojiUrl.size()<=/*2446*/2449) //Append team specific emojis to online lookup list
        {
            retrofit.getAllEmojiList(this.preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(emojiLoadObserver);
        }
        loadMessages(activity, idMessages);
    }

    @Override
    public void onIMClick(AppCompatActivity activity, IM im) {
        currentIM = im;
        currentChannel = null;
        currentActivity = activity;
        //try
        {
            activity.getSupportActionBar().setTitle(im.member.profile.real_name);
        }
        //catch(java.lang.){}
        if(Lookup.emojiUrl.size()<=2446)
        {
            retrofit.getAllEmojiList(this.preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(emojiLoadObserver);
        }
        loadMessages(activity, idMessages);
    }

    @Override
    public void loadAllMessages() {
        if(currentChannel!=null) {
            if (!TextUtils.isEmpty(currentChannel.is_channel)) { //Load Channel
                retrofit.getAllMessages(preferenceManager.getToken(), currentChannel.id, "200", "true","0").subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(messagesLoadObserver);
            } else { //Load Group
                retrofit.getAllPrivMessages(preferenceManager.getToken(), currentChannel.id, "200", "true","0").subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(messagesLoadObserver);
            }
        }
        else if(currentIM != null)
        {
            retrofit.getAllIMMessages(preferenceManager.getToken(), currentIM.id, "200", "true","0").subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(messagesLoadObserver);
        }

    }

    @Override
    public void updateWithNewMessages() {
        if(currentChannel!=null) {
            if (!TextUtils.isEmpty(currentChannel.is_channel)) { //Load Channel
                retrofit.getAllMessages(preferenceManager.getToken(), currentChannel.id, "200", "false",latestTS).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(messagesAddObserver);
            } else { //Load Group
                retrofit.getAllPrivMessages(preferenceManager.getToken(), currentChannel.id, "200", "false",latestTS).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(messagesAddObserver);
            }
        }
        else if(currentIM != null)
        {
            retrofit.getAllIMMessages(preferenceManager.getToken(), currentIM.id, "200", "false",latestTS).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(messagesAddObserver);
        }

    }

    @Override
    public void loadAllUsers() {
        retrofit.getAllUsers(preferenceManager.getToken()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(usersLoadObserver);
    }

    private void loadMessages(final Activity activity, List<String> idMessages) {
        queryMessage = "";
        if (messageCache == null) return;
        if (idMessages == null || idMessages.isEmpty()) {
            currentMessagesBU.clear();
            currentMessagesBU.addAll(currentMessages);
            viewMain.displayMessages(null);
            return;
        }
        currentMessages = new ArrayList<>();
        Observable.just(idMessages).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                viewMain.showProgressBar();
            }

            @Override
            public void onNext(List<String> value) {
                for (String id : value) {
                    Message message = messageCache.get(id);
                    if (message != null) {
                        currentMessages.add(message);
                    }
                }
                sortMessages(currentMessages);
                latestTS = currentMessages.get(0).ts; //update latest TS
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                viewMain.hideProgressBar();
                viewMain.hideBackIcon();
                viewMain.displayMessages(currentMessages);
            }
        });
    }

    @Override
    public void sendMessage(final Activity activity, final String message) {
        String cleanMessage = message.replace("&","&amp;").replace("<","&lt").replace(">","&gt;");

        if(currentChannel!=null) {
            retrofit.publicMessage(preferenceManager.getToken(),currentChannel.id,cleanMessage,"true","true").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(messageSentObserver);
        }
        else if(currentIM!=null)
        {
            retrofit.publicMessage(preferenceManager.getToken(),currentIM.id,cleanMessage,"true","true").subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(messageSentObserver);
        }
    }

    @Override
    public void uploadFile(Activity activity, String filePath) {

        if(filePath!=null?filePath.length()>0:false)
        {
            File file = new File(filePath);

            // create RequestBody instance from file
            RequestBody filebody =
                    RequestBody.create(
                            MediaType.parse(filePath), //
                            file
                    );

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part content =
                    MultipartBody.Part.createFormData("file", file.getName(), filebody);


            if (currentChannel != null) {

                RequestBody channel = RequestBody.create(okhttp3.MultipartBody.FORM, currentChannel.id);
                RequestBody token = RequestBody.create(okhttp3.MultipartBody.FORM, preferenceManager.getToken());

                retrofit.uploadFile(token, channel, content).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(fileUploadedObserver);
            } else if (currentIM != null) {

                RequestBody channel = RequestBody.create(okhttp3.MultipartBody.FORM, currentIM.id);
                RequestBody token = RequestBody.create(okhttp3.MultipartBody.FORM, preferenceManager.getToken());

                retrofit.uploadFile(token, channel, content).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(fileUploadedObserver);
            }
        }
    }

    @Override
    public void endSearchMessage() {
        if(currentMessages.size()<currentMessagesBU.size())
        {
            currentMessages.clear();
            currentMessages.addAll(currentMessagesBU);
            if(currentMessagesBU.size()<currentMessages.size())
            {
                currentMessagesBU.clear();
                currentMessagesBU.addAll(currentMessages);
            }
        }
        viewMain.displayMessages(currentMessages);

    }

    @Override
    public void searchMessage(final String query) {
        if (queryMessage.equals(query)) return;
        queryMessage = query;
        Matcher<Message> matcher = new Matcher<Message>() {
            public boolean matches(Message message) {
                if(message.text == null && message.attachments != null)
                {
                    for (Attachment attachment: message.attachments)
                    {
                        if(attachment.text != null) {
                            return attachment.text.toUpperCase().contains(query.toUpperCase());
                        }
                        else if(attachment.fallback != null) {
                            return attachment.fallback.toUpperCase().contains(query.toUpperCase());
                        }
                    }
                }
                if(message.text == null && message.files != null)
                {
                    for (SlackFile slackfile: message.files)
                    {
                        if(slackfile.title != null) {
                            return slackfile.title.toUpperCase().contains(query.toUpperCase());
                        }
                        else if(slackfile.name != null) {
                            return slackfile.name.toUpperCase().contains(query.toUpperCase());
                        }
                    }
                }
                return message.text.toUpperCase().contains(query.toUpperCase());
            }
        };
        List<Message> searchResult = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            if(currentMessages.size()<currentMessagesBU.size())
            {
                currentMessages.clear();
                currentMessages.addAll(currentMessagesBU);
            }
            for (Message message : currentMessages) {
                if (matcher.matches(message)) {
                    searchResult.add(message);
                }
            }
            currentMessagesBU.clear();
            currentMessagesBU.addAll(currentMessages);
            viewMain.displayMessages(searchResult);
        } else {
            if(currentMessages.size()<currentMessagesBU.size())
            {
                currentMessages.clear();
                currentMessages.addAll(currentMessagesBU);
            }
            viewMain.displayMessages(currentMessages);
        }
    }

    interface Matcher<T> {
        boolean matches(T t);
    }

    private void sortMessages(List<Message> messages) {
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message mes1, Message mes2) {
                if (mes1.ts.equals(mes2.ts)) {
                    return 0;
                }
                if (mes1.ts == null) {
                    return -1;
                }
                if (mes2.ts == null) {
                    return 1;
                }
                return mes2.ts.compareTo(mes1.ts); //OvG: most recent listed on top.
            }
        });
    }

    private Observer<EmojiWrapper> emojiLoadObserver = new Observer<EmojiWrapper>() {
        @Override
        public void onSubscribe(Disposable d) {
            /*viewMain.showProgressBar();*/
        }

        @Override
        public void onNext(EmojiWrapper emojiWrapper) {
            Lookup.emojiUrl.putAll(emojiWrapper.emoji);
        }

        @Override
        public void onError(Throwable e) {
            viewMain.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    private Observer<MessagesWrapper> messagesLoadObserver = new Observer<MessagesWrapper>() {
        @Override
        public void onSubscribe(Disposable d) {
            viewMain.showProgressBar();
        }

        @Override
        public void onNext(MessagesWrapper messagesWrapper) {
            messageCache = null;
            idMessages = null;
            if(messagesWrapper.messages != null?messagesWrapper.messages.size()>0:false) {
                viewMain.hideBackIcon();
                messageCache = new LruCache<>(messagesWrapper.messages.size());
                idMessages = new ArrayList<>();
                for (int i = 0; i < messagesWrapper.messages.size(); i++) {
                    Message message = messagesWrapper.messages.get(i);
                    messageCache.put(message.ts, message);
                    idMessages.add(message.ts);
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            viewMain.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {
            loadAllUsers(); //Populate user fields prior to showing messages.
            //viewMain.hideProgressBar();
        }
    };

    private int numberOfAddedMessages;
    private Observer<MessagesWrapper> messagesAddObserver = new Observer<MessagesWrapper>() {
        @Override
        public void onSubscribe(Disposable d) {
            viewMain.showProgressBar();
        }

        @Override
        public void onNext(MessagesWrapper messagesWrapper) {

            if(messagesWrapper.messages != null?messagesWrapper.messages.size()>0:false) {
                if(messageCache==null)
                {
                    loadAllMessages();
                }
                else
                {
                    messageCache.resize(messagesWrapper.messages.size()+messageCache.size());
                    //numberOfNewMessages = 0; //Set to zero only after notification
                    numberOfAddedMessages = 0;
                    for (int i = 0; i < messagesWrapper.messages.size(); i++) {
                        Message message = messagesWrapper.messages.get(i);
                        if(!idMessages.contains(message.ts))
                        {
                            numberOfNewMessages++; //Still need to subtract messages user has sent.
                            numberOfAddedMessages++;
                            messageCache.put(message.ts, message);
                            idMessages.add(message.ts);
                        }
                    }
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            viewMain.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {
            //loadAllUsers(); //Should already be loaded.
            if(numberOfAddedMessages>0) { //Only update if new messages received...
                linkUsersToMessages(myMembersWrapper);
                loadMessages(currentActivity, idMessages);
            }
            else {
                viewMain.hideProgressBar();
            }
        }
    };

    private void linkUsersToMessages(MembersWrapper membersWrapper)
    {
        if(membersWrapper != null?(membersWrapper.members != null):false)
        {
            for (int i = 0; i < membersWrapper.members.size(); i++)
            {
                if(messageCache != null)
                {
                    for(int j = 0; j < messageCache.size(); j++)
                    {
                        if(messageCache.get(idMessages.get(j))!=null)
                        {
                            if(messageCache.get(idMessages.get(j)).user!=null)
                            {
                                if(messageCache.get(idMessages.get(j)).member==null) {
                                    if (messageCache.get(idMessages.get(j)).user.equalsIgnoreCase(membersWrapper.members.get(i).id)) {
                                        if
                                                (
                                                (membersWrapper.members.get(i).profile != null) ?
                                                        (
                                                                (membersWrapper.members.get(i).profile.email != null) ?
                                                                        membersWrapper.members.get(i).profile.email.equalsIgnoreCase(myUserMail)
                                                                        : false
                                                        )
                                                        : false
                                                ) //Subtract messages user has sent
                                        {
                                            if(numberOfNewMessages>0){numberOfNewMessages--;}
                                        }
                                        Message message = messageCache.get(idMessages.get(j));
                                        message.member = membersWrapper.members.get(i);
                                        messageCache.put(idMessages.get(j), message);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Observer<MembersWrapper> usersLoadObserver = new Observer<MembersWrapper>() {
        @Override
        public void onSubscribe(Disposable d) {
            viewMain.showProgressBar();
        }

        @Override
        public void onNext(MembersWrapper membersWrapper) {
            myMembersWrapper = membersWrapper;
            linkUsersToMessages(membersWrapper);
        }

        @Override
        public void onError(Throwable e) {
            viewMain.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {
            loadMessages(currentActivity,idMessages); viewMain.hideProgressBar(); viewMain.hideBackIcon();
        }
    };

    @Override
    public MembersWrapper getMembersWrapper()
    {
        return myMembersWrapper;
    }

    private Observer<PublicMessageResponse> messageSentObserver = new Observer<PublicMessageResponse>() {
        @Override
        public void onSubscribe(Disposable d) {
            viewMain.showProgressBar();
        }

        @Override
        public void onNext(PublicMessageResponse publicMessageResponse) {

        }

        @Override
        public void onError(Throwable e) {
            viewMain.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {
            //loadAllMessages();
            updateWithNewMessages();
            //viewMain.hideProgressBar();
        }
    };

    void Dialogresponse()
    {

    }

    private Observer<UploadResponse> fileUploadedObserver = new Observer<UploadResponse>() {
        @Override
        public void onSubscribe(Disposable d) {
            viewMain.showProgressBar();
        }

        @Override
        public void onNext(UploadResponse uploadResponse) {
            if(uploadResponse.ok)
            {
                DialogUtils.displayChooseOkNoDialog(currentActivity,currentActivity.getString(R.string.file_upload_success),currentActivity.getString(R.string.tried),
                        new IDialogClickListener() {
                            @Override
                            public void onOK() {
                            }
                        }
                        );
            }
            else
            {
                DialogUtils.displayChooseOkNoDialog(currentActivity,currentActivity.getString(R.string.file_upload_failure),currentActivity.getString(R.string.tried),
                        new IDialogClickListener() {
                            @Override
                            public void onOK() {
                            }
                        }
                );

            }
        }

        @Override
        public void onError(Throwable e) {
            viewMain.showErrorMessage(e.getMessage());
        }

        @Override
        public void onComplete() {
            updateWithNewMessages();
            viewMain.hideProgressBar();
            viewMain.hideBackIcon();
        }
    };

}
