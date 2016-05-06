package com.exjobbandroidapplication.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.exjobbandroidapplication.Network.ConnectionHandler;
import com.exjobbandroidapplication.R;
import com.exjobbandroidapplication.Resources.inputCheck;

import java.util.ArrayList;
import java.util.List;

import Enums.ServerMessageType;
import NetworkMessages.LoginMessage;
import NetworkMessages.RegisterMessage;
import NetworkMessages.ServerMessage;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordRepeatView;
    private View mProgressView;
    private View mLoginFormView;
    private boolean registrationMode = false;
    private Button mEmailSignInButton;
    private Button backButton;
    private LoginActivity loginActivity = this;

    //Dangerous permissions user needs to grant.
    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConnectionHandler.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        //Check if the user has given permissions.
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 0);
        }
        //TODO : Kör igång platstjänster innan man får logga in.
        //TODO : Sätt zoomlevel på kartan beroende på användarens upplösning??
        //TODO : Man ska inte kunna logga in om man inte gett tillstånd till GPS och lagring.

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mPasswordRepeatView = (EditText) findViewById(R.id.passwordrepeat);
        mPasswordRepeatView.setVisibility(View.GONE);
        //Editable text = mPasswordRepeatView.getText();

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationPressed();
            }
    });

        backButton = (Button) findViewById(R.id.back_button);
        backButton.setVisibility(View.GONE);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    //TODO : fixa så att permissions frågas efter som de ska.
    /**
     *
     * @param context
     * @param permissions
     * @return
     */
    private boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }

    private void backPressed() {
        backButton.setVisibility(View.GONE);
        registrationMode = false;
        mPasswordRepeatView.setVisibility(View.GONE);
        mEmailSignInButton.setVisibility(View.VISIBLE);
        mPasswordRepeatView.setText("");
    }

    private void registrationPressed() {
        if (registrationMode) {
            attemptLogin();
        }
        else {
            registrationMode = true;
            mEmailSignInButton.setVisibility(View.GONE);
            mPasswordRepeatView.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
        }
    }

    private void showLogin() {

    }

    private void showRegistration() {

    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordRepeatView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password1 = mPasswordView.getText().toString();
        String password2 = mPasswordRepeatView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password1) && !inputCheck.isPasswordValid(password1)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!inputCheck.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (registrationMode) {
            if (!inputCheck.arePasswordsMatching(password1, password2)){
                mPasswordView.setError("Passwords are not matching");
                focusView = mPasswordView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password1);
            mAuthTask.execute((Void) null);
        }
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ConnectionHandler.getInstance().connectToServer();
            ConnectionHandler.getInstance().seteMail(mEmail);

            if (registrationMode){
                ConnectionHandler.getInstance().seteMail(mEmail);
                final ServerMessage serverMessage = ConnectionHandler.getInstance().sendMessage(new RegisterMessage(mEmail, mPassword));
                if (serverMessage == null) {
                    Log.d("serverMessage", "De e inte najs!");
                }
                if (serverMessage.getMessageType() == ServerMessageType.Authenticated){
                    registrationMode = false;
                    return true;
                }
                else if (serverMessage.getMessageType() == ServerMessageType.Disconnect){
                    loginActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final String message = serverMessage.getMessage().toString();
                            mEmailView.setError(message);
                        }
                    });
                }
            }
            else {
                ConnectionHandler.getInstance().seteMail(mEmail);
                final ServerMessage serverMessage = ConnectionHandler.getInstance().sendMessage(new LoginMessage(mEmail, mPassword));
                if (serverMessage.getMessageType() == ServerMessageType.Authenticated){
                    return true;
                }
                else if (serverMessage.getMessageType() == ServerMessageType.Disconnect){
                    loginActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final String message = serverMessage.getMessage().toString();
                            mEmailView.setError(message);
                        }
                    });
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent plupp = new Intent(loginActivity,osmtest.class);
                startActivity(plupp);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

