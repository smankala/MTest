package com.santhosh.mtest;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


import com.santhosh.mtest.service.PersistenceService;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class MTestApplication extends Application {

    private ServiceConnection persistenceConnection;
    IPersistenceInterface persistenceInterface;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        initPersistenceService();
        initRetrofit();
    }


    private void initPersistenceService() {
        persistenceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                persistenceInterface = IPersistenceInterface.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                    persistenceInterface = null;
            }
        };

        Intent intent = new Intent(this, PersistenceService.class);
        intent.setAction(PersistenceService.class.getName());
        bindService(intent, persistenceConnection, BIND_AUTO_CREATE);
    }

    private void initRetrofit(){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);


      retrofit = new Retrofit.Builder()
                .baseUrl("https://mirror-android-test.herokuapp.com/")
                .client(httpClient.build())
              .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetroFit() {
        return retrofit;
    }

    public IPersistenceInterface getPersistenceService() {
        return persistenceInterface;
    }
}
