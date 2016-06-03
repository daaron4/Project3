package com.companyname.ceramicgod;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ToiletFragment extends Fragment { //implements PhotoCallback,PhotoCallbackSigns {

    private ImageView mToiletView;
    private ImageView mSignView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toilet, container, false);
        mToiletView = (ImageView) view.findViewById(R.id.toilet_image_view);
        mSignView = (ImageView) view.findViewById(R.id.toilet_sign_image_view);
        Flickr.getInstance((PhotoCallback)this.getActivity()).doRequest();
        FlickrGroup.getInstance((PhotoCallbackSigns)this.getActivity()).doRequestSigns();
        return view;
    }

    //public void placeImageToilet(String response) {
    public void handleCallback(String response) {
        Picasso.with(getContext())
                .load(response)
                .fit()
                .centerCrop()
                .into(mToiletView);
    }

    //public void placeImageSigns(String response) {
    public void handleCallbackSigns(String response) {
        Picasso.with(getContext())
                .load(response)
                .fit()
                .centerCrop()
                .into(mSignView);
    }

}
