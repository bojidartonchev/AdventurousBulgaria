package com.codeground.wanderlustbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.codeground.wanderlustbulgaria.Utilities.SettingsManager;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sdsmdg.tastytoast.TastyToast;

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
                    DialogWindowManager.show(this);
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
            NotificationsManager.showToast(getString(R.string.alert_invalid_email), TastyToast.ERROR);
            DialogWindowManager.dismiss();
            return;
        }
        if(mFirstNameField.getText().toString().length()<3 || mFirstNameField.getText().toString().length()>12){
            NotificationsManager.showToast(getString(R.string.alert_first_name_length), TastyToast.ERROR);
            DialogWindowManager.dismiss();
            return;
        }
        if(mLastNameField.getText().toString().length()<3 || mLastNameField.getText().toString().length()>12){
            NotificationsManager.showToast(getString(R.string.alert_last_name_length), TastyToast.ERROR);
            DialogWindowManager.dismiss();
            return;
        }

        if(mPasswordField.getText().toString().length()<5 || mPasswordField.getText().toString().length()>12){
            NotificationsManager.showToast(getString(R.string.alert_password_length), TastyToast.ERROR);
            DialogWindowManager.dismiss();
            return;
        }

        if(password.equals(confirmPassword)==false){
            //Passwords do not match
            NotificationsManager.showToast(getString(R.string.alert_password_nomatch), TastyToast.ERROR);
            DialogWindowManager.dismiss();
            return;
        }

        String firstName = mFirstNameField.getText().toString();
        String lastName = mLastNameField.getText().toString();
        mCurrentUser = new ParseUser();
        mCurrentUser.setEmail(username);
        mCurrentUser.setUsername(username);
        mCurrentUser.setPassword(password);
        mCurrentUser.put("first_name",firstName);
        mCurrentUser.put("last_name",lastName);
        mCurrentUser.put("search_match",firstName.toLowerCase()+" "+lastName.toLowerCase());
        mCurrentUser.put("is_follow_allowed", true);
        mCurrentUser.put("profile_description", getString(R.string.register_profile_description));

        mCurrentUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                DialogWindowManager.dismiss();
                if (e == null) {
                    SettingsManager.updateDeviceInstallationInfo();
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    NotificationsManager.showToast(e.getMessage(), TastyToast.ERROR);
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
