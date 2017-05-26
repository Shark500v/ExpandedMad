package com.polito.madinblack.expandedmad.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.login.CheckLogIn;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;


    public NotificationUtils(Context mContext) {
        this.mContext = mContext;

    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;


        // notification icon
        final int icon = R.drawable.teamwork;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                        //PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        if (imageUrl != null) {

            if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showSmallNotification(mBuilder, bitmap, title, message, timeStamp, resultPendingIntent, alarmSound);
                    playNotificationSound();
                } else {
                    showSmallNotification(mBuilder, BitmapFactory.decodeResource(mContext.getResources(), icon), title, message, timeStamp, resultPendingIntent, alarmSound);
                    playNotificationSound();
                }
            }
        } else {
            showSmallNotification(mBuilder, BitmapFactory.decodeResource(mContext.getResources(), icon), title, message, timeStamp, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, Bitmap icon, String title, String message,
                                       String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);


        Notification notification = mBuilder.setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(icon)
                .setContentText(message)
                .setLights(Color.GREEN, 3000, 3000)
                .build();

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (preference.getBoolean("pref_key_notifications_vibrate", false)){
            notification = mBuilder.setVibrate(new long[] { 300, 300, 300, 300 })
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(++Config.NOTIFICATION_ID, notification);

       /* // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.customnotification);

        // Set Notification Title
        String strtitle = title;
        // Set Notification Text
        String strtext = message;

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(mContext, GroupListActivity.class);
        // Send data to NotificationView Class
        intent.putExtra("title", strtitle);
        intent.putExtra("text", strtext);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                // Set Icon
                .setSmallIcon(R.drawable.ic_wallet)
                // Set Ticker Message
                .setTicker("ticker")
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setContent(remoteViews);

        // Locate and set the Image into customnotificationtext.xml ImageViews
        remoteViews.setImageViewResource(R.id.imagenotileft,R.drawable.teamwork);

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.title,strtitle);
        remoteViews.setTextViewText(R.id.text,strtext);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());*/
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mContext);
        String strRingtonePreference = preference.getString("pref_key_notifications_ringtone", "DEFAULT_SOUND");
        try {

            if(strRingtonePreference.compareTo("") != 0){
                Uri alarmSound = Uri.parse(strRingtonePreference);
                Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
                r.play();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        if(timeStamp == null)
            return 0;
        try {
            return Long.parseLong(timeStamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void saveTokenOnDb(String token, String phone){
        FirebaseDatabase.getInstance().getReference().child("tokens/"+ phone).setValue(token);
    }

    public String getContent(Map<String, String> data){
        String type = data.get("type");
        int typeInt = Integer.parseInt(type);

        String content;
        if(typeInt == 0){
            //message
            content = data.get("madeBy") + " : "+data.get("message")+" ";
        }else{
            //expense
            content = data.get("madeBy") + " "+ mContext.getString(R.string.history_expense) + " "
                    + new DecimalFormat("#0.00").format(Double.parseDouble(data.get("message"))) + " "
                    + Currency.getSymbol(Currency.CurrencyISO.valueOf(data.get("currencyIso")));
        }
        return content;
    }

    public Intent getIntent(int type, Context context, String message, String groupId, String groupName){
        Intent resultIntent;
        if(type == 0){
            //message
            resultIntent = new Intent(context, CheckLogIn.class);
            resultIntent.putExtra("request", 2);
        }else{
            //expense
            resultIntent = new Intent(context, CheckLogIn.class);
            resultIntent.putExtra("request", 1);
        }
        resultIntent.putExtra("groupIndex", groupId);
        resultIntent.putExtra("groupName", groupName);
        return resultIntent;
    }

    public String getCurrentActivity(){
        ActivityManager am = (ActivityManager) mContext .getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        Log.d(TAG, "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName()+"   Package Name :  "+componentInfo.getPackageName());
        return taskInfo.get(0).topActivity.getClassName();
    }

    public boolean isNotificationEnabled(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        if(pref.getBoolean("pref_key_notifications_able", true))
            return true;
        else
            return false;
    }

    public BroadcastReceiver getBroadcastReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    SharedPreferences pref = mContext.getSharedPreferences(Config.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);

                    Log.e(TAG, "Firebase reg id: " + regId);

                    if (!TextUtils.isEmpty(regId) &&  MyApplication.getUserPhoneNumber()!= null)
                        saveTokenOnDb(regId, MyApplication.getUserPhoneNumber());

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    //per ora non faccio niente
                    /*String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();*/

                }
            }
        };
    }
}
