package com.companyname.ceramicgod;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class CardViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        Cursor cursor = DatabaseHelper.getInstance(CardViewActivity.this).getAllReviews();
        final CursorAdapter cursorAdapter = new CursorAdapter(this, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.simple_review, viewGroup, false);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                byte[] array = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COL_PICTURE));
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length, options);
                imageView.setImageBitmap(bitmap);

                TextView name = (TextView) view.findViewById(R.id.name);
                String nameText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NAME));
                name.setText("Name: " + nameText);

                RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
                float ratingNumber = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COL_RATING));
                rating.setRating(ratingNumber);

            }
        };

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(CardViewActivity.this, DetailView.class);
                Cursor cursor1 = cursorAdapter.getCursor();
                cursor1.moveToPosition(position);
                int id2 = cursor1.getInt(cursor1.getColumnIndex(DatabaseHelper.COL_ID));
                detailIntent.putExtra("id", id2);
                startActivity(detailIntent);
            }
        });
    }
}
