package com.companyname.ceramicgod;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class NearbyFragment extends Fragment {

    private ImageView userPicture;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        Cursor cursor = DatabaseHelper.getInstance(getContext()).getAllReviews();
        final CursorAdapter cursorAdapter = new CursorAdapter(getContext(), cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.simple_review, viewGroup, false);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                userPicture = (ImageView) view.findViewById(R.id.image);
                String filePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PICTURE));
                userPicture.setImageBitmap(getPic(filePath));

                TextView name = (TextView) view.findViewById(R.id.name);
                String nameText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NAME));
                name.setText("Name: " + nameText);

                RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
                float ratingNumber = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COL_RATING));
                rating.setRating(ratingNumber);

            }
        };

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(getContext(), DetailView.class);
                Cursor cursor1 = cursorAdapter.getCursor();
                cursor1.moveToPosition(position);
                int id2 = cursor1.getInt(cursor1.getColumnIndex(DatabaseHelper.COL_ID));
                detailIntent.putExtra("id", id2);
                startActivity(detailIntent);
            }
        });

        return view;
    }

    private Bitmap getPic(String filePath) {
        // Get the dimensions of the View
        userPicture.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int targetW = userPicture.getMeasuredWidth();
        int targetH = userPicture.getMeasuredHeight();

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
