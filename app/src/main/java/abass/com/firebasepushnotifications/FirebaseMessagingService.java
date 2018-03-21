package abass.com.firebasepushnotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ahmed on 14-Mar-18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
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
        String Domain = remoteMessage.getData().get("Domain");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.lunch)
                .setContentTitle(MessageTitle)
                .setContentText(MessageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        Intent intent = new Intent(click_action);
        intent.putExtra("message",dataMessage);
        intent.putExtra("from_user_id",dataFrom);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longtitude",longtitude);
        intent.putExtra("Domain",Domain);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        mBuilder.setContentIntent(pendingIntent);


        int mNotification_id = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotification_id,mBuilder.build());
    }

}
