package com.codeground.wanderlustbulgaria.Utilities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;

    public MarkerInfoWindowAdapter(Context ctx){
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        myContentsView = inflater.inflate(R.layout.layout_marker_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.marker_info_title));
        tvTitle.setText(marker.getTitle());
        TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.marker_info_snippet));
        tvSnippet.setText(marker.getSnippet());

        return myContentsView;
    }
}