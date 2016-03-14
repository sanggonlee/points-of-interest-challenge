package com.example.sanggon.synaptopchallenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    Firebase firebaseRef;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private LinearLayout mEmailLoginFormView;
    private ScrollView mLoginFormView;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_login);

        firebaseRef = new Firebase("https://glowing-fire-2769.firebaseio.com/");

        mEmailView = (EditText)findViewById(R.id.email);
        mPasswordView = (EditText)findViewById(R.id.password);
        mEmailLoginFormView = (LinearLayout)findViewById(R.id.email_login_form);
        mLoginFormView = (ScrollView)findViewById(R.id.login_form);
        mLoginButton = (Button)findViewById(R.id.email_sign_in_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinUser();
            }
        });

        firebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                    Intent intent = new Intent(LoginActivity.this, BrowseActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void signinUser() {
        firebaseRef.authWithPassword(
                mEmailView.getText().toString(),
                mPasswordView.getText().toString(),
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        // Authentication just completed successfully :)
                        Log.i(TAG, "Auth success");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("provider", authData.getProvider());
                        if(authData.getProviderData().containsKey("displayName")) {
                            map.put("displayName", authData.getProviderData().get("displayName").toString());
                        }
                        firebaseRef.child("users").child(authData.getUid()).setValue(map);

                        Intent intent = new Intent(LoginActivity.this, BrowseActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                        Log.i(TAG, "Auth fail: " + error.getMessage());
                        Log.i(TAG, "Trying to create user");
                        createUser();
                    }
                });
    }
    public void createUser() {
        firebaseRef.createUser(
                mEmailView.getText().toString(),
                mPasswordView.getText().toString(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        System.out.println("Successfully created user account with uid: " + result.get("uid"));
                        signinUser();
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Log.w(TAG, "Creating user failed: "+ firebaseError.getMessage());
                    }
                });
    }
}