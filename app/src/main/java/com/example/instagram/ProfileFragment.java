package com.example.instagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.instagram.adapters.MainFeedAdapter;
import com.example.instagram.adapters.ProfileFeedAdapter;
import com.example.instagram.databinding.FragmentMainFeedBinding;
import com.example.instagram.databinding.FragmentProfileBinding;
import com.example.instagram.models.Post;
import com.example.instagram.models.PostDataSourceFactory;
import com.example.instagram.models.UserPostDataSource;
import com.example.instagram.models.UserPostDataSourceFactory;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private final static String TAG = "ProfileFragment";
    private final static int PAGE_SIZE = 15;

    private Context backupContext;
    private ParseUser user;
    private ProfileFeedAdapter adapter;
    private FragmentProfileBinding binding;
    LiveData<PagedList<Post>> posts;

    private File photoFile;

    ActivityResultLauncher<Intent> launchCameraResultHandler = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        saveImageToProfile();
                    }
                }
            });

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

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(ParseUser user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.setUser(user);
        adapter = new ProfileFeedAdapter(getContext());
        binding.rvPosts.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);

        binding.rvPosts.setLayoutManager(gridLayoutManager);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .build();
        UserPostDataSourceFactory factory = new UserPostDataSourceFactory(user);
        posts = new LivePagedListBuilder<>(factory, config).build();
        posts.observe(getViewLifecycleOwner(), new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(PagedList<Post> posts) {
                adapter.submitList(posts);
                binding.swipeRefresh.setRefreshing(false);
            }
        });

        if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            binding.ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
                        launchCameraToSetProfilePicture();
                }
            });
        }

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                factory.postLiveData.getValue().invalidate();
            }
        });


        return binding.getRoot();
    }

    private void launchCameraToSetProfilePicture() {
        String photoFileName = "photo.jpg";
        photoFile = getPhotoFileUri(photoFileName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            launchCameraResultHandler.launch(intent);
        }
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void saveImageToProfile() {
        Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFile.getName()));
        Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(rawTakenImage, rawTakenImage.getWidth(), rawTakenImage.getHeight(), true);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File resizedFile = getPhotoFileUri(photoFile.getName() + "_resized");
        try {
            resizedFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ParseUser.getCurrentUser().put("profilePicture", new ParseFile(resizedFile));
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getContext(), R.string.profile_picture_changed_toast_text, Toast.LENGTH_SHORT).show();
                binding.ivProfilePicture.setImageBitmap(resizedBitmap);
            }
        });
    }
}