package com.divvipay.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class Dashboard extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static int PICK_IMAGE_REQUEST = 1;

    TextView Heading, UserName, Info, Reward,Trip,Balance;
    boolean connected = false;
    ImageView AddTrip, Profile, Chat, Menu, Profile_Pic,Back;
    LinearLayout AddTrip_l, Profile_l, Chat_l, Info_l, Reward_l,Trip_l,Header,Tab;
    View Info_v, Reward_v,Trip_v;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard);
        //Image View
        AddTrip = findViewById(R.id.addTrip);
        Profile = findViewById(R.id.profile);
        Chat = findViewById(R.id.chat);
        Menu = findViewById(R.id.menu);
        Profile_Pic = findViewById(R.id.profile_pic);
        Back = findViewById(R.id.back);

        //Liner Layout

        AddTrip_l = findViewById(R.id.add_l);
        Chat_l = findViewById(R.id.chat_l);
        Profile_l = findViewById(R.id.profile_l);
        Info_l = findViewById(R.id.info_l);
        Reward_l = findViewById(R.id.reward_l);
        Trip_l = findViewById(R.id.trip_l);
        Header = findViewById(R.id.card_view_for_image);
        Tab = findViewById(R.id.tab);

        //Heading Text View
        Heading = findViewById(R.id.heading);
        UserName = findViewById(R.id.username);
        Info = findViewById(R.id.info);
        Reward = findViewById(R.id.reward);
        Trip = findViewById(R.id.trip);
        Balance=findViewById(R.id.balace);

        //View
        Info_v = findViewById(R.id.info_view);
        Reward_v = findViewById(R.id.reward_v);
        Trip_v = findViewById(R.id.trip_view);


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        } else {

            // Code for Below 23 API Oriented Device
            // Do next code
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        Typeface roboto = Typeface.createFromAsset(this.getAssets(),
                "font/Roboto-Medium.ttf");
        Heading.setTypeface(roboto);
        Heading.setText("Info");
        checkInternet();

        SharedPreferences prefs = getSharedPreferences("LoginStatus", MODE_PRIVATE);
        String userName = prefs.getString("Name", "false");
        UserName.setTypeface(roboto);
        UserName.setText(userName);


        if (connected == true) {

            SharedPreferences myPrefrence = getPreferences(MODE_PRIVATE);
            String imageS = myPrefrence.getString("imagePreferance", "");
            Bitmap imageB;
            if (!imageS.equals("")) {
                imageB = decodeToBase64(imageS);
                Profile_Pic.setImageBitmap(imageB);
            }

            Heading.setText("Profile");
            AddTrip.setImageResource(R.drawable.ic_add_grey);
            Chat.setImageResource(R.drawable.ic_chat_grey);
            Trip.setTextColor(getResources().getColor(R.color.colorPrimary));
            Trip_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            Back.setVisibility(View.GONE);
            addFragment(new AddPlace(), true, "AddPlace");
        } else {
            Toast.makeText(Dashboard.this, "Please check internet connectivity", Toast.LENGTH_SHORT).show();

        }

        AddTrip_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Heading.setText("Create Event");
                AddTrip.setImageResource(R.drawable.ic_add_green);
                Chat.setImageResource(R.drawable.ic_chat_grey);
                Profile.setImageResource(R.drawable.ic_profile_grey);
                Reward.setTextColor(Color.LTGRAY);
                Reward_v.setBackgroundColor(Color.LTGRAY);
                Info.setTextColor(Color.LTGRAY);
                Info_v.setBackgroundColor(Color.LTGRAY);
                Trip.setTextColor(Color.LTGRAY);
                Trip_v.setBackgroundColor(Color.LTGRAY);
                Header.setVisibility(View.GONE);
                Tab.setVisibility(View.GONE);
                Back.setVisibility(View.VISIBLE);
                addFragment(new AddPlaceDetails(), false, "AddPlaceDetails");

            }
        });

        Profile_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Heading.setText("Profile");
                Profile.setImageResource(R.drawable.ic_profile_green_icon);
                Trip.setTextColor(getResources().getColor(R.color.colorPrimary));
                Trip_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                AddTrip.setImageResource(R.drawable.ic_add_grey);
                Chat.setImageResource(R.drawable.ic_chat_grey);
                Reward.setTextColor(Color.LTGRAY);
                Reward_v.setBackgroundColor(Color.LTGRAY);
                Info.setTextColor(Color.LTGRAY);
                Info_v.setBackgroundColor(Color.LTGRAY);
                Header.setVisibility(View.VISIBLE);
                Tab.setVisibility(View.VISIBLE);
                Back.setVisibility(View.GONE);
                addFragment(new AddPlace(), false, "AddPlace");
            }
        });


        Chat_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Heading.setText("Chat");
                Chat.setImageResource(R.drawable.ic_chat_green_icon);
                AddTrip.setImageResource(R.drawable.ic_add_grey);
                Profile.setImageResource(R.drawable.ic_profile_grey);
                Reward.setTextColor(Color.LTGRAY);
                Reward_v.setBackgroundColor(Color.LTGRAY);
                Info.setTextColor(Color.LTGRAY);
                Info_v.setBackgroundColor(Color.LTGRAY);
                Trip.setTextColor(Color.LTGRAY);
                Trip_v.setBackgroundColor(Color.LTGRAY);
                Header.setVisibility(View.GONE);
                Tab.setVisibility(View.GONE);
                Back.setVisibility(View.VISIBLE);
                 addFragment(new Chat(), false, "Chat");


            }
        });

        Profile_Pic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);

            }
        });


        Info_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Info.setTextColor(getResources().getColor(R.color.colorPrimary));
                Info_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                Reward.setTextColor(Color.LTGRAY);
                Reward_v.setBackgroundColor(Color.LTGRAY);
                Trip.setTextColor(Color.LTGRAY);
                Trip_v.setBackgroundColor(Color.LTGRAY);
                AddTrip.setImageResource(R.drawable.ic_add_grey);
                Profile.setImageResource(R.drawable.ic_profile_green_icon);
                Chat.setImageResource(R.drawable.ic_chat_grey);
                addFragment(new Info(), false, "Info");

            }
        });

        Reward_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reward.setTextColor(getResources().getColor(R.color.colorPrimary));
                Reward_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                Info.setTextColor(Color.LTGRAY);
                Info_v.setBackgroundColor(Color.LTGRAY);
                Trip.setTextColor(Color.LTGRAY);
                Trip_v.setBackgroundColor(Color.LTGRAY);
                AddTrip.setImageResource(R.drawable.ic_add_grey);
                Profile.setImageResource(R.drawable.ic_profile_green_icon);
                Chat.setImageResource(R.drawable.ic_chat_grey);
                addFragment(new Rewards(), false, "Rewards");






            }
        });


        Trip_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addFragment(new AddPlace(), true, "AddPlace");

                Trip.setTextColor(getResources().getColor(R.color.colorPrimary));
                Trip_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                Info.setTextColor(Color.LTGRAY);
                Info_v.setBackgroundColor(Color.LTGRAY);
                Reward.setTextColor(Color.LTGRAY);
                Reward_v.setBackgroundColor(Color.LTGRAY);
                AddTrip.setImageResource(R.drawable.ic_add_grey);
                Profile.setImageResource(R.drawable.ic_profile_green_icon);
                Chat.setImageResource(R.drawable.ic_chat_grey);


            }
        });

        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Dashboard.this, v);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.option_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.logout:
                                signout();
                                return true;
                            default:
                                return true;
                        }
                    }
                });

                popup.show();//showing popup menu

            }
        });


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Heading.setText("Profile");
                Profile.setImageResource(R.drawable.ic_profile_green_icon);
                Trip.setTextColor(getResources().getColor(R.color.colorPrimary));
                Trip_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                AddTrip.setImageResource(R.drawable.ic_add_grey);
                Chat.setImageResource(R.drawable.ic_chat_grey);
                Reward.setTextColor(Color.LTGRAY);
                Reward_v.setBackgroundColor(Color.LTGRAY);
                Info.setTextColor(Color.LTGRAY);
                Info_v.setBackgroundColor(Color.LTGRAY);
                Header.setVisibility(View.VISIBLE);
                Tab.setVisibility(View.VISIBLE);
                Back.setVisibility(View.GONE);
                addFragment(new AddPlace(), false, "AddPlace");
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            InputStream stream;
            try {
                Toast.makeText(Dashboard.this, "Profile photo updated", Toast.LENGTH_SHORT).show();
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap realImage = BitmapFactory.decodeStream(stream);
                Profile_Pic.setImageBitmap(realImage);


                SharedPreferences myPrefrence = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefrence.edit();
                editor.putString("imagePreferance", encodeToBase64(realImage));

                editor.commit();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


    }

    private void signout() {
        SharedPreferences preferences = getSharedPreferences("LoginStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();


        SharedPreferences myPrefrence = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editora = myPrefrence.edit();
        editora.clear();
        editora.commit();

        FirebaseAuth.getInstance().signOut();
        signOutGmail();

    }

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
/*
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }*/
        ft.replace(R.id.frame_container_dash, fragment, tag);
        ft.commit();

    }

    private void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;

    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Dashboard.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Dashboard.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Dashboard.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Dashboard.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public static String encodeToBase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentByTag("AddPlaceDetails") != null){

            Heading.setText("Profile");
            Profile.setImageResource(R.drawable.ic_profile_green_icon);
            Trip.setTextColor(getResources().getColor(R.color.colorPrimary));
            Trip_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            AddTrip.setImageResource(R.drawable.ic_add_grey);
            Chat.setImageResource(R.drawable.ic_chat_grey);
            Reward.setTextColor(Color.LTGRAY);
            Reward_v.setBackgroundColor(Color.LTGRAY);
            Info.setTextColor(Color.LTGRAY);
            Info_v.setBackgroundColor(Color.LTGRAY);
            Header.setVisibility(View.VISIBLE);
            Tab.setVisibility(View.VISIBLE);
            Back.setVisibility(View.GONE);
            addFragment(new AddPlace(), false, "AddPlace");
        }
        else if(getSupportFragmentManager().findFragmentByTag("Info") != null)
        {
            Heading.setText("Profile");
            Profile.setImageResource(R.drawable.ic_profile_green_icon);
            Trip.setTextColor(getResources().getColor(R.color.colorPrimary));
            Trip_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            AddTrip.setImageResource(R.drawable.ic_add_grey);
            Chat.setImageResource(R.drawable.ic_chat_grey);
            Reward.setTextColor(Color.LTGRAY);
            Reward_v.setBackgroundColor(Color.LTGRAY);
            Info.setTextColor(Color.LTGRAY);
            Info_v.setBackgroundColor(Color.LTGRAY);
            Header.setVisibility(View.VISIBLE);
            Tab.setVisibility(View.VISIBLE);
            Back.setVisibility(View.GONE);
            addFragment(new AddPlace(), false, "AddPlace");

        }
        else if(getSupportFragmentManager().findFragmentByTag("Chat") != null)
        {
            Heading.setText("Profile");
            Profile.setImageResource(R.drawable.ic_profile_green_icon);
            Trip.setTextColor(getResources().getColor(R.color.colorPrimary));
            Trip_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            AddTrip.setImageResource(R.drawable.ic_add_grey);
            Chat.setImageResource(R.drawable.ic_chat_grey);
            Reward.setTextColor(Color.LTGRAY);
            Reward_v.setBackgroundColor(Color.LTGRAY);
            Info.setTextColor(Color.LTGRAY);
            Info_v.setBackgroundColor(Color.LTGRAY);
            Header.setVisibility(View.VISIBLE);
            Tab.setVisibility(View.VISIBLE);
            Back.setVisibility(View.GONE);
            addFragment(new AddPlace(), false, "AddPlace");

        }

        else if(getSupportFragmentManager().findFragmentByTag("Chat_Message") != null)
        {
            Heading.setText("Chat");
            Chat.setImageResource(R.drawable.ic_chat_green_icon);
            AddTrip.setImageResource(R.drawable.ic_add_grey);
            Profile.setImageResource(R.drawable.ic_profile_grey);
            Reward.setTextColor(Color.LTGRAY);
            Reward_v.setBackgroundColor(Color.LTGRAY);
            Info.setTextColor(Color.LTGRAY);
            Info_v.setBackgroundColor(Color.LTGRAY);
            Trip.setTextColor(Color.LTGRAY);
            Trip_v.setBackgroundColor(Color.LTGRAY);
            Header.setVisibility(View.GONE);
            Tab.setVisibility(View.GONE);
            Back.setVisibility(View.VISIBLE);
            addFragment(new Chat(), false, "Chat");
        }
        else if(getSupportFragmentManager().findFragmentByTag("Rewards") != null)
        {
            Heading.setText("Profile");
            Profile.setImageResource(R.drawable.ic_profile_green_icon);
            Trip.setTextColor(getResources().getColor(R.color.colorPrimary));
            Trip_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            AddTrip.setImageResource(R.drawable.ic_add_grey);
            Chat.setImageResource(R.drawable.ic_chat_grey);
            Reward.setTextColor(Color.LTGRAY);
            Reward_v.setBackgroundColor(Color.LTGRAY);
            Info.setTextColor(Color.LTGRAY);
            Info_v.setBackgroundColor(Color.LTGRAY);
            Header.setVisibility(View.VISIBLE);
            Tab.setVisibility(View.VISIBLE);
            Back.setVisibility(View.GONE);
            addFragment(new AddPlace(), false, "AddPlace");

        }
        else{
            if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
                return;
            }
        }
    }

    public void UpdateBalance(int balance)
    {
        String current_Balance=Balance.getText().toString();
        float current= Float.parseFloat(current_Balance);

        SharedPreferences.Editor editor =getSharedPreferences("Wallet", MODE_PRIVATE).edit();
        editor.putInt("Wallet", (int) (balance+current));
        editor.apply();

        SharedPreferences prefs = getSharedPreferences("Wallet", MODE_PRIVATE);
        int count = prefs.getInt("Wallet", 0);

        Balance.setText(count);
    }


    public void signOutGmail() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(Dashboard.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        revokeAccess();
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(Dashboard.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Intent Main = new Intent(Dashboard.this, google_sign_in.class);
                        startActivity(Main);
                        finish();
                    }
                });
    }
    }
