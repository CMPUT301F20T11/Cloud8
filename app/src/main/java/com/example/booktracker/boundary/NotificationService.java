package com.example.booktracker.boundary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.booktracker.R;
import com.example.booktracker.ui.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

/**
 * Used for message handling and to receive notifications in foreground apps
 * Referenced from: https://firebase.google.com/docs/cloud-messaging/android/client
 * 11/16/2020
 * @author Edlee Ducay
 */
public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";
    /**
     * Called when a message is received
     * @param remoteMessage Object representing message received from FCM
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        sendNotification(remoteMessage);
        /*
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "admin_channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        }
        notificationManager.notify(0, notificationBuilder.build());

        */

    }

    /**
     * Called if FCM registration token is updated
     * Called when FCM registration token is initially generated
     * @param token
     */
    @Override
    public void onNewToken(@NonNull String token) {

        sendRegistrationToServer(token);
    }

    /**
     * Handles the token generation and stores token in server
     * @param token
     */
    public void sendRegistrationToServer(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference userDoc = db.collection("users").document(Objects.requireNonNull(user.getEmail()));
        userDoc.update("token", token);

        String email = user.getEmail();
        FirebaseMessaging.getInstance().subscribeToTopic(user.getEmail());

    }

    /**
     * Updates the current user's token
     * TODO implement multiple tokens allowing notification on multiple devices
     */
    public void updateToken() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        sendRegistrationToServer(token);
                    });


        }
    }


    /**
     * Create and show notification
     * @param remoteMessage
     * FCM message body to be received
     */
    /*
    public void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "admin_channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }
    */
    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String,String> response = remoteMessage.getData();
        String title = response.get("title");
        String message = response.get("message");

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,intent,0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"admin_channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(defaultSound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1,notificationBuilder.build());
    }


    /**
     * Set up a notification channel for SDK versions Oreo or higher
     * @param notificationManager
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to device notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("ADMIN_CHANNEL_ID", adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.BLUE);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}
