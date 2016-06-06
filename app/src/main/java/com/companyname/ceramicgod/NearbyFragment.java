package com.companyname.ceramicgod;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class NearbyFragment extends Fragment {

    public static final String AUTHORITY = "com.companyname.ceramicgod.ReviewContentProvider";
    public static final String ACCOUNT_TYPE = "example.com";
    public static final String ACCOUNT = "default_account";

    private Account mAccount;
    private CursorAdapter cursorAdapter;
    private ImageView userPicture;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        mAccount = createSyncAccount(getContext());
        Button maps = (Button) view.findViewById(R.id.go_to_maps);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),MapsActivity.class);
                startActivity(i);
            }
        });

        Cursor cursor = getContext().getContentResolver().query(ReviewContentProvider.CONTENT_URI, null, null, null, null);
        cursorAdapter = new CursorAdapter(getContext(), cursor, 0) {
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
                // ToDo: will create problems:
                Picasso.with(getContext())
                        .load(filePath)
                        .fit()
                        .centerCrop()
                        .into(userPicture);
                //userPicture.setImageBitmap(getPic(filePath));

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

        if (RandomData.doThisOnce) {
            getContext().getContentResolver().registerContentObserver(ReviewContentProvider.CONTENT_URI, true, new ReviewsContentObserver(new Handler()));

            Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

            ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

            ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
            ContentResolver.addPeriodicSync(
                    mAccount,
                    AUTHORITY,
                    Bundle.EMPTY,
                    60);
            RandomData.doThisOnce = false;
        }
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
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(filePath, bmOptions);
    }

    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
        } else {
        }
        return newAccount;
    }

    public class ReviewsContentObserver extends ContentObserver {

        public ReviewsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            try {
                cursorAdapter.swapCursor(getContext().getContentResolver().query(ReviewContentProvider.CONTENT_URI, null, null, null, null));

            } catch (NullPointerException e) {
                Log.d("BAD", "NOOOOOOO");
            }
        }
    }
}
