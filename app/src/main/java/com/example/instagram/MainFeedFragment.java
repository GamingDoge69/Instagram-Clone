package com.example.instagram;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.adapters.MainActivityPagerAdapter;
import com.example.instagram.adapters.MainFeedAdapter;
import com.example.instagram.databinding.FragmentMainFeedBinding;
import com.example.instagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFeedFragment extends Fragment {
    private final static String TAG = "MainFeedFragment";
    FragmentMainFeedBinding binding;
    MainFeedAdapter adapter;
    List<Post> posts = new ArrayList<>();

    private Context backupContext;
    @Nullable
    @Override
    public Context getContext() {
        // Higher Chance of not failing
        Context context;
        context = super.getContext();
        if (context != null) return context;

        context = backupContext;
        if (context != null) return context;

        context = getActivity();
        return context;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        backupContext = context;
    }

    public MainFeedFragment() {
        // Required empty public constructor
    }

    public static MainFeedFragment newInstance() {
        MainFeedFragment fragment = new MainFeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainFeedBinding.inflate(inflater, container, false);
        adapter = new MainFeedAdapter(posts, getContext());
        binding.rvPosts.setAdapter(adapter);
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts();

        return binding.getRoot();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "done: ", e);
                    return;
                }
                MainFeedFragment.this.posts.addAll(posts);
                adapter.notifyItemRangeInserted(0, posts.size());
            }
        });
    }
}