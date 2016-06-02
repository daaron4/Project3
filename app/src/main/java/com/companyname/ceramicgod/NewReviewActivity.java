package com.companyname.ceramicgod;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class NewReviewActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText locationName;
    private RatingBar ratingBar;
    private EditText userComments;
    private Button saveReview;
    private Button favorite;
    private Button takePicture;
    private Button post;
    private ImageView userPicture;

    private String mCurrentPhotoPath;

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
        post = (Button) findViewById(R.id.post);
        userPicture = (ImageView) findViewById(R.id.user_picture);

        saveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedSave();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedTakePicture();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigurationBuilder configBuilder = new ConfigurationBuilder();
                configBuilder.setDebugEnabled(true);

                configBuilder.setOAuthConsumerKey(TwitterData.OAUTH_CONSUMER_KEY);
                configBuilder.setOAuthConsumerSecret(TwitterData.OAUTH_CONSUMER_SECRET);
                configBuilder.setOAuthAccessToken(TwitterData.OAUTH_ACCESS_TOKEN);
                configBuilder.setOAuthAccessTokenSecret(TwitterData.OAUTH_ACCESS_TOKEN_SECRET);

                TwitterFactory tf = new TwitterFactory(configBuilder.build());
                final Twitter twitter = tf.getInstance();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Status status = twitter.updateStatus(locationName.getText().toString());
                            System.out.println("Successfully updated the status to [" + status.getText() + "].");
                        } catch(TwitterException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
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
                    userComments.getText().toString(), date, 0f,0f, mCurrentPhotoPath);
            DatabaseHelper.getInstance(NewReviewActivity.this).insertReview(newReview);
            Toast.makeText(this, "Review submitted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void clickedTakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            userPicture.setImageBitmap(getPic());
        }
    }

    private Bitmap getPic() {
        // Get the dimensions of the View
        int targetW = userPicture.getWidth();
        int targetH = userPicture.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

}
