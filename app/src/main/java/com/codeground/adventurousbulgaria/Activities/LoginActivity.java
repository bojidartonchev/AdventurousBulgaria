package com.codeground.adventurousbulgaria.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.MainApplication;
import com.codeground.adventurousbulgaria.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Arrays;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button mSignUpBtn;
    private Button mLoginUpBtn;
    private Button mFacebookLoginUpBtn;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Client mKinveyClient;
    private CallbackManager mCallbackManager;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(this);
        mKinveyClient = ((MainApplication)getApplication()).getKinveyClient();
        User user = mKinveyClient.user();

        if(user.isUserLoggedIn()){
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_login);

            mSignUpBtn = (Button) findViewById(R.id.sign_up_btn);
            mSignUpBtn.setOnClickListener(this);

            mLoginUpBtn = (Button) findViewById(R.id.login_btn);
            mLoginUpBtn.setOnClickListener(this);

            mFacebookLoginUpBtn = (Button) findViewById(R.id.facebook_login_button);
            mFacebookLoginUpBtn.setOnClickListener(this);

            mEmailField = (EditText) findViewById(R.id.email_login_field);
            mPasswordField = (EditText) findViewById(R.id.pass_login_field);
        }

    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            if(v.getId() == R.id.sign_up_btn){
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
            }else if(v.getId() == R.id.login_btn){
                if(mEmailField!=null && mPasswordField!=null){


                    mProgressDialog.setMessage("Please Wait");
                    mProgressDialog.setTitle("Logging in");
                    mProgressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //Attempt to login with user credentials
                                login(mEmailField.getText().toString(),mPasswordField.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }
            }else if(v.getId() == R.id.facebook_login_button){
                submitFacebook(v);
            }
        }
    }

    private void login(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    mProgressDialog.dismiss();
                    CharSequence text = "Wrong username or password.";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void submitFacebook(View view){
        // The FB SDK has a bit of a delay in response
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Connecting to Facebook", "Logging in with Facebook - just a moment");
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String accessToken = AccessToken.getCurrentAccessToken().getToken();
                        loginFacebookKinveyUser(progressDialog, accessToken);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "FB login cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        error(progressDialog, exception.getMessage());
                    }
                });
    }
    private void loginFacebookKinveyUser(final ProgressDialog progressDialog, String accessToken) {
        mKinveyClient.user().loginFacebook(accessToken, new KinveyUserCallback() {
            @Override
            public void onFailure(Throwable e) {
                error(progressDialog, "Kinvey: " + e.getMessage());
                Toast.makeText(LoginActivity.this, "ERROR :Not Logged in Kinvey with Facebook.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(User u) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void error(ProgressDialog d, String msg){
        if (d != null) {
            d.setMessage(msg);
            d.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainMenuActivity
        moveTaskToBack(true);
    }
}
