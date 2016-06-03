package com.companyname.ceramicgod;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ToiletFragment extends Fragment implements PhotoCallback, PhotoCallbackSigns {

    private ImageView mToiletView;
    private ImageView mSignView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        mToiletView = (ImageView) view.findViewById(R.id.toilet_image_view);
        Flickr.getInstance(ToiletFragment.this).doRequest();

        mSignView = (ImageView) view.findViewById(R.id.toilet_sign_image_view);
        FlickrGroup.getInstance(ToiletFragment.this).doRequestSigns();

        return view;
    }

    @Override
    public void handleCallback(String response) {
        Picasso.with(getContext())
                .load(response)
                .fit()
                .centerCrop()
                .into(mToiletView);
    }
    public void handleCallbackSigns(String response) {
        Picasso.with(getContext())
                .load(response)
                .fit()
                .centerCrop()
                .into(mSignView);
    }

}
