package com.companyname.ceramicgod;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class DetailView extends AppCompatActivity {

    ImageView mImageView;
    TextView mDetailTextView, mAddressTextView, mComment;
    Button mFavoriteButton;
    RatingBar ratingBar;
    String name,comments, address;
    int rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        mImageView = (ImageView) findViewById(R.id.detailImageView);
        mDetailTextView = (TextView) findViewById(R.id.detailTextView);
        mAddressTextView = (TextView) findViewById(R.id.address);
        mComment = (TextView) findViewById(R.id.commentSection);
        mFavoriteButton = (Button) findViewById(R.id.favoriteButton);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(DetailView.this);

        int id = getIntent().getIntExtra("id", -1);
            Cursor detailViewCursor = databaseHelper.getReviewAtIndex(id);
            name = detailViewCursor.getString((detailViewCursor.getColumnIndex(DatabaseHelper.COL_NAME)));
            comments = detailViewCursor.getString((detailViewCursor.getColumnIndex(DatabaseHelper.COL_COMMENTS)));
            address = detailViewCursor.getString((detailViewCursor.getColumnIndex(DatabaseHelper.COL_ADDRESS)));
            rating = detailViewCursor.getInt((detailViewCursor.getColumnIndex(DatabaseHelper.COL_RATING)));

            mDetailTextView.setText(name);
            mComment.setText(comments);
            mAddressTextView.setText(address);
            ratingBar.setRating(rating);

    }

    //TODO Connect photo to database.

    //TODO Favorite button needs to connect to the Review Page.
}
