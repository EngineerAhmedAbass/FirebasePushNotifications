package abass.com.firebasepushnotifications.Request;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import abass.com.firebasepushnotifications.R;

/**
 * Created by ahmed on 14-Mar-18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    NotificationManager mNotifyMgr;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String MessageTitle = remoteMessage.getNotification().getTitle();
        String MessageBody = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();
        String dataMessage = remoteMessage.getData().get("message");
        String dataFrom = remoteMessage.getData().get("from_user_id");
        String latitude = remoteMessage.getData().get("latitude");
        String longtitude = remoteMessage.getData().get("longtitude");
        String Domain = remoteMessage.getData().get("domain");
        String RequestID = remoteMessage.getData().get("request_id");
        String type = remoteMessage.getData().get("type");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.lunch)
                .setAutoCancel(true)
                .setContentTitle(MessageTitle)
                .setContentText(MessageBody)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("message", dataMessage);
        intent.putExtra("from_user_id", dataFrom);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longtitude", longtitude);
        intent.putExtra("domain", Domain);
        intent.putExtra("request_id", RequestID);
        intent.putExtra("type", type);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(pendingIntent);

        int mNotification_id = new Random().nextInt(60000);
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotification_id, mBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(getString(R.string.default_notification_channel_id), adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (mNotifyMgr != null) {
            mNotifyMgr.createNotificationChannel(adminChannel);
        }
    }

}
