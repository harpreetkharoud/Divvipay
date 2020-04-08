package com.divvipay.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.List;

/**
 * Created by hp15-p017tu on 25-07-2016.
 */

/*
 * adapter class for AddPlace
 * */

public class add_adapter extends ArrayAdapter<additem> {
    private SparseBooleanArray mSelectedItemsIds;
    private LayoutInflater inflater;
    private Context mContext;
    private List<additem> list;
    AdView mAdView;

    public add_adapter (Context context, int resourceId, List<additem> list) {
        super(context, 0, list);
        mSelectedItemsIds = new SparseBooleanArray();
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.list = list;


    }

    private static class ViewHolder {
        TextView date;
        TextView name;
        TextView GID;
        TextView amount;
    }

    @SuppressLint("ResourceAsColor")
    public View getView(int position, View view, ViewGroup parent) {

        if((position) % 5 == 0 && position != 0)
        {
            view = inflater.inflate(R.layout.admob, null);

            mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.

                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    Toast.makeText(mContext,"Failed to load",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.

                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.

                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.

                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.

                }
            });

        }
        else {
            final ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.add_text, null);
                holder = new ViewHolder();

                holder.name = (TextView) view.findViewById(R.id.name);
                holder.date = (TextView) view.findViewById(R.id.date);
                holder.GID = (TextView) view.findViewById(R.id.gid);
                holder.amount = (TextView) view.findViewById(R.id.amount);

                Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                        "font/Roboto-Light.ttf");
                holder.name.setTypeface(roboto);
                holder.date.setTypeface(roboto);
                holder.GID.setTypeface(roboto);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.name.setText(list.get(position).getName());
            holder.GID.setText(list.get(position).getGroup_id());

            String first = list.get(position).getAmount();
                String f = String.valueOf(first.charAt(0));
                if (f.equalsIgnoreCase("-")) {
                    String amount =list.get(position).getAmount().substring(1);
                    holder.date.setText(list.get(position).getDate());
                    holder.date.setTextColor(view.getResources().getColor(R.color.red));
                    holder.amount.setText(amount);
                    holder.amount.setTextColor(view.getResources().getColor(R.color.red));
                } else {
                    holder.date.setText(list.get(position).getDate());
                    holder.date.setTextColor(view.getResources().getColor(R.color.colorPrimary));
                    holder.amount.setText(list.get(position).getAmount());
                    holder.amount.setTextColor(view.getResources().getColor(R.color.colorPrimary));
                }



            if (position % 2 == 1) {
                view.setBackgroundColor(Color.WHITE);
            } else {
                view.setBackgroundColor(Color.WHITE);
            }

        }
        return view;
    }

    @Override
    public void remove(additem remitm) {
        list.remove(remitm);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
