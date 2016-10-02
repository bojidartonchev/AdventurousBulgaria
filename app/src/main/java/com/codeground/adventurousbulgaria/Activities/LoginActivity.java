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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mSignUpBtn;
    private Button mLoginUpBtn;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = ((MainApplication)getApplication()).getKinveyClient().user();

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
            }
        }
    }

    private void login(String username, String password){
        Client mKinveyClient = ((MainApplication)getApplication()).getKinveyClient();

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
}
