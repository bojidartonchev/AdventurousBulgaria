package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.MainApplication;
import com.codeground.adventurousbulgaria.R;
import com.kinvey.android.Client;
import com.kinvey.java.User;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mUsername;
    private Button mLogoutButton;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        mUsername = (TextView)findViewById(R.id.username);
        mLogoutButton =(Button)findViewById(R.id.logout_btn);
        mLogoutButton.setOnClickListener(this);
        
        mCurrentUser = ((MainApplication)getApplication()).getKinveyClient().user();

        if(mUsername!=null && !mCurrentUser.isEmpty()){
            mUsername.setText(mCurrentUser.getUsername());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.logout_btn){
            logout();
        }
    }

    private void logout() {
        Client mKinveyClient = ((MainApplication)getApplication()).getKinveyClient();
        mKinveyClient.user().logout().execute();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
