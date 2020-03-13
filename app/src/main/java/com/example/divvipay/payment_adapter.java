package com.divvipay.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class payment_adapter extends BaseAdapter {

    Activity activity;
    int balance_textview;
    ArrayList<balancedata> list_items=new ArrayList<balancedata>();

    ArrayList<String> resultarr=new ArrayList<String>();


    public payment_adapter(FragmentActivity activity, int balance_textview, ArrayList<balancedata> list_items, ArrayList<String> resultarr) {
       this.activity=activity;
       this.balance_textview=balance_textview;
       this.list_items=list_items;
       this.resultarr=resultarr;

    }

    @Override
    public int getCount() {
        return resultarr.size();
    }

    @Override
    public Object getItem(int position) {
        return resultarr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        Typeface roboto = Typeface.createFromAsset(activity.getAssets(),
                "font/Roboto-Light.ttf");
        UserListHolder holder = null;

        if(view==null)
        {
            LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.payment, null);
            holder = new UserListHolder();
            holder.name = (TextView) view.findViewById(R.id.payment);
            view.setTag(holder);
        }
        try
        {
            holder.name.setTypeface(roboto);
        holder.name.setText(resultarr.get(position).toString());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("error", e.toString());
        }

        return view;
    }

    public class UserListHolder {
        TextView name;

    }
}
