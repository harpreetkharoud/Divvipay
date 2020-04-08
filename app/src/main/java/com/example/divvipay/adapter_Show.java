package com.divvipay.app;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.divvipay.app.R;

import java.util.List;

/**
 * Created by hp15-p017tu on 25-07-2016.
 */



public class adapter_Show extends ArrayAdapter<com.divvipay.app.item> {
    private SparseBooleanArray mSelectedItemsIds;
    private LayoutInflater inflater;
    private Context mContext;
    private List<item> list;

    public adapter_Show (Context context, int resourceId, List<item> list) {
        super(context, resourceId, list);
        mSelectedItemsIds = new SparseBooleanArray();
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.list = list;
    }

    private static class ViewHolder {
        TextView name;
        TextView note;
        TextView amount;
        TextView time;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                "font/Roboto-Light.ttf");
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.textfile, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.note = (TextView) view.findViewById(R.id.note);
            holder.amount = (TextView) view.findViewById(R.id.amount);
            holder.time=(TextView)view.findViewById(R.id.Time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setTypeface(roboto);
        holder.amount.setTypeface(roboto);
        holder.note.setTypeface(roboto);
        holder.time.setTypeface(roboto);

        holder.note.setText(list.get(position).getNote());
        holder.amount.setText(list.get(position).getAmount());
        holder.name.setText(list.get(position).getName()+"(Paid)");
        holder.time.setText(list.get(position).getTime());

        return view;
    }

    @Override
    public void remove(item remitm) {
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
