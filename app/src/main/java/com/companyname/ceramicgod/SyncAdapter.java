package com.companyname.ceramicgod;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final Uri CONTENT_URI = ReviewContentProvider.CONTENT_URI;
    private static String TAG = SyncAdapter.class.getCanonicalName();

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        //Use our content provider to empty our local database.
        //The data will be replaced with the new results from the api call below.
        mContentResolver.delete(ReviewContentProvider.CONTENT_URI,null,null);

        String data ="";
        try {
            URL url = new URL("https://nameless-bayou-62702.herokuapp.com/reviews.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inStream = connection.getInputStream();
            data = getInputData(inStream);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        //Organize our out response by using gson to instantiate a SearchResult data model.
        Gson gson = new Gson();
        DataModel[] result = gson.fromJson(data,DataModel[].class);

        //Loop through the results and insert the contents of each NewsItem into the database via our content provider.
        for (int i = 0; i < result.length; i++) {
            ContentValues values = new ContentValues();
            values.put("name",result[i].getName());
            values.put("rating",result[i].getRating());
            values.put("date",result[i].getDate());
            values.put("comment",result[i].getComment());
            values.put("latitude",result[i].getLatitude());
            values.put("longitude",result[i].getLongitude());
            values.put("address",result[i].getAddress());
            values.put("picture",result[i].getImg_url().toString());
            mContentResolver.insert(CONTENT_URI,values);
        }
    }

    private String getInputData(InputStream inStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        String data = null;

        while ((data = reader.readLine()) != null){
            builder.append(data);
        }

        reader.close();

        return builder.toString();
    }
}
