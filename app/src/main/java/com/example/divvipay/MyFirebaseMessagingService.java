package com.divvipay.app;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static com.divvipay.app.NotificationUtils.ANDROID_CHANNEL_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String body;
    NotificationUtils mNotificationUtils;
    Notification.Builder nb;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        mNotificationUtils = new NotificationUtils(this);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Check", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.

        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.e("JSON OBJECT", object.toString());
        try {
            body = object.getString("body");

            String title = "New Message Recieved";
            String text = "Message in  " + body.toString() + " Group";

            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(text)) {
                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
//you can use your launcher Activity insted of SplashActivity, But if the Activity you used here is not launcher Activty than its not work when App is in background.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//Add Any key-value to pass extras to intent
                intent.putExtra("pushnotification", "yes");

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                nb = mNotificationUtils.getAndroidChannelNotification(title, text,pendingIntent);

                mNotificationUtils.getManager().notify(101, nb.build());
            }
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d("Check", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}