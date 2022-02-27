package com.example.instagram;

import android.app.Application;
import android.os.Build;

import com.example.instagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
 
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.applicationId)
                .clientKey(BuildConfig.clientKey)
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
