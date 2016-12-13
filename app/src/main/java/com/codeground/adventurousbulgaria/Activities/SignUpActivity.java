package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.MainApplication;
import com.codeground.adventurousbulgaria.R;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mRegBtn;
    private EditText mEmailField;
    private EditText mUsernameField;
    private EditText mProfileNameField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mProfileNameField = (EditText) findViewById(R.id.profile_name_field) ;
        mEmailField = (EditText) findViewById(R.id.email_field);
        mUsernameField = (EditText) findViewById(R.id.username_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mConfirmPasswordField = (EditText) findViewById(R.id.confirm_password_field);

        mRegBtn = (Button) findViewById(R.id.register_btn);
        mRegBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            if(v.getId() == R.id.register_btn){
                if(mProfileNameField!=null && mEmailField!=null && mUsernameField!=null && mPasswordField!=null && mConfirmPasswordField!=null){
                    registerUser(mUsernameField.getText().toString(),mPasswordField.getText().toString(),mConfirmPasswordField.getText().toString());
                }
            }
        }
    }

    private void registerUser(String username, String password, String confirmPassword)
    {
        //Validations
        if(mUsernameField.getText().toString().length()<=4 || mUsernameField.getText().toString().length()>12){
            //Check username length
            Toast.makeText(this, "Username must be between 4 and 12 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!validateEmail(mEmailField.getText().toString())) {
            //Invalid email
            Toast.makeText(this, "Invalid E-mail address. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals(confirmPassword)==false){
            //Passwords do not match
            Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mPasswordField.getText().toString().length()<5 || mPasswordField.getText().toString().length()>12){
            Toast.makeText(this, "Password must be between 5 and 12 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }

        mCurrentUser = new ParseUser();
        mCurrentUser.setUsername(username);
        mCurrentUser.setPassword(password);
        mCurrentUser.setEmail(mEmailField.getText().toString());
        mCurrentUser.put("first_name",mProfileNameField.getText().toString());
        mCurrentUser.put("last_name",mProfileNameField.getText().toString());
        mCurrentUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    CharSequence text = "Could not sign up.";
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateEmail(String email){
        boolean isValid = false;
        String expression = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;

        }
        return isValid;
    }
}
