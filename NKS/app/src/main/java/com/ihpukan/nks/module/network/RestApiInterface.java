package com.ihpukan.nks.module.network;

import com.ihpukan.nks.model.ChannelJoin;
import com.ihpukan.nks.model.ChannelWrapper;
import com.ihpukan.nks.model.EmojiWrapper;
import com.ihpukan.nks.model.GroupsWrapper;
import com.ihpukan.nks.model.IMWrapper;
import com.ihpukan.nks.model.MembersWrapper;
import com.ihpukan.nks.model.MessagesWrapper;
import com.ihpukan.nks.model.OauthAccess;
import com.ihpukan.nks.model.OpenIMWrapper;
import com.ihpukan.nks.model.PublicMessageResponse;
import com.ihpukan.nks.model.UploadResponse;
import com.ihpukan.nks.model.User;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RestApiInterface {

    @GET("oauth.access")
    Observable<OauthAccess> oauthAccess(@Query("client_id") String clientId, @Query("client_secret") String clientSecret,
                                        @Query("code") String code, @Query("redirect_uri") String redirUri);

    @GET("users.profile.get")
    Observable<User> getAuthUser(@Query("token") String token);

    @GET("users.profile.get")
    Observable<User> getUser(@Query("token") String token, @Query("code") String code);

    @GET("channels.list")
    Observable<ChannelWrapper> getChannels(@Query("token") String token);

    @GET("im.list")
    Observable<IMWrapper> getIMs(@Query("token") String token);

    @GET("groups.list")
    Observable<GroupsWrapper> getGroups(@Query("token") String token);

    @GET("users.list")
    Observable<MembersWrapper> getAllUsers(@Query("token") String token);

    @GET("channels.history")
    Observable<MessagesWrapper> getAllMessages(@Query("token") String token,
                                               @Query("channel") String channel,
                                               @Query("count") String count,
                                               @Query("inclusive") String inclusive,
                                               @Query("oldest") String oldest);

    @GET("im.history")
    Observable<MessagesWrapper> getAllIMMessages(@Query("token") String token,
                                               @Query("channel") String channel,
                                               @Query("count") String count,
                                               @Query("inclusive") String inclusive,
                                                 @Query("oldest") String oldest);

    @FormUrlEncoded
    @POST("im.open")
    Observable<OpenIMWrapper> openIM(@Field("token") String token,
                                     @Field("user") String user);

    @FormUrlEncoded
    @POST("conversations.join")
    Observable<ChannelJoin> joinChannel(@Field("token") String token,
                                        @Field("channel") String channel);
    @FormUrlEncoded
    @POST("emoji.list")
    Observable<EmojiWrapper> getAllEmojiList(@Field("token") String token);

    @GET("groups.history")
    Observable<MessagesWrapper> getAllPrivMessages(@Query("token") String token,
                                               @Query("channel") String channel,
                                               @Query("count") String count,
                                               @Query("inclusive") String inclusive,
                                               @Query("oldest") String oldest);

    @FormUrlEncoded
    @POST("chat.postMessage")
    Observable<PublicMessageResponse> publicMessage(@Field("token") String token,
                                                    @Field("channel") String channel,
                                                    @Field("text") String message, //String message,
                                                    @Field("as_user") String asUser,
                                                    @Field("link_names") String linkNames
                                           ); // @Query("username") String userName
    @Multipart
    @POST("files.upload")
    Observable<UploadResponse> uploadFile(@Part("token") RequestBody token,
                                           @Part("channels") RequestBody channel,
                                            @Part MultipartBody.Part file);

}
