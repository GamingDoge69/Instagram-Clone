package com.example.instagram.models;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import com.example.instagram.interfaces.ScrollEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

public class PostDataSource extends ItemKeyedDataSource<Date, Post> {
    private final String TAG = "PostDataSource";
    private final ScrollEventListener scrollEventListener;

    public PostDataSource(ScrollEventListener scrollEventListener) {
        this.scrollEventListener = scrollEventListener;
    }

    @NonNull
    @Override
    public Date getKey(@NonNull Post post) {
        return post.getCreatedAt();
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Date> loadParams, @NonNull LoadCallback<Post> loadCallback) {
        ParseQuery.getQuery(Post.class)
                .setLimit(loadParams.requestedLoadSize)
                .include(Post.KEY_USER)
                .addDescendingOrder(Post.KEY_CREATED_AT)
                .whereLessThan(Post.KEY_CREATED_AT, new Date(loadParams.key.getTime()))
                .findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "done: ", e);
                            return;
                        }
                        loadCallback.onResult(posts);
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Date> loadParams, @NonNull LoadCallback<Post> loadCallback) {
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Date> loadInitialParams, @NonNull LoadInitialCallback<Post> loadInitialCallback) {
        ParseQuery.getQuery(Post.class)
                .setLimit(loadInitialParams.requestedLoadSize)
                .include(Post.KEY_USER)
                .addDescendingOrder(Post.KEY_CREATED_AT)
                .findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "done: ", e);
                            return;
                        }
                        loadInitialCallback.onResult(posts);
                        scrollEventListener.scrollToNewPosition(0);
                    }
                });
    }
}
