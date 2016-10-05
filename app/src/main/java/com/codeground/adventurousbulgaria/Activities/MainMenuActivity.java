package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.codeground.adventurousbulgaria.R;


public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mProfileBtn=(Button) findViewById(R.id.button8);
        mProfileBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button8){
            Intent intent = new Intent(this, UserHomeActivity.class);
            startActivity(intent);
        }
    }
}
