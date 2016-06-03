package com.companyname.ceramicgod;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by David on 6/3/2016.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("<><>", "onPerformSync");
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

        Gson gson = new Gson();
        SearchResult result = gson.fromJson(data,SearchResult.class);

        for (int i = 0; i < result.getResults().size(); i++) {
            ContentValues values = new ContentValues();
            values.put("name",result.getResults().get(i).getName());
            values.put("rating",result.getResults().get(i).getRating());
            values.put("date",result.getResults().get(i).getDate());
            values.put("comment",result.getResults().get(i).getComment());
            values.put("latitude",result.getResults().get(i).getLatitude());
            values.put("longitude",result.getResults().get(i).getLongitude());
            values.put("address",result.getResults().get(i).getAddress());
            values.put("picture",result.getResults().get(i).getImg_url().toString());
            mContentResolver.insert(ReviewContentProvider.CONTENT_URI,values);
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
