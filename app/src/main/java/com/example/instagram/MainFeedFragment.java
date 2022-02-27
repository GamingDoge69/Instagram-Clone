package com.example.instagram;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.adapters.MainFeedAdapter;
import com.example.instagram.databinding.FragmentMainFeedBinding;
import com.example.instagram.interfaces.ScrollEventListener;
import com.example.instagram.models.Post;
import com.example.instagram.models.PostDataSourceFactory;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFeedFragment extends Fragment implements ScrollEventListener {
    private final static int NEW_POSTS_REQUEST_LIMIT = 20;
    private final static String TAG = "MainFeedFragment";
    FragmentMainFeedBinding binding;
    MainFeedAdapter adapter;
    LiveData<PagedList<Post>> posts;

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
        binding = FragmentMainFeedBinding.inflate(inflater, container, false);

        adapter = new MainFeedAdapter(getContext());
        binding.rvPosts.setAdapter(adapter);
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));


        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(NEW_POSTS_REQUEST_LIMIT)
                .setInitialLoadSizeHint(NEW_POSTS_REQUEST_LIMIT)
                .build();
        PostDataSourceFactory factory = new PostDataSourceFactory(this);
        posts = new LivePagedListBuilder<>(factory, config).build();
        posts.observe(getViewLifecycleOwner(), new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(PagedList<Post> posts) {
                adapter.submitList(posts);
                binding.swipeRefresh.setRefreshing(false);
            }
        });

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                factory.postLiveData.getValue().invalidate();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void scrollToNewPosition(int position) {
        binding.rvPosts.post(new Runnable() {
            @Override
            public void run() {
                binding.rvPosts.scrollToPosition(0);
            }
        });
    }
}