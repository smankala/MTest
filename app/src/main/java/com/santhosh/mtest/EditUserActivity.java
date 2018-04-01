package com.santhosh.mtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditUserActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mHeightView;
    private EditText mAgeView;
    private View mProgressView;
    private View mLoginFormView;
    String username;
    String token;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = getIntent().getExtras().getString(LoginSuccessActivity.USER_NAME);
        token = getIntent().getExtras().getString(LoginSuccessActivity.TOKEN);
        userId = getIntent().getExtras().getString(LoginSuccessActivity.USERID);

        // Set up the login form.
        mEmailView =  findViewById(R.id.email);
        mEmailView.setEnabled(false);
        mHeightView =  (EditText) findViewById(R.id.height);
        mAgeView = (EditText) findViewById(R.id.age);
        mPasswordView =  (EditText) findViewById(R.id.password);

        mAgeView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.create_account);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void setUpData(){
        try {
            ((MTestApplication) getApplication()).getPersistenceService().getCurrentAuthedUser(username,
                    new IPersistenceInterfaceCallBack.Stub() {
                        @Override
                        public void handleLoginResponse(String isSuccess) throws RemoteException {
                        }

                        @Override
                        public void handleUserUpdate(User user) throws RemoteException {
                            if (user == null) {
                                Toast.makeText(EditUserActivity.this, "Failed to fetch user", Toast
                                        .LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditUserActivity.this, "Succesfully created user.Please Login", Toast.LENGTH_SHORT)
                                        .show();
                                Intent intent = new Intent(EditUserActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to authenticate user please try later", Toast.LENGTH_SHORT);
        }
    }


    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String age = mAgeView.getText().toString();
        String height = mHeightView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(height)) {
            mHeightView.setError(getString(R.string.error_field_required));
            focusView = mHeightView;
            cancel = true;
        } else if (!isHeightValid(height)) {
            mHeightView.setError(getString(R.string.error_invalid_height));
            focusView = mHeightView;
            cancel = true;
        }

        if (TextUtils.isEmpty(age)) {
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            cancel = true;
        } else if (!isAgeValid(email)) {
            mAgeView.setError(getString(R.string.error_invalid_age));
            focusView = mAgeView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            User user = new User();
            user.username = email;
            user.password = password;
            user.height = Integer.parseInt(height);
            user.age = Integer.parseInt(age);
            try {
                ((MTestApplication) getApplication()).getPersistenceService().createUser(user,
                        new IPersistenceInterfaceCallBack.Stub() {
                            @Override
                            public void handleLoginResponse(String token) throws RemoteException {
                            }

                            @Override
                            public void handleUserUpdate(User user) throws RemoteException {
                                if (user == null) {
                                    Toast.makeText(EditUserActivity.this, "Failed to create user", Toast
                                            .LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditUserActivity.this, "Succesfully fetched user.Please Login",
                                            Toast
                                            .LENGTH_SHORT).show();
                                    mEmailView.setText(user.username);
                                    mAgeView.setText(user.age);
                                    mHeightView.setText(user.height);
                                }
                            }
                        });
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to authenticate user please try later", Toast.LENGTH_SHORT);
            }

        }
    }

    private boolean isEmailValid(String email) {
        return true;
    }

    private boolean isHeightValid(String height) {
        int heightInt;
        try {
            heightInt = Integer.parseInt(height);
        } catch (NumberFormatException e) {
            return false;
        }

        return heightInt > 0 && heightInt < 247;
    }

    private boolean isAgeValid(String age) {
        int ageInt;
        try {
            ageInt = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            return false;
        }

        return ageInt > 0 && ageInt < 110;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

