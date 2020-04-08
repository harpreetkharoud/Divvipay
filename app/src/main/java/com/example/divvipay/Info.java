package com.divvipay.app;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Info extends Fragment {

    TextView InfoUser,Tag;

    public Info() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(),
                "font/Roboto-Regular.ttf");
        SharedPreferences prefs = getActivity().getSharedPreferences("LoginStatus", MODE_PRIVATE);
        String userName = prefs.getString("Name", "false");
        InfoUser=getActivity().findViewById(R.id.info_user);
        Tag=getActivity().findViewById(R.id.tag);
        InfoUser.setTypeface(roboto);
        Tag.setTypeface(roboto);
        InfoUser.setText(userName);
    }
}
