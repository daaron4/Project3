package com.companyname.ceramicgod;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class NewReviewFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText locationName;
    private RatingBar ratingBar;
    private EditText userComments;
    private Button saveReview;
    private Button takePicture;
    private Button tweet;
    private ImageView userPicture;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_new_review, container, false);

        locationName = (EditText) view.findViewById(R.id.location_name);
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        userComments = (EditText) view.findViewById(R.id.user_comments);
        saveReview = (Button) view.findViewById(R.id.save_review);
        takePicture = (Button) view.findViewById(R.id.take_picture);
        userPicture = (ImageView) view.findViewById(R.id.user_picture);
        tweet = (Button) view.findViewById(R.id.tweet);

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

        tweet.setOnClickListener(new View.OnClickListener() {
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
                            String theTweet = "Location: " + locationName.getText().toString() + " Rating: " +
                                    ratingBar.getRating() + " Address:" + LocationData.address + " #ceramicgodapp";
                            Status status = twitter.updateStatus(theTweet);
                            System.out.println("Successfully updated the status to [" + status.getText() + "].");
                        } catch(TwitterException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        
        return view;
    }

    private void clickedSave() {
        if (locationName.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Please enter a name of the place", Toast.LENGTH_LONG).show();
        }
        // ToDo: add check for no picture:
        else {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Review newReview = new Review(locationName.getText().toString(), ratingBar.getRating(),
                    date, userComments.getText().toString(), LocationData.latitude,LocationData.longitude,
                    "Test address", "Test photo path");
            DatabaseHelper.getInstance(getContext()).insertReview(newReview);
            Toast.makeText(getContext(), "Review submitted", Toast.LENGTH_LONG).show();
            doPost();
        }
    }

    public void doPost() {
        try {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        URL url = new URL("http://nameless-bayou-62702.herokuapp.com/reviews.json");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");

                        String name = locationName.getText().toString();
                        float rating = ratingBar.getRating();
                        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        String comment = userComments.getText().toString();
                        float lat = LocationData.latitude;
                        float lon = LocationData.longitude;
                        String address = LocationData.address;
                        String goodAddress = address.replaceAll("\n", "");

                        String img = "http://www.example.com/";

                        String str = "{\n" +
                                "\"name\" : \"" + name + "\", \n" +
                                "\"rating\" : " + rating + ",\n" +
                                "\"date\" : \"" + date + "\",\n" +
                                "\"comment\" : \"" + comment + "\",\n" +
                                "\"latitude\" : " + lat + ",\n" +
                                "\"longitude\" : " + lon + ",\n" +
                                "\"address\" : \"" + goodAddress + "\",\n" +
                                "\"img_url\" : \"" + img + "\"\n" +
                                "}";
                        Log.d("STR", str);

                        OutputStream os = conn.getOutputStream();
                        os.write(str.getBytes());
                        os.flush();

                        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                        String output;
                        System.out.println("Output from Server .... \n");
                        while ((output = br.readLine()) != null) {
                            System.out.println(output);
                        }

                        conn.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            };
            thread.start();

        } finally {

        }
    }

    public void clickedTakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = PictureUtility.createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void putPicOnView(Bitmap bitmap) {
        userPicture.setImageBitmap(bitmap);
    }

}
