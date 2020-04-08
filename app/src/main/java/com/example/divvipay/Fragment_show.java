package com.divvipay.app;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_show extends Fragment {

    String str,gid,Name;
    ProgressDialog pd;
    private ListView expense_list;
    TextView nodata;
    FloatingActionButton add_expense;
    String[] expenseType;
    private List<item> niitemlist=new ArrayList<>();
    private adapter_Show adapter;
    ArrayList<String > splitbtwn= new ArrayList<String>();
    Vector<Integer> vec =new Vector<>();
    ArrayList<item> list_items= new ArrayList<>();
    ArrayList<String> member=new ArrayList<>();
    ArrayList<String> nameaa=new ArrayList<>();
    ArrayList<String> moneyaa=new ArrayList<>();
    ArrayList<balancedata> list_itemsa=new ArrayList<>();
    double amounta;
    Firebase reference1;
    int count=0;

    public Fragment_show( String str, String gid) {
        // Required empty public constructor
        this.str=str;
        this.gid=gid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_show, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pd = new ProgressDialog(getActivity());
        Firebase.setAndroidContext(getActivity());
        ((ViewTripDetail)getActivity()).header("Expenses a trip");

        pd.setMessage("Loading...");
        pd.show();
        pd.setCancelable(false);

        niitemlist.clear();
        //List View
        expense_list = (ListView) getActivity().findViewById(R.id.list_view);

        //Text View
        nodata=(TextView)getActivity().findViewById(R.id.noData);



        //Floatting Button
        add_expense = (FloatingActionButton) getActivity().findViewById(R.id.myFAB);

        //Click Listners
        add_expense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add(R.id.frame_split, new Update(str,gid), "Update");
                        ft.commit();
                        ((ViewTripDetail)getActivity()).header("Add expenses");
                    }
                }
        );

        //Expense Type
        //--------------------------Start--------------------------------//

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("GroupDetail/"+gid+"/expense_type");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                    expenseType = map.keySet().toArray(new String[map.size()]);
                    int k = expenseType.length;
                    for (int i = 0; i < k; i++) {
                        String s = expenseType[i];
                        HashMap<String, Object> map2 = (HashMap<String, Object>) map.get(s);
                        String ti = String.valueOf(map2.get("note"));
                        String am = String.valueOf(map2.get("amount"));
                        String n = String.valueOf(map2.get("friend_name"));
                        String tim = String.valueOf(map2.get("time"));

                        String[] getNumber=n.split("--> ");
                        String na=getNumber[1];

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/"+na);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue()!=null)
                                {
                                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                                    String[] gid = map.keySet().toArray(new String[map.size()]);
                                    for (int i=0; i<gid.length;i++)
                                    {
                                        String s=gid[i];
                                        HashMap<String, Object> map2 = (HashMap<String, Object>) map.get(s);
                                        Name = String.valueOf(map2.get("Name"));
                                        niitemlist.add(new item(ti,am,Name+"\n"+na,tim));
                                        vec.add(1);
                                        try {
                                            //adapter=new adapter_Show(Fragment_show.this,niitemlist);
                                            adapter =new adapter_Show(getActivity(),R.layout.textfile,niitemlist);
                                            expense_list.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                            pd.dismiss();

                                        } catch (Exception e) {
                                            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                                            System.out.println(e);
                                        }
                                        break;

                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                }
                else
                {
                    expense_list.setVisibility(View.GONE);
                    nodata.setVisibility(View.VISIBLE);
                    pd.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //-------------------------------End-------------------------------//




        //Get Current Split Resuly
        //---------------------Start---------------------//

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
                        nameaa.add(messages.get(i).getName());
                        moneyaa.add(messages.get(i).getMoney());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
       //---------------------End----------------------//



        //Get Total Users
        //---------------------------Start--------------------------//

        DatabaseReference ref_id = FirebaseDatabase.getInstance().getReference("GroupDetail/"+gid);
        ref_id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,ArrayList<String>> map = (HashMap<String, ArrayList<String>>) dataSnapshot.getValue();
                member = map.get("members");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       //----------------------------End--------------------------//


        expense_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        expense_list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                if(vec.get(i)==0)
                {
                    list_items.remove(niitemlist.get(i));
                    count -= 1;
                    expense_list.getChildAt(i).setBackgroundColor(Color.WHITE);

                    vec.set(i,1);
                    String ii=Integer.toString(i);
                    Log.e("not select",ii);

                    actionMode.setTitle(count + " items selected");
                } else
                    {
                    count += 1;
                    if(count==1) {
                        expense_list.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                        vec.set(i,0);
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Select Only Sinlge Entry",Toast.LENGTH_SHORT).show();                        expense_list.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                        expense_list.getChildAt(i).setBackgroundColor(Color.RED);
                        count--;
                        vec.set(i,1);
                    }

                    String ii=Integer.toString(i);
                    Log.e("select",ii);
                    actionMode.setTitle(count + " items selected");
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.my_context_menu, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete_id:
                        pd.show();
                        for (int i=vec.size()-1;i>-1;i--) {
                            if (vec.get(i) == 0) {
                                TextView t = (TextView) getActivity().findViewById(R.id.name);
                                String name=niitemlist.get(i).getName();
                                String amount=niitemlist.get(i).getAmount();
                                String note=niitemlist.get(i).getNote();

                                DatabaseReference ref_get = FirebaseDatabase.getInstance().getReference("GroupDetail/"+gid+"/expense_type");
                                ref_get.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                                            expenseType = map.keySet().toArray(new String[map.size()]);
                                            int k = expenseType.length;
                                            for (int i = 0; i < k; i++) {
                                                String s = expenseType[i];
                                                HashMap<String, Object> map2 = (HashMap<String, Object>) map.get(s);
                                                String ti = String.valueOf(map2.get("note"));
                                                String am = String.valueOf(map2.get("amount"));
                                                String n = String.valueOf(map2.get("friend_name"));
                                                if(note.equalsIgnoreCase(ti))
                                                {
                                                 splitbtwn= (ArrayList<String>) map2.get("splitted_between");
                                                 for(int a=0;a<splitbtwn.size();a++)
                                                 {
                                                     list_itemsa.clear();
                                                     int total_amount= Integer.parseInt(am);
                                                     double divide=(total_amount/splitbtwn.size());

                                                     try {
                                                         for(int l=0; l<member.size();l++ )
                                                         {
                                                             String namea=member.get(l);
                                                             if(namea.equalsIgnoreCase(n))
                                                             {
                                                                 for(int q=0;q<nameaa.size();q++)
                                                                 {
                                                                     String namec=nameaa.get(q);
                                                                     if(namec.equalsIgnoreCase(namea))
                                                                     {
                                                                         String moneyc=moneyaa.get(q);
                                                                         double money= Double.parseDouble(moneyc);
                                                                         amounta= (money -(divide*(splitbtwn.size()-1)));
                                                                         list_itemsa.add(new balancedata(namea,""+amounta));
                                                                         break;
                                                                     }
                                                                 }
                                                             }
                                                             else if (splitbtwn.contains(namea))
                                                             {
                                                                 amounta=(divide);
                                                                 for(int q=0;q<nameaa.size();q++)
                                                                 {
                                                                     String namec=nameaa.get(q);
                                                                     if(namec.equalsIgnoreCase(namea))
                                                                     {
                                                                         String moneyc=moneyaa.get(q);
                                                                         double money= Double.parseDouble(moneyc);
                                                                         amounta= money+(divide);
                                                                         list_itemsa.add(new balancedata(namea,""+amounta));
                                                                         break;
                                                                     }
                                                                 }
                                                             }
                                                             else
                                                             {
                                                                 for(int q=0;q<nameaa.size();q++)
                                                                 {
                                                                     String namec=nameaa.get(q);
                                                                     if(namec.equalsIgnoreCase(namea))
                                                                     {
                                                                         String moneyc=moneyaa.get(q);
                                                                         double money= Double.parseDouble(moneyc);
                                                                         amounta= money;
                                                                         break;
                                                                     }
                                                                 }
                                                                 list_itemsa.add(new balancedata(namea,""+amounta));
                                                             }
                                                         }
                                                         Map<String, Object> map3 = new HashMap<String, Object>();
                                                         map3.put("split_result", list_itemsa);
                                                         reference1 = new Firebase("https://divvipay-d08bf.firebaseio.com/GroupDetail/" + gid);
                                                         reference1.child("final_result").setValue(map3);

                                                     } catch (Exception e) {
                                                         Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
                                                     }
                                                 }
                                                }

                                                vec.add(1);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                vec.set(i,1);
                                expense_list.getChildAt(i).setBackgroundColor(Color.WHITE);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                Query applesQuery = ref.child("GroupDetail").child(gid).child("expense_type").orderByChild("note").equalTo(note);
                                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshota) {
                                        for (DataSnapshot appleSnapshot: dataSnapshota.getChildren()) {
                                            appleSnapshot.getRef().removeValue();
                                            pd.dismiss();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e(TAG, "onCancelled", databaseError.toException());
                                    }
                                });
                                pd.dismiss();
                                niitemlist.remove(i);

                            }
                            adapter.notifyDataSetChanged();
                        }

                        Toast.makeText(getActivity(), count + " items removed ", Toast.LENGTH_SHORT).show();
                        count = 0;
                        actionMode.finish();
                        getActivity().finish();
                        return true;
                    default:
                        return false;


                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

    }




}
