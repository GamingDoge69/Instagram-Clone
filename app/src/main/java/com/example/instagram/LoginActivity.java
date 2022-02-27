package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().isAuthenticated()) {
            goToMainActivity();
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(Objects.requireNonNull(binding.tietUsername.getText()).toString(),
                        Objects.requireNonNull(binding.tietPassword.getText()).toString()
                );
            }
        });

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup(Objects.requireNonNull(binding.tietUsername.getText()).toString(),
                        Objects.requireNonNull(binding.tietPassword.getText()).toString());
            }
        });

        binding.tietPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE && !binding.tietUsername.getText().toString().isEmpty()) {
                    login(Objects.requireNonNull(binding.tietUsername.getText()).toString(),
                            Objects.requireNonNull(binding.tietPassword.getText()).toString()
                    );
                }
                return false;
            }
        });

    }

    private void lockScreen() {
        binding.tilUsername.setEnabled(false);
        binding.tilPassword.setEnabled(false);
        binding.loginButton.setEnabled(false);
    }

    private void unlockScreen() {
        binding.tilUsername.setEnabled(true);
        binding.tilPassword.setEnabled(true);
        binding.loginButton.setEnabled(true);
    }

    private void goToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return;
        lockScreen();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    unlockScreen();
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                goToMainActivity();
            }
        });
    }

    private void signup(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return;
        lockScreen();
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    unlockScreen();
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                goToMainActivity();
            }
        });
    }
}