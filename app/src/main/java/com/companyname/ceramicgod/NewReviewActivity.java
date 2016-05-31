package com.companyname.ceramicgod;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewReviewActivity extends AppCompatActivity {

    private EditText locationName;
    private RatingBar ratingBar;
    private EditText userComments;
    private Button saveReview;
    private Button favorite;
    private Button takePicture;
    private ImageView userPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);

        locationName = (EditText) findViewById(R.id.location_name);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        userComments = (EditText) findViewById(R.id.user_comments);
        saveReview = (Button) findViewById(R.id.save_review);
        favorite = (Button) findViewById(R.id.favorite);
        takePicture = (Button) findViewById(R.id.take_picture);
        userPicture = (ImageView) findViewById(R.id.user_picture);

        saveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedSave();
            }
        });


    }

    private void clickedSave() {
        if (locationName.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter a name of the place", Toast.LENGTH_LONG).show();
        }
        // ToDo: add check for no picture:
        else {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Review newReview = new Review(locationName.getText().toString(), ratingBar.getRating(),
                    userComments.getText().toString(), date, 0f,0f, "test");
            DatabaseHelper.getInstance(NewReviewActivity.this).insertReview(newReview);
            Toast.makeText(this, "Review submitted", Toast.LENGTH_LONG).show();
            finish();
        }
    }



}
