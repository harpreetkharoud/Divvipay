package com.divvipay.app;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    ListView listView;
    String[] firebase_user,firebase_user_uid,GROUNP_ID;
    String firebase_group_id,logined_user_phone_no,UserName,get_token;
    ArrayList<String> GroupId = new ArrayList<>();
    ArrayList<String> member=new ArrayList<>();
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> money=new ArrayList<>();
    ArrayList<String> member_match=new ArrayList<>();

    ArrayList<String> groupName=new ArrayList<>();
    ArrayList<String> time=new ArrayList<>();
    ArrayList<String> id=new ArrayList<>();
    ArrayList<String> amount=new ArrayList<>();
    ArrayList<String> lastMeesage=new ArrayList<>();

    int j,check=0,l;
    add_adapter adapter;
    List<additem> niitemlist = new ArrayList<>();
    ProgressDialog pd;
    TextView NoData;
    boolean connected = false;

    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        checkInternet();
        Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(),
                "font/Roboto-Light.ttf");

        NoData=getActivity().findViewById(R.id.noData);

        NoData.setTypeface(roboto);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        SharedPreferences prefs = getActivity().getSharedPreferences("LoginStatus", MODE_PRIVATE);
        logined_user_phone_no = prefs.getString("phone", "false");
        UserName = prefs.getString("Name", "false");

        niitemlist.clear();


        listView = (ListView) getActivity().findViewById(R.id.list_add);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView temp = (TextView) view.findViewById(R.id.name);
                        TextView temp1 = (TextView) view.findViewById(R.id.gid);
                        String str = temp.getText().toString();
                        String gid=temp1.getText().toString();


                    /*    FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add(R.id.frame_container_dash, new Chat_Message(str,gid), "Chat_Message");
                        ft.commit();*/


                        addFragment(new Chat_Message(str,gid), false, "Chat_Message");

                    }
                }
        );
        if(connected==true)
        {
            fillActivity();
        }
        else
        {
            pd.dismiss();
            Toast.makeText(getActivity(),"Please check internet connectivity",Toast.LENGTH_SHORT).show();

        }

    }

    private void fillActivity() {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null)
                    {
                        HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                        firebase_user = map.keySet().toArray(new String[map.size()]);
                        for (int i=0; i<firebase_user.length;i++)
                        {
                            String s=firebase_user[i];
                            firebase_user_uid=null;
                            HashMap<String, Object> map2 = (HashMap<String, Object>) map.get(s);
                            firebase_user_uid = map2.keySet().toArray(new String[map2.size()]);
                            for (int j=0; j<firebase_user_uid.length;j++) {
                                String a=firebase_user_uid[j];
                                HashMap<String, Object> map3 = (HashMap<String, Object>) map2.get(a);
                                firebase_group_id = String.valueOf(map3.get("GroupID"));
                                if (GroupId.contains(firebase_group_id)) {
                                    /// not add duplicate values
                                } else {
                                    GroupId.add(firebase_group_id);
                                }
                            }

                        }

                        if (GroupId.size() != 0) {
                            getMembers();
                        } else {

                            Toast.makeText(getActivity(),"1",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        pd.dismiss();
                        listView.setVisibility(View.GONE);
                        NoData.setVisibility(View.VISIBLE);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void getMembers()
    {
        DatabaseReference ref_id = FirebaseDatabase.getInstance().getReference("GroupDetail");
        try {

            ref_id.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                    GROUNP_ID=map.keySet().toArray(new String[map.size()]);
                    for(j=0;j<GROUNP_ID.length;j++)
                    {
                        check=0;
                        member.clear();
                        member_match.clear();
                        if(GroupId.contains(GROUNP_ID[j]))
                        {
                            String a=GROUNP_ID[j];
                            HashMap<String, ArrayList<String>> map3 = (HashMap<String, ArrayList<String>>) map.get(a);
                            member = map3.get("members");

                            for(int i=0;i<member.size(); i++)
                            {
                                String[] getNumber=member.get(i).split("--> ");
                                String quizType=getNumber[1];
                                member_match.add(quizType);
                            }

                            if (member_match.contains(logined_user_phone_no)) {
                                String groupname= String.valueOf(map3.get("groupName"));
                                String timea= String.valueOf(map3.get("time"));
                                String ida= String.valueOf(map3.get("id"));

                                groupName.add(groupname);
                                time.add(timea);
                                id.add(ida);
                            }
                            else
                            {
                                pd.dismiss();

                            }
                        }
                    }

                    getAmonut();
                    //getLastMessage();


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void getAmonut()
    {
        for ( l=0;l<id.size(); l++)
        {


            String a=id.get(l);

            DatabaseReference ref_id_pre = FirebaseDatabase.getInstance().getReference("GroupDetail/"+a+"/final_result/split_result");
            ref_id_pre.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    name.clear();
                    money.clear();

                    if(dataSnapshot.getValue()!=null)
                    {
                        GenericTypeIndicator<List<get_pre_value>> t = new GenericTypeIndicator<List<get_pre_value>>() {
                        };
                        List<get_pre_value> messages = dataSnapshot.getValue(t);
                        int a = messages.size();
                        for (int  i = 0; i < a; i++) {

                            name.add(messages.get(i).getName());
                            money.add(messages.get(i).getMoney());
                        }

                        for (int j=0;j<name.size();j++)
                        {
                            String[] getNumber=name.get(j).split("--> ");
                            String namea=getNumber[1];

                           // String namea=name.get(j);
                            if(namea.equalsIgnoreCase(logined_user_phone_no)) {
                                String amounta=money.get(j);
                                amount.add(amounta);

                            }
                        }

                        if(id.size()==amount.size()) {
                            UpdateUi();
                        }

                    }
                    else
                    {
                        amount.add("0.0");
                        if(id.size()==amount.size()) {
                            UpdateUi();
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }

            });
        }


    }



   /* private void getLastMessage()
    {
        for ( l=0;l<id.size(); l++)
        {


            String a=id.get(l);

            DatabaseReference ref_id_pre = FirebaseDatabase.getInstance().getReference("GroupDetail/"+a+"/LastMessage");
            try {

                ref_id_pre.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        HashMap<String,String> map = (HashMap<String, String >) dataSnapshot.getValue();
                       String LastMessage=map.get("lastmessage");

                        lastMeesage.add(LastMessage);

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }
*/

    private void UpdateUi()
    {
        for (int k=0;k<amount.size();k++)
        {
            String gn=groupName.get(k);
            String t=time.get(k);
            String i=id.get(k);
            String am=amount.get(k);
            niitemlist.add(new additem(gn, t, i,am));

        }

        if(niitemlist.size()==0)
        {
            NoData.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }


        adapter = new add_adapter(getActivity().getApplicationContext(), R.layout.add_text, niitemlist);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        pd.dismiss();

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

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
/*
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }*/
        ft.replace(R.id.frame_container_dash, fragment, tag);
        ft.commit();

    }
}
