package rankstop.steeringit.com.rankstop.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterAuthImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.FCMRegistrationToken;
import rankstop.steeringit.com.rankstop.data.model.db.UserInfo;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;

public class FCMService extends FirebaseMessagingService implements RSView.StandardService {



    private static final String TAG = "FCM_SERVICE";
    private RSPresenter.fcmPresenter fcmPresenter;


    @Override
    public void onCreate() {
        super.onCreate();
        fcmPresenter = new PresenterAuthImpl(this);
    }


    @Override
    public void onNewToken(String token){
        if (RSSession.getCurrentUser() != null){
            if (RSSession.getCurrentUser().get_id() != null && !RSSession.getCurrentUser().get_id().equalsIgnoreCase("")){

                fcmPresenter.sendRegistrationTokenToServer(token,getApplicationContext());
            }
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }




    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

        super.onMessageReceived(remoteMessage);
        String channelId = getString(R.string.default_notification_channel_id);
        String title;
        String content;

        if ( remoteMessage.getData().size() > 0) {
            Map<String, String> dataObject = remoteMessage.getData();
            title = dataObject.get("title");
            content = dataObject.get("body");

            Intent intent = new Intent(FCMService.this, ContainerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.rs_logo_f)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //  Support Version >= Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                CharSequence channelName = "Message provenant de Firebase";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                mChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                mChannel.enableVibration(true);
                notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationManager.notify(007, builder.build());
        }


    }

    @Override
    public void onSuccess(Object data){
        /*// update localStorage registrationToken
        FCMRegistrationToken fcmRegistrationToken = null ;
        if (!(data instanceof String)) {
            fcmRegistrationToken = new Gson().fromJson(new Gson().toJson(data), FCMRegistrationToken.class);
            UserInfo userInfo = RSSession.getCurrentUserInfo();
            userInfo.getUser().setFcmRegistrationToken(fcmRegistrationToken.getRegistrationToken());
            RSSession.refreshLocalStorage(userInfo);
        }*/

    }

    @Override
    public void onFailure() {
    }
}
