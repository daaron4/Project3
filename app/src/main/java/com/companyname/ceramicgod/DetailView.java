package com.companyname.ceramicgod;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class DetailView extends AppCompatActivity {

    ImageView mImageView;
    TextView mDetailTextView, mAddressTextView, mComment;
    Button mFavoriteButton;
    RatingBar ratingBar;
    String name,comments, address, filePath;
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
        filePath = detailViewCursor.getString((detailViewCursor.getColumnIndex(DatabaseHelper.COL_PICTURE)));

        mDetailTextView.setText(name);
        mComment.setText(comments);
        mAddressTextView.setText(address);
        ratingBar.setRating(rating);
        mImageView.setImageBitmap(getPic(filePath));

        float lat = detailViewCursor.getFloat(detailViewCursor.getColumnIndex(DatabaseHelper.COL_LATITUDE));
        float lon = detailViewCursor.getFloat(detailViewCursor.getColumnIndex(DatabaseHelper.COL_LONGITUDE));
        mAddressTextView.setText("Lat: " + lat + " Long: " + lon);
    }

    private Bitmap getPic(String filePath) {
        // Get the dimensions of the View
        mImageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int targetW = mImageView.getMeasuredWidth();
        int targetH = mImageView.getMeasuredHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(filePath, bmOptions);
    }
}
