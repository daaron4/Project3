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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewReviewFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText locationName;
    private RatingBar ratingBar;
    private EditText userComments;
    private Button saveReview;
    private Button takePicture;
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
        
        return view;
    }

    private void clickedSave() {
        if (locationName.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Please enter a name of the place", Toast.LENGTH_LONG).show();
        }
//        else if (userPicture.getDrawable() == null) {
//            Toast.makeText(getContext(), "Please take a picture", Toast.LENGTH_LONG).show();
//        }
        // ToDo: add check for no picture:
        else {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Review newReview = new Review(locationName.getText().toString(), ratingBar.getRating(),
                    date, userComments.getText().toString(), LocationData.latitude,LocationData.longitude, PictureUtility.getmCurrentPhotoPath());
            DatabaseHelper.getInstance(getContext()).insertReview(newReview);
            Toast.makeText(getContext(), "Review submitted", Toast.LENGTH_LONG).show();
            userComments.setText("");
            locationName.setText("");
            ratingBar.setNumStars(0);
            userPicture.setImageResource(0);
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
