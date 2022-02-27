package com.example.instagram.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.instagram.MainActivity;
import com.example.instagram.interfaces.ProfileViewRequestListener;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

    @BindingAdapter("setRelativeTimeText")
    public static void getUserProfileDrawable(TextView view, Date date) {
        String time;
        long diff = (System.currentTimeMillis() - date.getTime()) / 1000;
        if (diff < 5)
            time = "Just now";
        else if (diff < 60)
            time = String.format(Locale.ENGLISH, "%ds",diff);
        else if (diff < 60 * 60)
            time = String.format(Locale.ENGLISH, "%dm", diff / 60);
        else if (diff < 60 * 60 * 24)
            time = String.format(Locale.ENGLISH, "%dh", diff / (60 * 60));
        else if (diff < 60 * 60 * 24 * 30)
            time = String.format(Locale.ENGLISH, "%dd", diff / (60 * 60 * 24));
        else {
            Calendar now = Calendar.getInstance();
            Calendar then = Calendar.getInstance();
            then.setTime(date);
            if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR)) {
                time = String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                        + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
            } else {
                time = String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                        + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                        + " " + String.valueOf(then.get(Calendar.YEAR) - 2000);
            }
        }
        view.setText(time);
    }

    @BindingAdapter("profileLauncher")
    public static void getUserProfileDrawable(View view, ParseUser user) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileViewRequestListener listener = MainActivity.tryToGetProfileLauncher();
                if (listener != null) {
                    listener.viewProfile(user);
                }
            }
        });
    }

}
