package com.example.instagram.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.instagram.interfaces.ScrollEventListener;
import com.parse.ParseUser;

import java.util.Date;

public class UserPostDataSourceFactory extends DataSource.Factory<Date, Post>{

    private final ParseUser user;
    public MutableLiveData<UserPostDataSource> postLiveData;

    public UserPostDataSourceFactory(ParseUser user) {
        this.user = user;
    }

    @NonNull
    @Override
    public DataSource<Date, Post> create() {
        UserPostDataSource post = new UserPostDataSource(user);

        postLiveData = new MutableLiveData<>();
        postLiveData.postValue(post);
        return post;
    }
}
