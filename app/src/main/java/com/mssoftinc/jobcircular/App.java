package com.mssoftinc.jobcircular;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
    }
}
