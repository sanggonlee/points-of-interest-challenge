package com.example.sanggon.synaptopchallenge;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PoiAdapter extends BaseAdapter {
    private Context context;
    private List<PointOfInterest> mItems;
    public enum Mode {
        GRID, LIST
    }
    Mode mode = Mode.GRID;
    Boolean changed;

    public PoiAdapter(Context context, List<PointOfInterest> items) {
        this.context = context;
        this.mItems = items;
        changed = false;
    }

    public void setMode(Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            for (PointOfInterest item : mItems) {
                item.needsViewChange = true;
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView = null;

        if (convertView != null) {
            gridView = (View) convertView;
        }

        if (convertView == null || getItem(position).needsViewChange) {
            if (mode == Mode.GRID) {
                gridView = inflater.inflate(R.layout.poi_item, null);
            } else {
                gridView = inflater.inflate(R.layout.poi_item_horizontal, null);
            }
        }

        TextView textView = (TextView) gridView.findViewById(R.id.title);
        textView.setText(mItems.get(position).title);
        Log.i("poia", mItems.get(position).title);

        TextView lngView = (TextView) gridView.findViewById(R.id.lng);
        lngView.setText(mItems.get(position).lng);

        TextView latView = (TextView) gridView.findViewById(R.id.lat);
        latView.setText(mItems.get(position).lat);

        TextView urlView = (TextView) gridView.findViewById(R.id.url);
        urlView.setText(mItems.get(position).url);

        ImageView imageView = (ImageView) gridView.findViewById(R.id.image);
        // hard coding this, extremely ugly but I have no idea how to relate the image files
        // to the data because there's no item id or anything
        Integer resourceId = null;
        if (mItems.get(position).title.equals("Ripley's Aquarium")) {
            resourceId = R.drawable.aquarium;
        } else if (mItems.get(position).title.equals("CN Tower")) {
            resourceId = R.drawable.cntower;
        } else if (mItems.get(position).title.equals("Toronto Zoo")) {
            resourceId = R.drawable.torontozoo;
        } else if (mItems.get(position).title.equals("Royal Ontario Museum")) {
            resourceId = R.drawable.rom;
        } else if (mItems.get(position).title.equals("Art Gallery of Ontario")) {
            resourceId = R.drawable.artgalleryontario;
        } else if (mItems.get(position).title.equals("Eaton Center")) {
            resourceId = R.drawable.torontoeatoncentre;
        } else if (mItems.get(position).title.equals("City Hall")) {
            resourceId = R.drawable.torontocityhall;
        } else {
            resourceId = 0;
        }

        imageView.setImageResource(resourceId);

        return gridView;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public PointOfInterest getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}