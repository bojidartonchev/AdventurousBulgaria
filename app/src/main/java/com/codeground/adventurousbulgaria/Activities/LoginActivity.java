package com.codeground.adventurousbulgaria.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mSignUpBtn;
    private Button mLoginUpBtn;
    private Button mFacebookLoginUpBtn;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Client mKinveyClient;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKinveyClient = ((MainApplication)getApplication()).getKinveyClient();
        User user = mKinveyClient.user();

        if(user.isUserLoggedIn()){
            Intent intent = new Intent(this, UserHomeActivity.class);
            startActivity(intent);
            finish();
        }else {
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
                    String username = mEmailField.getText().toString();
                    String pass = mPasswordField.getText().toString();

                    //Attempt to login with user credentials
                    login(username,pass);
                }
            }else if(v.getId() == R.id.facebook_login_button){
                submitFacebook(v);
            }
        }
    }

    private void login(String username, String password){
        mKinveyClient.user().login(username, password, new KinveyUserCallback() {
            @Override
            public void onFailure(Throwable t) {
                CharSequence text = "Wrong username or password.";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(User u) {
                Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                startActivity(intent);
                finish();
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
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
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
                Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
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
}
