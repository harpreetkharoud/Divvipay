package com.divvipay.app;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPlaceDetails extends Fragment {
    EditText Place,Name,PhoneNo;
    TextView Date,NoUser;
    CountryCodePicker ccp;
    Button Date_pick,Add_User,Submit;
    ImageView SelectContact;
    public DatePickerDialog datepick = null;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    ArrayList<String> GroupUserNo= new ArrayList<String>();
    ArrayList<String> GroupUserName= new ArrayList<String>();
    ArrayList<String> GroupUserNameNo= new ArrayList<String>();
    Firebase reference1,reference2;
    String randomNo,logined_user_phone_no,UserName;
    int i=1;
    boolean connected = false;
    CheckBox checkBox;
    LinearLayout linearLayout;

    public AddPlaceDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_place_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       //Edit Text
       Firebase.setAndroidContext(getActivity());


        Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(),
                "font/Roboto-Light.ttf");
       checkInternet();
       GroupUserNo.clear();
       GroupUserName.clear();
        Random r = new Random();
        randomNo = String.valueOf(r.nextInt(1000+1));

        SharedPreferences prefs = getActivity().getSharedPreferences("LoginStatus", MODE_PRIVATE);
        logined_user_phone_no = prefs.getString("phone", "false");
        UserName = prefs.getString("Name", "false");

       Place=(EditText) getActivity().findViewById(R.id.Textplace);
       Name=(EditText)getActivity().findViewById(R.id.friend_Name);
       PhoneNo=(EditText)getActivity().findViewById(R.id.friend_Number);

       NoUser=getActivity().findViewById(R.id.nouser);
       NoUser.setTypeface(roboto);
       //ImageView
        SelectContact=getActivity().findViewById(R.id.contact_list);

       //Country Code
        ccp = (CountryCodePicker)getActivity().findViewById(R.id.ccp);
        //Text View
        Date=(TextView) getActivity().findViewById(R.id.daye);

        //Button
        Date_pick= (Button) getActivity().findViewById(R.id.daypickbut);
        Add_User=(Button)getActivity().findViewById(R.id.add_btn);
        Submit=getActivity().findViewById(R.id.button_SUBMIT);

        // Check Box
        checkBox=getActivity().findViewById(R.id.add_you);
        checkBox.setText(logined_user_phone_no);

        GroupUserNo.add(logined_user_phone_no);
        GroupUserName.add(UserName);
        GroupUserNameNo.add(UserName+" --> "+logined_user_phone_no);
        checkBox.setChecked(true);
        checkBox.setEnabled(false);
        //Linear Layout
        linearLayout = (LinearLayout) getActivity().findViewById(R.id.linearMain);

        //Click Listners
        Date_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepick = new DatePickerDialog(v.getContext(), (DatePickerDialog.OnDateSetListener) new DatePickHandler(), Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                datepick.show();
            }
        });


        Add_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           if(GroupUserNo.contains(logined_user_phone_no))
           {

            if (!Name.getText().toString().equalsIgnoreCase("")) {
              if (ccp.getSelectedCountryCode().length() > 0 && PhoneNo.getText().length() > 0) {
                if (isValidPhoneNumber(PhoneNo.getText().toString()))
                {
                  boolean status = validateUsing_libphonenumber(ccp.getSelectedCountryCode(), PhoneNo.getText().toString());
                   if (status) {
                       if (checkBox.getText().equals("+" + ccp.getSelectedCountryCode() + PhoneNo.getText().toString()))
                       {
                       Toast.makeText(getActivity(), "Don't Add yourself , You already a member", Toast.LENGTH_SHORT).show();
                       }
                       else
                           {

                               PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                               try {
                                   // phone must begin with '+'
                                   if(PhoneNo.getText().toString().startsWith("+"))
                                   {
                                       Phonenumber.PhoneNumber numberProto = phoneUtil.parse(PhoneNo.getText().toString(), "");

                                       GroupUserNo.add("+" + ccp.getSelectedCountryCode() + numberProto.getNationalNumber());
                                   }
                                   else if(PhoneNo.getText().toString().startsWith("0"))
                                   {
                                       String number=PhoneNo.getText().toString().substring(1);
                                       GroupUserNo.add("+" + ccp.getSelectedCountryCode() + number);

                                   }
                                   else
                                   {
                                       GroupUserNo.add("+" + ccp.getSelectedCountryCode() + PhoneNo.getText().toString());

                                   }


                               } catch (NumberParseException e) {
                                   System.err.println("NumberParseException was thrown: " + e.toString());
                               }

                              GroupUserName.add(Name.getText().toString());
                              Button button = new Button(getActivity());
                              button.setText(GroupUserName.get(i)+" --> "+GroupUserNo.get(i));
                               GroupUserNameNo.add(GroupUserName.get(i)+" --> "+GroupUserNo.get(i));

                              NoUser.setVisibility(View.GONE);

                              Drawable drawable = getResources().getDrawable(R.drawable.ic_delete_button);
                              int bound = (int) (drawable.getIntrinsicWidth() * 0.05);
                              drawable.setBounds(0, 0, bound,bound);
                              button.setCompoundDrawables(null, null, drawable, null);
                              button.setId(i);
                              button.setBackgroundColor(Color.TRANSPARENT);
                              button.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  String names =button.getText().toString();
                                  String[] namesList = names.split(">");
                                  String getNo=namesList[1].trim();
                              int index=GroupUserNo.indexOf(getNo);
                              GroupUserName.remove(index);
                              GroupUserNo.remove(index);
                                  GroupUserNameNo.remove(index);
                              i--;
                              View namebar = v.findViewById(v.getId());
                              ((ViewGroup) namebar.getParent()).removeView(namebar);
                               }
                              });

                              linearLayout.addView(button);
                              PhoneNo.setText("");
                              PhoneNo.setEnabled(true);
                              Name.setText("");
                              Name.setEnabled(true);
                              i++;
                           }
                   }
                   else
                       {
                                                PhoneNo.setError("Invalid Phone Number");
                                                PhoneNo.setText("");
                                            }
                                        }

                else {
                                            PhoneNo.setError("Invalid Phone Number");
                                            PhoneNo.setText("");
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Country Code and Phone Number is required", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Name.setError("Please Enter Name");
                                }
                            }
                            else
                            {
                                checkBox.setError("Please select admin first");
                            }

            }

        });


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected==true) {
                    SaveDataFirebase();
                }
                else
                {
                    Toast.makeText(getActivity(),"Please check internet connectivity",Toast.LENGTH_SHORT).show();

                }
                }
        });


        SelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

                }
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 1);
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


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case(1):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String hasName= c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String num = "";
                        String name="";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                name = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                getPhone(num,name);
                            }
                        }
                    }
                    break;
                }
        }
    }

    public void getPhone(String Phone , String name)
    {
        String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
        Matcher m = null;

        Pattern r = Pattern.compile(pattern);
        if (!Phone.equalsIgnoreCase("")) {
            m = r.matcher(Phone);
        } else {
            Toast.makeText(getActivity(), "Please enter mobile number ", Toast.LENGTH_LONG).show();
        }
        if (m.find()) {
           // Toast.makeText(getActivity(), "MATCH", Toast.LENGTH_LONG).show();
            Name.setText(name);
            Name.setEnabled(false);
            PhoneNo.setText(Phone);
            PhoneNo.setEnabled(false);
        } else {
            Toast.makeText(getActivity(), "Invalid Number", Toast.LENGTH_LONG).show();
        }


    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            //return android.util.Patterns.PHONE.matcher(phoneNumber).matches();


            String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
            Matcher m = null;

            Pattern r = Pattern.compile(pattern);
            if (!phoneNumber.equals("")) {
                m = r.matcher(phoneNumber);
            } else {
                Toast.makeText(getActivity(), "Please enter mobile number ", Toast.LENGTH_LONG).show();
            }
            if (m.find()) {

                return  true;

            } else {
                Toast.makeText(getActivity(), "Invalid Number", Toast.LENGTH_LONG).show();
            }


        }
        return false;
    }

    private boolean validateUsing_libphonenumber(String countryCode, String phNumber) {
        if (!TextUtils.isEmpty(phNumber)) {

                try {

                    String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
                    Matcher m = null;

                    Pattern r = Pattern.compile(pattern);
                    if (!phNumber.equals("")) {
                        m = r.matcher(phNumber);
                    } else {
                        Toast.makeText(getActivity(), "Please enter mobile number ", Toast.LENGTH_LONG).show();
                    }
                    if (m.find()) {
                        // Toast.makeText(getActivity(), "MATCH", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Toast.makeText(getActivity(), "No Number Added", Toast.LENGTH_LONG).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


        }
        return false;
    }


    private void SaveDataFirebase() {

            if (Place.getText().toString().length() != 0|| Date.getText().toString().length() != 0) {

                reference1 = new Firebase("https://divvipay-d08bf.firebaseio.com/GroupDetail");

                List nameList = new ArrayList<String>((GroupUserNameNo));
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("groupName", Place.getText().toString());
                map.put("time", Date.getText().toString());
                map.put("members", nameList);
                map.put("id", randomNo);
                map.put("admin", logined_user_phone_no);
                reference1.child(randomNo).setValue(map);


                for (int i = 0; i < GroupUserNo.size(); i++) {
                    String Phone = GroupUserNo.get(i);
                    String Name = GroupUserName.get(i);
                    reference2 = new Firebase("https://divvipay-d08bf.firebaseio.com/Users");
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("Name", Name);
                    map1.put("Phone", Phone);
                    map1.put("GroupID", randomNo);
                    reference2.child(Phone).push().setValue(map1);
                }
                getActivity().finish();
                Intent Dashboard = new Intent(getActivity(), Dashboard.class);
                startActivity(Dashboard);
            } else {
                Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
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
}
