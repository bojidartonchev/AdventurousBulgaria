package com.codeground.adventurousbulgaria.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.DialogWindowManager;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener {
    final List<String> permissions = Arrays.asList("public_profile", "email");

    private TextView mSignUpBtn;
    private Button mLoginUpBtn;
    private TextView mFacebookLoginUpBtn;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(ParseUser.getCurrentUser() != null){
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_login);

            mSignUpBtn = (TextView) findViewById(R.id.sign_up_btn);
            mSignUpBtn.setOnClickListener(this);

            mLoginUpBtn = (Button) findViewById(R.id.login_btn);
            mLoginUpBtn.setOnClickListener(this);

            mFacebookLoginUpBtn = (TextView) findViewById(R.id.facebook_login_button);
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
                    DialogWindowManager.show(this);
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
                DialogWindowManager.show(this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Attempt to login with user credentials
                            loginFacebookUser();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    private void login(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    DialogWindowManager.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    DialogWindowManager.dismiss();
                    CharSequence text = "Wrong username or password.";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginFacebookUser() {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    DialogWindowManager.dismiss();
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                    getUserDetailFromFB();
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    switchToMainMenu();
                }
            }
        });
    }

    void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(final JSONObject object, GraphResponse response) {
                String fname = "",lname = "", email = "", id = "";

                try {
                    fname = object.getString(getString(R.string.db_user_firstname));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    lname = object.getString(getString(R.string.db_user_lastname));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    email = object.getString(getString(R.string.db_user_email));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try{
                    id= object.getString("id");
                } catch(JSONException e){
                    e.printStackTrace();
                }

                saveNewUser(fname, lname, email);
            }
        });


        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }
    void saveNewUser(final String firstName, String lastName, String email){

        ParseUser user = ParseUser.getCurrentUser();

        user.put(getString(R.string.db_user_firstname),firstName);
        user.put(getString(R.string.db_user_lastname),lastName);
        user.put(getString(R.string.db_user_searchmatch),firstName.toLowerCase()+" "+lastName.toLowerCase());
        user.setEmail(email);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                switchToMainMenu();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainMenuActivity
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void switchToMainMenu(){
        DialogWindowManager.dismiss();
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
