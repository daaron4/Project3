package com.companyname.ceramicgod;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class FlickrGroup extends ToiletView {

    private static FlickrGroup instance;
    private static PhotoCallbackSigns callbackSigns;
    private String id, secret, server, farm;

    private FlickrGroup(){
        //Method needs to be empty.
    }


    public static FlickrGroup getInstance(PhotoCallbackSigns call) {
        callbackSigns = call;
        if(instance == null) {
            instance = new FlickrGroup();
        }
        return instance;
    }

    public void doRequestSigns(){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(
                "https://api.flickr.com/services/rest/?method=flickr.groups.pools.getPhotos&api_key=a40360bdf387fa2da03c446546fd7ccc&group_id=40881558@N00&format=json&nojsoncallback=1"
                ,null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        String photoResult = null;

                        try {
                            JSONObject results = response.getJSONObject("photos");
                            JSONArray post = results.getJSONArray("photo");
                            Random randomToiletGroup = new Random();
                            int idFetchSigns = randomToiletGroup.nextInt(post.length());
                            JSONObject fetchSigns = post.getJSONObject(idFetchSigns);
                            id = fetchSigns.getString("id");
                            secret = fetchSigns.getString("secret");
                            server = fetchSigns.getString("server");
                            farm = fetchSigns.getString("farm");
                            photoResult = "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+"_z.jpg";
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                        callbackSigns.handleCallbackSigns(photoResult);
                    }
                }
        );
    }

}
