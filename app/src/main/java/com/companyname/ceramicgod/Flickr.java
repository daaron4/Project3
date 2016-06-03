package com.companyname.ceramicgod;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class Flickr {

    private static Flickr instance;
    private static PhotoCallback callback;
    private String id, secret, server, farm;

    private Flickr(){
        //Method needs to be empty.
    }

    public static Flickr getInstance(PhotoCallback call) {
        callback = call;
        if(instance == null) {
            instance = new Flickr();
        }
        return instance;
    }

    public void doRequest(){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(
                "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=a40360bdf387fa2da03c446546fd7ccc&tags=toilet&format=json&nojsoncallback=1"
                ,
                null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        String photoResult = null;

                        try {
                            JSONObject results = response.getJSONObject("photos");
                            JSONArray post = results.getJSONArray("photo");
                            Random randomToilet = new Random();
                            int idFetchToilet = randomToilet.nextInt(post.length());
                            JSONObject fetchToilet = post.getJSONObject(idFetchToilet);
                            id = fetchToilet.getString("id");
                            secret = fetchToilet.getString("secret");
                            server = fetchToilet.getString("server");
                            farm = fetchToilet.getString("farm");
                            photoResult = "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+"_z.jpg";
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                        callback.handleCallback(photoResult);
                    }
                }
        );
    }
}
