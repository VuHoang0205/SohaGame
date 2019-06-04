package com.s.sdk.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.s.sdk.R;
import com.s.sdk.tracking.STracker;
import com.s.sdk.utils.Alog;

import java.util.Map;

public class FCMMessagingService extends FirebaseMessagingService {
    //private static final String TAG = "mytag";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // (developer): Handle FCMInit messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Alog.e("Message data payloaddd: " + remoteMessage.getData().toString());

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//            // Handle message within 10 seconds
//            handleNow();
//              }

            Map<String, String> data = remoteMessage.getData();
            if (data == null) return;
            String title = data.get("title");
            String body = data.get("message");
            generateNotification(title, body);

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
//            Log.e("mytag", "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Alog.e("Message Notification Body: " + remoteMessage.getNotification().getBody());
            generateNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCMInit
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Alog.e("token_fcm 1: " + token);
        //Log.e(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
//    private void scheduleJob() {
//        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
//        // [END dispatch_job]
//    }

//    /**
//     * Handle time allotted to BroadcastReceivers.
//     */
//    private void handleNow() {
//        Log.e(TAG, "Short lived task is done.");
//    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCMInit InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        FCMRequest.sendRegistrationToServer(this, token);
    }

    /**
     * Create and show a simple notification containing the received FCMInit message.
     *
     * @param messageBody FCMInit message body received.
     */
    private void generateNotification(String title, String messageBody) {
        Intent intent =  getPackageManager().getLaunchIntentForPackage(getPackageName());
        assert intent != null;
        intent.putExtra(STracker.ACTION_OPEN_NOTIFI, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getPackageName();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.s_ic_sgame)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "S", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        //int i = new Random().nextInt(1000);
        notificationManager.notify(0, notificationBuilder.build());
    }

}
