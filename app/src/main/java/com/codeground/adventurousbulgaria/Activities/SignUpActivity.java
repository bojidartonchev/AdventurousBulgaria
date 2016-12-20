package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.DialogWindowManager;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mRegBtn;
    private EditText mEmailField;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mEmailField = (EditText) findViewById(R.id.email_field);
        mFirstNameField = (EditText) findViewById(R.id.first_name_field) ;
        mLastNameField = (EditText) findViewById(R.id.last_name_field) ;
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mConfirmPasswordField = (EditText) findViewById(R.id.confirm_password_field);

        mRegBtn = (Button) findViewById(R.id.register_btn);
        mRegBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            if(v.getId() == R.id.register_btn){
                if(mEmailField!=null && mFirstNameField!=null && mLastNameField!=null && mPasswordField!=null ){
                    registerUser(mEmailField.getText().toString(),mPasswordField.getText().toString(),mConfirmPasswordField.getText().toString());
                }
            }
        }
    }

    private void registerUser(String username, String password, String confirmPassword)
    {
        //Validations

        if(!validateEmail(mEmailField.getText().toString())) {
            //Invalid email
            Toast.makeText(this, getString(R.string.alert_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals(confirmPassword)==false){
            //Passwords do not match
            Toast.makeText(this, getString(R.string.alert_password_nomatch), Toast.LENGTH_SHORT).show();
            return;
        }

        if(mPasswordField.getText().toString().length()<5 || mPasswordField.getText().toString().length()>12){
            Toast.makeText(this, getString(R.string.alert_password_length), Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = mFirstNameField.getText().toString();
        String lastName = mLastNameField.getText().toString();
        mCurrentUser = new ParseUser();
        mCurrentUser.setUsername(username);
        mCurrentUser.setPassword(password);
        mCurrentUser.put("first_name",firstName);
        mCurrentUser.put("last_name",lastName);
        mCurrentUser.put("search_match",firstName.toLowerCase()+" "+lastName.toLowerCase());
        DialogWindowManager.show(this);
        mCurrentUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    DialogWindowManager.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    DialogWindowManager.dismiss();
                    CharSequence text = getString(R.string.alert_signup_fail);
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
        if (matcher.matches()&&email.length()<30) {
            isValid = true;

        }

        return isValid;
    }
}
