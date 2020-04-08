package com.divvipay.app;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_balance extends Fragment {

    String str,gid,Name;
    ListView listView;
    ArrayList<balancedata> list_items=new ArrayList<>();
    balance_adapter adapter;
    ProgressDialog pd;
    TextView NoData;
    String[] gida;
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> money=new ArrayList<>();
    public Fragment_balance(String str , String gid) {
        // Required empty public constructor
        this.str=str;
        this.gid=gid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_balance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Firebase.setAndroidContext(getActivity());
        listView=getActivity().findViewById(R.id.list_above);
        NoData=getActivity().findViewById(R.id.noData);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        getAmount();

    }

    private void getAmount() {

        DatabaseReference ref_id_pre = FirebaseDatabase.getInstance().getReference("GroupDetail/"+gid+"/final_result/split_result");
        ref_id_pre.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null) {
                    GenericTypeIndicator<List<get_pre_value>> t = new GenericTypeIndicator<List<get_pre_value>>() {
                    };
                    List<get_pre_value> messages = dataSnapshot.getValue(t);
                    int a = messages.size();
                    for (int  i = 0; i < a; i++) {

                        name.add(messages.get(i).getName());
                        money.add(messages.get(i).getMoney());
                    }

                UpdateUi();
                }
                else
                {
                    NoData.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    pd.dismiss();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void UpdateUi() {
        int a=name.size();
        for(int j=0;j<a;j++)
        {
            String phone=name.get(j);
            String mon=money.get(j);
            String[] getNumber=phone.split("--> ");
            String na=getNumber[1];

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/"+na);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue()!=null)
                    {
                        HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                        gida = map.keySet().toArray(new String[map.size()]);
                        String s=gida[0];
                        HashMap<String, Object> map2 = (HashMap<String, Object>) map.get(s);
                        Name = String.valueOf(map2.get("Name"));
                        list_items.add(new balancedata(Name + "\n" + na, mon));
                    }
                    adapter= new balance_adapter(getActivity(),R.layout.balance_textview,list_items);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    pd.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}
