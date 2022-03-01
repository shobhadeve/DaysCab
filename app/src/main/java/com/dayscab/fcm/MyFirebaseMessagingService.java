package com.dayscab.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.dayscab.R;
import com.dayscab.common.activties.ChatingAct;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.driver.activities.ActiveScheduleDriverAct;
import com.dayscab.driver.activities.DriverHomeAct;
import com.dayscab.driver.activities.UserPoolRequestAct;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MusicManager;
import com.dayscab.utils.SharedPref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationChannel mChannel;
    private NotificationManager notificationManager;
    private MediaPlayer mPlayer;
    String name, requestId, receiverId;
    Intent intent;
    SharedPref sharedPref;
    String title = "", key = "", status = "", noti_type = "", bookindStatus = "", bookType = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "fsfsdfd:" + remoteMessage.getData());

        if (remoteMessage.getData().size() > 0) {

            Map<String, String> data = remoteMessage.getData();

            try {

                JSONObject object = new JSONObject(data.get("message"));

                try {
                    noti_type = object.getString("notifi_type");
                } catch (Exception e) {
                }

                try {
                    bookType = object.getString("booktype");
                } catch (Exception e) {
                }

                try {
                    requestId = object.getString("request_id");
                } catch (Exception e) {
                }

                try {
                    receiverId = object.getString("user_id");
                } catch (Exception e) {
                }

                try {
                    name = object.getString("user_name");
                } catch (Exception e) {
                }

                try {
                    bookindStatus = object.getString("booking_status");
                } catch (Exception e) {
                }

                try {
                    key = object.getString("key");
                    status = object.getString("status");
                } catch (Exception e) {
                }

                Log.e("fasdfsadfsfs", "key = " + key);
                Log.e("fasdfsadfsfs", "bookindStatus = " + bookindStatus);
                Log.e("fasdfsadfsfs", "noti_type = " + noti_type);
                Log.e("fasdfsadfsfs", "status = " + status);

                if (AppConstant.USER.equals(noti_type)) {
                    if ("Cancel".equals(bookindStatus)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "Cancel");
                        sendBroadcast(intent1);
                    } else if ("Cancel".equals(status)) {
                        // title = object.getString("title");
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "Cancel");
                        sendBroadcast(intent1);
                    } else if ("Start".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "Start");
                        sendBroadcast(intent1);
                    } else if ("End".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "End");
                        sendBroadcast(intent1);
                    } else if ("Arrived".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "Arrived");
                        sendBroadcast(intent1);
                    } else if ("Accept".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action_Accept");
                        Log.e("SendData ===== ", object.toString());
                        if (bookType != null && bookType.equals("LATER")) {
                            intent1.putExtra("booktype", "LATER");
                        } else {
                            intent1.putExtra("booktype", "NOW");
                        }
                        intent1.putExtra("request_id", requestId);
                        intent1.putExtra("status", "Accept");
                        sendBroadcast(intent1);
                    } else if ("Finish".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "Arrived");
                        sendBroadcast(intent1);
                    } else if ("driverchange".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("action_change");
                        sendBroadcast(intent1);
                    }
                } else {
                    if ("Pending".equals(status)) {
                        sharedPref = SharedPref.getInstance(this);
                        if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                            ModelLogin modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
                            if (modelLogin.getResult().getMute_music() == null || modelLogin.getResult().getMute_music().equals("")) {
                                MusicManager.getInstance().initalizeMediaPlayer(this, Uri.parse
                                        (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
                                MusicManager.getInstance().startPlaying();
                            } else {
                                if (modelLogin.getResult().getMute_music().equals("True")) {
                                    MusicManager.getInstance().initalizeMediaPlayer(this, Uri.parse
                                            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
                                    MusicManager.getInstance().startPlaying();
                                }
                            }
                        }

                        if (!bookType.equals("POOL")) {
                            // title = object.getString("title");
                            title = "Taxi Booking";
                            key = object.getString("key");
                            Intent intent1 = new Intent("Job_Status_Action_Taxi");
                            Log.e("SendData=====", object.toString());
                            intent1.putExtra("object", object.toString());
                            sendBroadcast(intent1);
                        }

                    } else if ("changed_route".equals(status)) {
                        // title = object.getString("title");
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("job_status");
                        Log.e("SendData ===== ", "Aayaa Route Change");
                        intent1.putExtra("status", "changed_route");
                        intent1.putExtra("lat", object.getString("droplat"));
                        intent1.putExtra("lon", object.getString("droplon"));
                        intent1.putExtra("dropofflocation", object.getString("dropofflocation"));
                        sendBroadcast(intent1);
                    } else if ("Cancel_by_user".equals(status)) {
                        Intent intent1 = new Intent("job_status");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("status", "Cancel");
                        sendBroadcast(intent1);
                    }

                }

                sharedPref = SharedPref.getInstance(this);

                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    if (AppConstant.DRIVER.equals(noti_type)) {
                        displayCustomTaxiNotifyForDriver(status, title, key, object.toString());
                    } else {
                        if (!"driverchange".equals(status))
                            displayCustomTaxiNotifyForUser(status, title, key, object.toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void displayCustomTaxiNotifyForDriver(String status, String title, String msg, String data) {

//        MusicManager.getInstance().initalizeMediaPlayer(this, Uri.parse
//                (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
//        MusicManager.getInstance().startPlaying();

        Log.e("ggddfdfdfd", "Displaying Notify Cancel");
        Log.e("ggddfdfdfd", "status = " + status);
        Log.e("ggddfdfdfd", "Data Status 123= " + data);
        Log.e("ggddfdfdfd", "booktype = " + bookType);
        Log.e("ggddfdfdfd", "Cancel_by_user = " + !"Cancel_by_user".equals(status));

        if ("POOL".equals(bookType)) {
            intent = new Intent(this, UserPoolRequestAct.class);
        } else {
            if (!"Cancel_by_user".equals(status)) {
                if (msg.contains("message")) {
                    intent = new Intent(this, ChatingAct.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("request_id", requestId);
                    intent.putExtra("name", name);
                    intent.putExtra("receiver_id", receiverId);
                } else if ("changed_route".equals(status)) {
                    intent = new Intent(this, ActiveScheduleDriverAct.class);
                } else {
                    intent = new Intent(this, DriverHomeAct.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("type", "dialog");
                    intent.putExtra("data", data);
                    intent.putExtra("object", data);
                }
            }
        }

//      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//      intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent
                (0, PendingIntent.FLAG_UPDATE_CURRENT);
        /*PendingIntent.getActivity(this, 123, intent,PendingIntent.FLAG_UPDATE_CURRENT);*/
        String channelId = "123";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;

        sharedPref = SharedPref.getInstance(this);
        ModelLogin modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        if (modelLogin.getResult().getMute_request_sound() == null ||
                modelLogin.getResult().getMute_request_sound().equals("")) {
            notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setSmallIcon(R.drawable.ic_logo)
                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_logo))
                    .setContentTitle(getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText(msg)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        } else {
            if (modelLogin.getResult().getMute_request_sound().equals("True")) {
                notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setSmallIcon(R.drawable.ic_logo)
                        //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_logo))
                        .setContentTitle(getString(R.string.app_name))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
            } else {
                notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setSmallIcon(R.drawable.ic_logo)
                        //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_logo))
                        .setContentTitle(getString(R.string.app_name))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
            }
        }

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId, "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.enableVibration(true);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    private void displayCustomTaxiNotifyForUser(String status, String title, String msg, String data) {

        if (msg.contains("message")) {
            intent = new Intent(this, ChatingAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("request_id", requestId);
            intent.putExtra("name", name);
            intent.putExtra("receiver_id", receiverId);
        } else {
            intent = new Intent(this, UserHomeAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("type", "dialog");
            intent.putExtra("data", data);
            intent.putExtra("object", data);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "1";

        String message = "";

        if ("Arrived".equals(status)) {
            message = "Driver is Arrived";
        } else if ("Start".equals(status)) {
            message = "Your Trip is started";
        } else if ("End".equals(status)) {
            message = "Trip is Ended";
        } else if ("Finish".equals(status)) {
            message = "Trip is Finish";
        } else {
            message = msg;
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }

}
