package com.divvipay.app;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Payment extends Fragment {

    String str,gid,Name;
    int j,a;

    ListView listView;
    ArrayList<balancedata> list_items=new ArrayList<>();
    payment_adapter adapter;
    ProgressDialog pd;
    TextView NoData;
    String[] gida;
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> money=new ArrayList<>();

    ArrayList<String> owesName=new ArrayList<String>();
    ArrayList<String> payName=new ArrayList<String>();

    ArrayList<String> oweAmount=new ArrayList<String>();
    ArrayList<String> payAmount=new ArrayList<String>();

    ArrayList<String> resultarr=new ArrayList<String>();


    ArrayList<String> finalresultamount=new ArrayList<String>();
    ArrayList<String> finalresultnaem=new ArrayList<String>();



    public Fragment_Payment( String str, String gid) {
        // Required empty public constructor
        this.str=str;
        this.gid=gid;
    }

    public Fragment_Payment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment__payment, container, false);
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
      a=name.size();
        for( j=0;j<a;j++)
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



                        if(String.valueOf(mon).startsWith("-"))
                        {
                            owesName.add(Name);
                            oweAmount.add(String.valueOf(mon));
                        }
                        else
                        {
                            payName.add(Name);
                            payAmount.add(String.valueOf(mon));
                        }

                        finalresultamount.add(String.valueOf(mon));
                        finalresultnaem.add(String.valueOf(Name));
                        list_items.add(new balancedata(Name + "\n" + na, String.valueOf(mon)));



                        postDatatoAdapter();
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



    }

    private void postDatatoAdapter() {


        if((owesName.size()+payName.size())==a)
        {
            for (int j = 0; j < owesName.size(); j++)
            {
                String OweName = owesName.get(j).toString();
                for (int i = 0; i < payName.size(); i++)
                {
                    float getAmount = Float.parseFloat((payAmount.get(i)));
                    String getName = payName.get(i);

                    int total = oweAmount.size();

                    float ShowAmount = getAmount / total;


                    String result = (OweName + " has to pay " + ShowAmount + " to " + getName);
                    if(resultarr.size()==0)
                    {
                        resultarr.add(result);

                    }
                    else if(resultarr.contains(result) || ShowAmount==0.0)
                    {
                        //do nothing
                    }
                    else
                    {
                        resultarr.add(result);
                    }
                    Comparator<String> ALPHABETICAL_ORDER1 = new Comparator<String>() {
                        public int compare(String object1, String object2) {
                            int res = String.CASE_INSENSITIVE_ORDER.compare(object1.toString(), object2.toString());
                            return res;
                        }
                    };

                    Collections.sort(resultarr, ALPHABETICAL_ORDER1);

                    adapter = new payment_adapter(getActivity(), R.layout.balance_textview, list_items,resultarr);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    pd.dismiss();


                }
            }
        }

    }
}
