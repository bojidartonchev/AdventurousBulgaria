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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mRegBtn;
    private EditText mEmailField;
    private EditText mNameField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmailField = (EditText) findViewById(R.id.email_field);
        mNameField = (EditText) findViewById(R.id.name_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mConfirmPasswordField = (EditText) findViewById(R.id.confirm_password_field);

        mRegBtn = (Button) findViewById(R.id.register_btn);
        mRegBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            if(v.getId() == R.id.register_btn){
                if(mEmailField!=null && mNameField!=null && mPasswordField!=null && mConfirmPasswordField!=null){
                    RegisterUser(mEmailField.getText().toString(),mPasswordField.getText().toString(),mConfirmPasswordField.getText().toString());
                }
            }
        }
    }

    private void RegisterUser(String email, String password, String confirmPassword)
    {
        if(password.equals(confirmPassword)==false){
            //Passwords do not match
            Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        Client mKinveyClient = ((MainApplication)getApplication()).getKinveyClient();

        if(mKinveyClient!=null){
            mKinveyClient.user().create(email, password, new KinveyUserCallback() {
                @Override
                public void onSuccess(User user) {
                    String todaysDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    ((MainApplication) getApplication()).updateKinveyUser("Name", mNameField.getText().toString());
                    ((MainApplication) getApplication()).updateKinveyUser("DateCreated", todaysDate);

                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    CharSequence text = "Could not sign up.";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
