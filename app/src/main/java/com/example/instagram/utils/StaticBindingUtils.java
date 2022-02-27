package com.example.instagram.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Objects;

public class StaticBindingUtils {
    @BindingAdapter("parseFileToDrawable")
    public static void parseFileToDrawable(ImageView view, ParseFile parseFile) {
        parseFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e != null) return;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                view.setImageBitmap(bitmap);
            }
        });
    }

    @BindingAdapter("getUserProfileDrawable")
    public static void getUserProfileDrawable(ImageView view, ParseUser parseUser) {
        Objects.requireNonNull(parseUser.getParseFile("profilePicture")).getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e != null) return;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                view.setImageBitmap(bitmap);
            }
        });
    }
}
