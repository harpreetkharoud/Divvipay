package com.divvipay.app;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.renderscript.Sampler;
import android.text.Layout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat_Message extends Fragment {

    String str,gid,generatedFilePath,filename;
    ImageView sendButton,buttonAttach,Selected_Image,Close;
    Firebase reference1,reference2,reference3;
    EditText message;
    String logined_user_phone_no,UserName,get_token;
    LinearLayout layout;
    ScrollView scrollView;
    ProgressDialog pd;
    VideoView simpleVideoView;


    int Upload = 0;
    boolean isImageFitToScreen;
    StorageReference storageReference;
    ArrayList<String> imagerefresh=new ArrayList<String>();

    private Uri filePath;
    int position=0;

    public Chat_Message(String str, String gid) {

        this.str=str;
        this.gid=gid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat__message, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Firebase.setAndroidContext(getActivity());
        FirebaseStorage storage = FirebaseStorage.getInstance();
         storageReference = storage.getReference();

        Selected_Image=getActivity().findViewById(R.id.selected_image);
        simpleVideoView = (VideoView) getActivity().findViewById(R.id.VideoView);
        Close=getActivity().findViewById(R.id.close);



        SharedPreferences prefs = getActivity().getSharedPreferences("LoginStatus", MODE_PRIVATE);
        logined_user_phone_no = prefs.getString("phone", "false");
        get_token = prefs.getString("token", "false");
        UserName = prefs.getString("Name", "false");



        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading Chat...");
        pd.setCancelable(false);


        layout = (LinearLayout)getActivity().findViewById(R.id.layout1);
        scrollView = (ScrollView)getActivity().findViewById(R.id.scrollView);
        reference2 = new Firebase("https://divvipay-d08bf.firebaseio.com/GroupDetail/"+gid+"/GroupMessage");



        buttonAttach=getActivity().findViewById(R.id.btnAttach);

        sendButton=getActivity().findViewById(R.id.sendButton);
        message=getActivity().findViewById(R.id.messageArea);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadFile();
                sendNotification();
            }
        });





        reference2.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                if(map.isEmpty())
                {

                }
                else {
                    pd.show();
                    String message = map.get("message").toString();
                    String userName = map.get("Name").toString();
                    String MobileNumber = map.get("MobileNumber").toString();
                    String Type = map.get("type").toString();
                    String image = "";

                    try {
                        image = map.get("image").toString();

                        if (image == null) {
                            image = "";
                        }
                    } catch (Exception e) {
                        image = "";
                    }


                    if (MobileNumber.equals(logined_user_phone_no)) {
                        if (image.equalsIgnoreCase("")) {
                            addMessageBox(message, userName + "    ", 1, Type);
                        } else {
                            addImageBox(message, userName + "    ", image, 1, Type);
                        }
                    } else {
                        if (image.equalsIgnoreCase("")) {
                            addMessageBox(message, userName + "    ", 2, Type);
                        } else {
                            addImageBox(message, userName + "    ", image, 2, Type);
                        }
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        buttonAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] options = {"Images", "Videos", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select From...");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Images")) {

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);

                            }
                            else if (options[item].equals("Videos")) {
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Video"), 2);


                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });



    }

    private void sendNotification() {

        reference3 = new Firebase("https://divvipay-d08bf.firebaseio.com/GroupDetail/"+gid+"/TokenId");
        reference3.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map map = dataSnapshot.getValue(Map.class);
                if(map.isEmpty())
                {

                }
                else {

                    String Token = map.get("token").toString();
                    if(get_token.equalsIgnoreCase(Token)) {

                    }
                    else
                    {
                        pushnotification(Token);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }

    private void pushnotification(String token) {



        Thread thread = new Thread(new Runnable() {

            @Override
            public void run()
            {
                try  {
                    try {

                        final String apiKey = "AAAAiz3ZGhs:APA91bElgYAYDUlUk0RL2A_67y2t4-944-0_qKSQFLiCKJr4CmQSkILqp_ey103xHI-pOxq5nnDHn0xlv55G7MNHvRXDlvN15wxE384aYGR2hx93pe2qEdrZ2tkO2X1n9QTa80_0kocY";
                        URL url = new URL("https://divvipay-d08bf.firebaseio.com/fcm/send");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Authorization", "key=" + apiKey);
                        conn.setDoOutput(true);
                        JSONObject message = new JSONObject();
                        message.put("to", token);
                        message.put("priority", "high");
                        JSONObject notification = new JSONObject();
                        // notification.put("title", title);
                        notification.put("body", str);
                        message.put("data", notification);
                        OutputStream os = conn.getOutputStream();
                        os.write(message.toString().getBytes());
                        os.flush();
                        os.close();

                        int responseCode = conn.getResponseCode();
                        System.out.println("\nSending 'POST' request to URL : " + url);
                        System.out.println("Post parameters : " + message.toString());
                        System.out.println("Response Code : " + responseCode);
                        System.out.println("Response Code : " + conn.getResponseMessage());

                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // print result
                        System.out.println(response.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Upload=0;
            filePath = data.getData();
            Close.setVisibility(View.VISIBLE);
            Close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Selected_Image.setVisibility(View.GONE);
                    Close.setVisibility(View.GONE);
                }
            });
// it contains your image path...I'm using a temp string...
            filename=String.valueOf(filePath).substring(String.valueOf(filePath).lastIndexOf("/")+1);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(500, 500);


            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                Selected_Image.setVisibility(View.VISIBLE);
                Selected_Image.setLayoutParams(layoutParams);
                Selected_Image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Upload=1;
            filePath = data.getData();
            Close.setVisibility(View.VISIBLE);
            Close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    simpleVideoView.setVisibility(View.GONE);
                    Close.setVisibility(View.GONE);
                }
            });



            filename=String.valueOf(filePath).substring(String.valueOf(filePath).lastIndexOf("/")+1);




            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) simpleVideoView.getLayoutParams();
            params.width = metrics.widthPixels;
            params.height = metrics.heightPixels;
            params.leftMargin = 0;


            simpleVideoView.setVisibility(View.VISIBLE);
            simpleVideoView.setLayoutParams(params);// initiate a video vie
            MediaController m = new MediaController(getActivity());
            m.setAnchorView(simpleVideoView);
            simpleVideoView.setMediaController(m);
            simpleVideoView.setVideoURI(filePath);
            simpleVideoView.requestFocus();
            simpleVideoView.start();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addMessageBox(String message,String time, int type,String Type){
        TextView textView = new TextView(getActivity());
        textView.setText(message);
        TextView texttime = new TextView(getActivity());
        texttime.setText(time);

        texttime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);



        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;
        lp3.setMargins(40, 40, 40, 0);
        lp2.setMargins(40, 0, 40, 10);


        if(type == 1) {
            lp3.gravity = Gravity.RIGHT;
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.text_in);
        }
        else{
            lp3.gravity = Gravity.LEFT;
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.text_out);
        }
        texttime.setLayoutParams(lp3);
        textView.setLayoutParams(lp2);
        layout.addView(texttime);
        layout.addView(textView);


        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 600);


        pd.dismiss();
    }




    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addImageBox (String message,String time, String  image,int type,String Type){



        imagerefresh.add(image);


        if(Type.equalsIgnoreCase("image")) {
            ImageView imageView = new ImageView(getActivity());

            Picasso.get().load(image)
                    .error(R.drawable.ic_update_arrow)
                    .placeholder(R.drawable.progress_animation)
                    .resize(500, 500)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso.get()
                                    .load(image) // image url goes here
                                    .resize(500, 500)
                                    .placeholder(imageView.getDrawable())
                                    .into(imageView);
                            pd.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {


                        }
                    });


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pos= (String) v.getTag();

                    String u=imagerefresh.get(Integer.parseInt(pos));
                    Intent intent = new Intent(getActivity(), FullScreenImage.class);

                    intent.putExtra("imagebitmap", u);
                    startActivity(intent);

                }
            });


            TextView textView = new TextView(getActivity());
            if(message.equalsIgnoreCase(""))
            {
                textView.setVisibility(View.GONE);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
            else
                {
                textView.setVisibility(View.VISIBLE);
                textView.setText(message);
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                }
            TextView texttime = new TextView(getActivity());
            texttime.setText(time);

            texttime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;
            lp3.setMargins(40, 40, 40, 0);
            lp2.setMargins(40, 0, 40, 10);


            if(type == 1) {
                lp3.gravity = Gravity.RIGHT;
                lp2.gravity = Gravity.RIGHT;
                textView.setBackgroundResource(R.drawable.text_in);
            }
            else{
                lp3.gravity = Gravity.LEFT;
                lp2.gravity = Gravity.LEFT;
                textView.setBackgroundResource(R.drawable.text_out);
            }
            texttime.setLayoutParams(lp3);
            textView.setLayoutParams(lp2);
            imageView.setLayoutParams(lp2);
            layout.addView(texttime);
            layout.addView(textView);
            layout.addView(imageView);



            imageView.setTag(Integer.toString(position));
            position++;

            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 600);

            pd.dismiss();


        }

        if(Type.equalsIgnoreCase("video")) {


            WebView webview =  new WebView(getActivity());
            webview.setWebViewClient(new WebViewClient());
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webview.getSettings().setPluginState(WebSettings.PluginState.ON);
            webview.getSettings().setMediaPlaybackRequiresUserGesture(true);
            webview.setWebChromeClient(new WebChromeClient());
            webview.loadUrl(image);

/*
            webview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    String pos= (String) v.getTag();

                    String u=imagerefresh.get(Integer.parseInt(pos));


                    Intent intent = new Intent(getActivity(), FullScreenVideo.class);

                    intent.putExtra("video", u);
                    startActivity(intent);
                    return false;
                }
            });*/

            TextView textView = new TextView(getActivity());
            if(message.equalsIgnoreCase(""))
            {
                textView.setVisibility(View.GONE);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
            else
            {
                textView.setVisibility(View.VISIBLE);
                textView.setText(message);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }

            TextView texttime = new TextView(getActivity());
            texttime.setText(time);

            texttime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(500,500);
            //WebView.LayoutParams lp4 = new WebView.LayoutParams(ViewGroup.LayoutParams);
            lp2.weight = 1.0f;
            lp3.setMargins(40, 40, 40, 0);
            lp2.setMargins(40, 0, 40, 10);


            if(type == 1) {
                lp3.gravity = Gravity.RIGHT;
                lp2.gravity = Gravity.RIGHT;
                lp4.gravity=Gravity.RIGHT;
                textView.setBackgroundResource(R.drawable.text_in);
            }
            else{
                lp3.gravity = Gravity.LEFT;
                lp2.gravity = Gravity.LEFT;
                lp4.gravity = Gravity.LEFT;
                textView.setBackgroundResource(R.drawable.text_out);
            }
            texttime.setLayoutParams(lp3);
            textView.setLayoutParams(lp2);

            webview.setLayoutParams(lp4);
            layout.addView(texttime);
            layout.addView(textView);
            layout.addView(webview);

            webview.setTag(Integer.toString(position));
            position++;

            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 600);

            pd.dismiss();

        }

    }


    private void uploadFile() {
        String type="message";

        //if there is a file to upload
        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference riversRef;
            if(Upload==0)
            {
                 type="image";


                riversRef =storageReference.child("SharedCostImagesApp/"+filename);

            }
            else
                {
                     type="video";

                    riversRef =storageReference.child("SharedCostVideosApp/"+filename);

                }

            String finalType = type;
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Download file From Firebase Storage
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadPhotoUrl) {
                                    //Now play with downloadPhotoUrl
                                    //Store data into Firebase Realtime Database

                                    generatedFilePath = downloadPhotoUrl.toString();
                                    System.out.println("## Stored path is " + generatedFilePath);

                                    String COL_2 = "message";
                                    String COL_3 = "MobileNumber";
                                    String COL_4 = "Name";
                                    String COL_5 = "image";
                                    String COL_6 = "type";
                                    reference1 = new Firebase("https://divvipay-d08bf.firebaseio.com/GroupDetail/" + gid);
                                    Map<String, Object> map1 = new HashMap<String, Object>();
                                    map1.put(COL_2, message.getText().toString());
                                    map1.put(COL_3, logined_user_phone_no);
                                    map1.put(COL_4, UserName);
                                    map1.put(COL_5, generatedFilePath);
                                    map1.put(COL_6, finalType);

                                    Map<String, Object> map2 = new HashMap<String, Object>();
                                    map2.put("lastmessage", message.getText().toString());
                                    reference1.child("GroupMessage").push().setValue(map1);
                                    reference1.child("LastMessage").child("lastmeesgae").setValue(map2);
                                    message.setText("");
                                    scrollView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                            Selected_Image.setVisibility(View.GONE);
                                            simpleVideoView.setVisibility(View.GONE);
                                            Close.setVisibility(View.GONE);

                                        }
                                    }, 600);

                                    progressDialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");


                        }
                    });



        }
        //if there is not any file
        else {
            String COL_2 = "message";
            String COL_3 = "MobileNumber";
            String COL_4 = "Name";
            String COL_5 = "image";
            String COL_6 = "type";
            reference1 = new Firebase("https://divvipay-d08bf.firebaseio.com/GroupDetail/" + gid);
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put(COL_2, message.getText().toString());
            map1.put(COL_3, logined_user_phone_no);
            map1.put(COL_4, UserName);
            map1.put(COL_5, generatedFilePath);
            map1.put(COL_6,type);
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("lastmessage", message.getText().toString());
            reference1.child("GroupMessage").push().setValue(map1);
            reference1.child("LastMessage").setValue(map2);
            message.setText("");
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 600);

            //you can display an error toast
        }
    }


    }
