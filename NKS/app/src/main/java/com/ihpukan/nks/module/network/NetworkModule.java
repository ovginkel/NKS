package com.ihpukan.nks.module.network;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ihpukan.nks.common.PreferenceManager;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.picasso.Picasso;

import java.net.InetSocketAddress;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class NetworkModule {

    private String baseUrl;
    private PreferenceManager preferenceManager;
    public NetworkModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkhttpClient(Application application) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        /*if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(httpLoggingInterceptor);
        }*/
        httpClient.retryOnConnectionFailure(true);
        httpClient.connectTimeout(2, java.util.concurrent.TimeUnit.MINUTES);
        OkHttpClient client = httpClient.build();
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(client);
        Picasso picasso = new Picasso.Builder(application)
                .downloader(okHttp3Downloader)
                .build();
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored) {
        }
        return httpClient.build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.setPrettyPrinting().serializeNulls();
        gsonBuilder.serializeNulls();
        return gsonBuilder.create();
    }

}
