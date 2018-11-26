package com.example.andrewtran.superapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomWindowInforAdapter implements GoogleMap.InfoWindowAdapter{
    private final View mView;

    Bitmap mBitmap;

    public CustomWindowInforAdapter(Context context, Bitmap bitmap) {
        this.mBitmap = bitmap;
        mView = LayoutInflater.from(context).inflate(R.layout.infor_window,null);
    }
    private void renderWindowText (Marker marker, View view, Bitmap bitmap){
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);
        if(!title.isEmpty()){
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);
        if(!snippet.isEmpty()){
            tvSnippet.setText(snippet);
        }

        ImageView placeImage = view.findViewById(R.id.placeImage);
        placeImage.setImageBitmap(bitmap);

    }


    @Override

    public View getInfoWindow(Marker marker) {
        renderWindowText(marker,mView,mBitmap);
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,mView,mBitmap);
        return mView;
    }
}
