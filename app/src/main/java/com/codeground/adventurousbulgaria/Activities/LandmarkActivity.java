package com.codeground.adventurousbulgaria.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.ParseLocation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class LandmarkActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTitle;
    private TextView mDescription;
    private TextView mLocation;
    private ImageView mPhoto;
    private ParseLocation mCurrLocation;
    private EditText mCommentField;
    private Button mCommentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        mTitle = (TextView) findViewById(R.id.landmark_title);
        mDescription = (TextView) findViewById(R.id.landmark_description);
        mLocation = (TextView) findViewById(R.id.landmark_location);
        mPhoto = (ImageView) findViewById(R.id.monument_picture);
        mCommentField = (EditText) findViewById(R.id.comment_field);
        mCommentBtn = (Button) findViewById(R.id.comment_btn);

        mCommentBtn.setOnClickListener(this);

        String currentLandmarkName = getIntent().getStringExtra("locationName");
        String locId = getIntent().getStringExtra("locationId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");
        query.whereEqualTo("objectId",locId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mCurrLocation=(ParseLocation)objects.get(0);
                    if (mTitle != null) {
                        mTitle.setText(mCurrLocation.getName());
                    }
                    if (mDescription != null) {
                        mDescription.setText(mCurrLocation.getDescription());
                    }
                    if (mLocation != null) {
                        mLocation.setText(mCurrLocation.getCity());
                    }

                } else {
                    // error
                }
            }
        });




    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.comment_btn){
            submitComment();
        }

    }

    private void submitComment() {
        String content = mCommentField.getText().toString();
        final ParseObject comment = new ParseObject("Comments");

        comment.put("creator", ParseUser.getCurrentUser().getEmail());
        comment.put("content", content);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseRelation<ParseObject> relation = mCurrLocation.getRelation("comments");
                relation.add(comment);
                mCurrLocation.saveInBackground();
            }

        });


    }
}
