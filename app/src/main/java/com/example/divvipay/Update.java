package com.divvipay.app;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Update extends Fragment {
    EditText Title,Amount;
    Button Split,Date_pick,SelectBill;
    String str,gid,paidby,Bill;
    ArrayList<String> member=new ArrayList<>();
    ArrayList<String> selected_user=new ArrayList<>();
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> money=new ArrayList<>();
    Spinner Spinner;
    ArrayAdapter<String> adapterBusinessType;
    LinearLayout linearMain;
    CheckBox checkBox;
    int total_selected_user=0;
    double amount;
    ProgressDialog pd;
    Firebase reference1;
    boolean connected = false;
    private DatePickerDialog datepick = null;
    ArrayList<balancedata> list_items=new ArrayList<>();
    TextView Date;
    LinkedHashMap<String, String> alphabet = new LinkedHashMap<String, String>();
    public Update(String str, String gid) {
        this.str=str;
        this.gid=gid;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        member.clear();
        selected_user.clear();
        Firebase.setAndroidContext(getActivity());
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        //Linear lyout
        linearMain = (LinearLayout) getActivity().findViewById(R.id.linearMain);

        //Spinner
        Spinner = (Spinner) getActivity().findViewById(R.id.spinner);

        //Edit Text
        Title=getActivity().findViewById(R.id.editTitle);
        Amount=getActivity().findViewById(R.id.editAmount);

        //Button
        Split=getActivity().findViewById(R.id.button_ok);
        Date_pick= (Button) getActivity().findViewById(R.id.daypickbut);
        SelectBill=(Button)getActivity().findViewById(R.id.select_image);

        //TextView

        Date=(TextView) getActivity().findViewById(R.id.daye);

        DatabaseReference ref_id = FirebaseDatabase.getInstance().getReference("GroupDetail/"+gid);
        ref_id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    HashMap<String,ArrayList<String>> map = (HashMap<String, ArrayList<String>>) dataSnapshot.getValue();
                    member = map.get("members");

                    for(int i=0;i<member.size();i++)
                    {
                        String d4=member.get(i);
                        alphabet.put(String.valueOf(i),d4);
                        selected_user.add(member.get(i));

                    }
                    adapterBusinessType = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item,member);
                    adapterBusinessType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    Spinner.setAdapter(adapterBusinessType);
                    Set<?> set = alphabet.entrySet();
                    // Get an iterator
                    Iterator<?> i = set.iterator();
                    // Display elements
                    while (i.hasNext()) {
                        @SuppressWarnings("rawtypes")
                        Map.Entry me = (Map.Entry) i.next();
                        checkBox = new CheckBox(getActivity());
                        checkBox.setId(Integer.parseInt(me.getKey().toString()));
                        checkBox.setText(me.getValue().toString());
                        checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
                        linearMain.addView(checkBox);
                        checkBox.setChecked(true);
                    }
                    pd.dismiss();
                }
                else
                {
                    pd.dismiss();
                }
            }

            View.OnClickListener getOnClickDoSomething(final Button button) {
                return new View.OnClickListener() {
                    public void onClick(View v) {
                        System.out.println("*************id******" + button.getId());
                        System.out.println("and text***" + button.getText().toString());
                        // Toast.makeText(getActivity(),button.getText().toString(),Toast.LENGTH_SHORT).show();
                        if (((CheckBox) v).isChecked()) {
                            total_selected_user++;
                            selected_user.add(button.getText().toString());
                        }

                        if (!((CheckBox) v).isChecked()) {
                            total_selected_user--;
                            selected_user.remove(button.getText().toString());
                        }
                    }
                };
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref_id_pre = FirebaseDatabase.getInstance().getReference("GroupDetail/"+gid+"/final_result/split_result");
        ref_id_pre.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null) {
                    GenericTypeIndicator<List<get_pre_value>> t = new GenericTypeIndicator<List<get_pre_value>>() {
                    };
                    List<get_pre_value> messages = dataSnapshot.getValue(t);
                    int a = messages.size();

                    for (int i = 0; i < a; i++) {
                        name.add(messages.get(i).getName());
                        money.add(messages.get(i).getMoney());
                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        Date_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepick = new DatePickerDialog(v.getContext(), (DatePickerDialog.OnDateSetListener) new Update.DatePickHandler(), Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                datepick.show();
            }
        });


        SelectBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getActivity(), select_bill_activity.class);
                startActivityForResult(i, 1);
            }
        });

        Split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternet();

                if(connected==true)
                {
                    if(Title.getText().toString()!="" && Amount.getText().toString()!="" && Date.getText().toString()!="")
                    {
                        paidby = Spinner.getSelectedItem().toString();
                        String COL_2 = "friend_name";
                        String COL_3 = "note";
                        String COL_4 = "amount";
                        String COL_5 = "time";

                        List nameList = new ArrayList<String>((selected_user));
                        reference1 = new Firebase("https://divvipay-d08bf.firebaseio.com/GroupDetail/" + gid);
                        Map<String, Object> map1 = new HashMap<String, Object>();
                        map1.put(COL_2, paidby);
                        map1.put(COL_3, Title.getText().toString());
                        map1.put(COL_4, Amount.getText().toString());
                        map1.put(COL_5, Date.getText().toString());
                        map1.put("splitted_between",nameList);
                        reference1.child("expense_type").push().setValue(map1);

                        int size=selected_user.size();
                        Float total_amount= Float.valueOf(Amount.getText().toString());
                        double divide=(total_amount/size);

                        try {
                            for(int l=0; l<member.size();l++ )
                            {
                                int flag=0;
                                String namea=member.get(l);
                                if(namea.equalsIgnoreCase(paidby))
                                {
                                    amount=(divide*(size-1));
                                    for(int i=0;i<name.size();i++)
                                    {
                                        String namec=name.get(i);
                                        if(namec.equalsIgnoreCase(namea))
                                        {
                                            String moneyc=money.get(i);
                                            double money= Double.parseDouble(moneyc);
                                            amount= (money +(divide*(size-1)));
                                            list_items.add(new balancedata(namea,""+amount));
                                            flag=1;
                                            break;
                                        }

                                    }
                                    if(flag==0) {
                                        list_items.add(new balancedata(namea, "+" + amount));
                                    }
                                }
                                else if (selected_user.contains(namea))
                                {
                                    amount=(divide);
                                    for(int i=0;i<name.size();i++)
                                    {
                                        String namec=name.get(i);
                                        if(namec.equalsIgnoreCase(namea))
                                        {
                                            String moneyc=money.get(i);
                                            double money= Double.parseDouble(moneyc);
                                            amount= money+(-divide);
                                            list_items.add(new balancedata(namea,""+amount));
                                            flag=1;
                                            break;
                                        }
                                    }
                                    if(flag==0) {
                                        list_items.add(new balancedata(namea, "-" + divide));
                                    }
                                }
                                else
                                {
                                    for(int i=0;i<name.size();i++)
                                    {
                                        String namec=name.get(i);
                                        if(namec.equalsIgnoreCase(namea))
                                        {
                                            String moneyc=money.get(i);
                                            double money= Double.parseDouble(moneyc);
                                            amount= money;
                                            break;
                                        }
                                    }
                                    if(name.size()==0) {
                                        list_items.add(new balancedata(namea, "" + "0.0"));
                                    }
                                    else
                                    {
                                        list_items.add(new balancedata(namea, "" + amount));

                                    }

                                }
                            }
                            Map<String, Object> map2 = new HashMap<String, Object>();
                            map2.put("split_result", list_items);
                            reference1.child("final_result").setValue(map2);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
                        }
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.remove(Update.this);
                        ft.replace(R.id.frame_split, new Fragment_show(str,gid), "AddPlaceDetails");
                        ft.commit();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"All Fields are Mandatory",Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(getActivity(),"Please check internet connectivity",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public class DatePickHandler implements DatePickerDialog.OnDateSetListener {
        public void onDateSet(DatePicker view, int year, int month, int day) {
            int months = month+1;
            if((months<10)&&(day<10))
                Date.setText(year + "-0" + (months) + "-0" + day);
            else if((months<10)&&(day>10))
                Date.setText(year + "-0" + (months) + "-" + day);
            else if((months>10)&&(day<10))
                Date.setText(year + "-" + (months) + "-0" + day);
            else
                Date.setText(year + "-" + (months) + "-" + day);
            datepick.hide();
        }
    }


    private void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                 Bill = data.getStringExtra("bill");
                 Amount.setText(Bill);
            }
        }
    }
}
