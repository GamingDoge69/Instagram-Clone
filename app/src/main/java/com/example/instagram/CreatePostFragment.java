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

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.instagram.databinding.FragmentCreatePostBinding;
import com.example.instagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class CreatePostFragment extends Fragment {
    private final static String TAG = "CreatePostFragment";


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


    ActivityResultLauncher<Intent> launchCameraResultHandler = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        binding.ivPhoto.setImageBitmap(takenImage);
                        handlePostLocking();
                    }
                }
            });

    FragmentCreatePostBinding binding;
    File photoFile;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    public static CreatePostFragment newInstance(/* Args */) {
        CreatePostFragment fragment = new CreatePostFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);
        binding.btnSubmit.setVisibility(View.INVISIBLE);
        binding.pbLoading.setIndeterminate(true);

        binding.tietDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                handlePostLocking();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = binding.tietDescription.getText().toString();
                if (description.isEmpty() || photoFile == null || binding.ivPhoto.getDrawable() == null) {
                    return;
                }
                savePost(description, photoFile, ParseUser.getCurrentUser());
            }
        });

        binding.btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        return binding.getRoot();
    }

    private void launchCamera() {
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

    private void handlePostLocking() {
        if (!binding.tietDescription.getText().toString().isEmpty() &&
                binding.ivPhoto.getDrawable() != null &&
                photoFile != null)
            binding.btnSubmit.setVisibility(View.VISIBLE);
        else
            binding.btnSubmit.setVisibility(View.INVISIBLE);
    }

    private void lockFrag() {
        binding.pbLoading.setVisibility(View.VISIBLE);
        binding.tietDescription.setEnabled(false);
        binding.btnTakePicture.setEnabled(false);
    }

    private void unlockFrag() {
        binding.pbLoading.setVisibility(View.INVISIBLE);
        binding.tietDescription.setEnabled(true);
        binding.btnTakePicture.setEnabled(true);
    }

    private void savePost(String description, File imageFile, ParseUser user) {
        lockFrag();
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(imageFile));
        post.setUser(user);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                unlockFrag();
                if (e != null) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.tietDescription.setText("");
                binding.ivPhoto.setImageResource(0);
                photoFile = null;
                binding.btnSubmit.setVisibility(View.INVISIBLE);
            }
        });
    }
}