package com.example.instagram.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.instagram.interfaces.ScrollEventListener;

import java.util.Date;

public class PostDataSourceFactory extends DataSource.Factory<Date, Post>{

    private final ScrollEventListener scrollEventListener;
    public MutableLiveData<PostDataSource> postLiveData;

    public PostDataSourceFactory(ScrollEventListener scrollEventListener) {
        this.scrollEventListener = scrollEventListener;
    }

    @NonNull
    @Override
    public DataSource<Date, Post> create() {
        PostDataSource post = new PostDataSource(scrollEventListener);

        postLiveData = new MutableLiveData<>();
        postLiveData.postValue(post);
        return post;
    }
}
