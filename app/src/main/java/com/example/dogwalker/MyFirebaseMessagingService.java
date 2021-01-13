package com.example.dogwalker;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.dogwalker.owner.OwnerBookingActivity;
import com.example.dogwalker.owner.OwnerBookingRecordActivity;
import com.example.dogwalker.owner.OwnerChatListActivity;
import com.example.dogwalker.owner.OwnerLiveActivity;
import com.example.dogwalker.walker.WalkerChatListActivity;
import com.example.dogwalker.walker.WalkerDogwalkingActivity;
import com.example.dogwalker.walker.WalkerDogwalkingRecordActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

//백그라운드에서 앱의 알림을 수신하는 것 외에 다른 방식으로 메세지를 처리하는 경우 MyFirebaseMessagingService 가 필요함
//예를들어, 포그라운드에서 앱의 알림 수신, 데이터 페이로드 수신, 업스트림 메세지 전송 등..을 수행하려면 이 서비스를 확장해야함
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "FCM 태그";

    //새로운 토큰을 발행했을때 호출되는 메서드
    //토큰 = 등록id
    @Override
    public void onNewToken(@NonNull String token) {
//        super.onNewToken(s);
        Log.d(TAG, "onNewToken : "+ token);
    }

    //새로운 메세지를 받았을때 호출되는 메서드
    //메세지가 도착하면 onMessageReceived() 메서드가 자동으로 호출된다
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "onMessageReceived()");
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // 메세지가 데이터 페이로드를 포함하고 있는지 체크한다
        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG, "Message notification payload: " + remoteMessage.getNotification().getBody());

            //포그라운드 & 백그라운드 둘다 notification 데이터로 나왔음
            //포그라운드 & 백그라운드 둘다 = "notification":{"title":"아아","body":"내가 알람을 보낸다"}
            //포그라운드에서 headup되는 알람을 눌렀을때 -> 내가 원하는 주문관리 액티비티로 감
            //백그라운드에서 뜨는 알람을 눌렀을때 -> 관리자 홈 액티비티로 감
//            String messageTitle = remoteMessage.getNotification().getTitle();
//            String messageBody = remoteMessage.getNotification().getBody();
            //포그라운드 : data & 백그라운드 : notification 데이터로 나옴
            //포그라운드 알람 = "data":{"title":"aaa","body":"bbb"}
            //백그랑운드 알람 = "notification":{"title":"아아","body":"내가 알람을 보낸다"}
            //포그라운드에서 headup되는 알람을 눌렀을때 -> 내가 원하는 주문관리 액티비티로 감
            //백그라운드에서 뜨는 알람을 눌렀을때 -> 관리자 홈 액티비티로 감
            String messageTitle = remoteMessage.getData().get("title");
            String messageBody = remoteMessage.getData().get("body");
            Log.d(TAG, "title : " + messageTitle);
            Log.d(TAG, "body : " + messageBody);

            //노티피케이션을 보낸다
            sendNotification(messageTitle, messageBody);


        }
        //

//        if(remoteMessage.getNotification() != null){
//            Log.d(TAG, "onMessageReceived : "+ remoteMessage.getNotification().getBody());
//
//            String messageBody = remoteMessage.getNotification().getBody();
//            String messageTitle = remoteMessage.getNotification().getTitle();
//
//            Intent intent = new Intent(this, WalkerChatListActivity.class); //TODO: 변경해야할 부분
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
//            String channelId = "Channel ID";
//
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
//                            .setSmallIcon(R.mipmap.ic_launcher)
//                            .setContentTitle(messageTitle)
//                            .setContentText(messageBody)
//                            .setAutoCancel(true)
//                            .setSound(defaultSoundUri)
//                            .setContentIntent(pendingIntent);
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//
//                String channelName = "Channel Name";
//                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//                notificationManager.createNotificationChannel(channel);
//            }
//            notificationManager.notify(0, notificationBuilder.build());
//        }

    }

    private void sendNotification(String messageTitle, String messageBody){

        //[3]번은 알람의 정보만 표시하도록 한거고, 표시된 알람을 눌렀을때 어떤 동작을 하도록 만들고싶을때 intent 를 추가한다
        //PendingIntent는 Intent와 유사하지만, 시스템에서 대기! 하는 역할을 한다
        //그리고 원하는 상황이 만들어졌을때, 시스템에 의해 해석되고 처리된다
        //startActivity 메서드를 호출하면 시스템에서는 즉시 해석하고 처리한다
        //하지만 PendingIntent는 지정된 상황이 될때까지 보관하고 있게 된다
        Intent intent;
        //팬딩인텐트로 알림 클릭시 내가 지정한 액티비티로 보내는건 포그라운드일때
//        if(messageTitle.contentEquals("산책 방송 라이브중")){ //OwnerBookingActivity
//            intent = new Intent(this, WalkerChatListActivity.class);
//        }else{
//            intent = new Intent(this, OwnerChatListActivity.class);
//        }

        if(messageTitle.contentEquals("산책 방송 라이브중")){
            intent = new Intent(this, VlcSeePlayerActivity.class);
            intent.putExtra("liveStreamUrl", "rtmp://15.164.216.186:1935/myapp/walker2");
        }else {
            intent = new Intent(this, OwnerLiveActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Channel ID";

        //[1] NotificationManager 객체를 참조하기
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //[2] NotificationCompat.Builder 객체를 생성하기
        //[3] 생성된 빌더 객체에 정보를 설정하기
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)  //푸시알림 제목
                .setContentText(messageBody)    //푸시알림 내용
                .setAutoCancel(true)            //알람을 클릭했을때 자동으로 알림표시를 삭제
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setPriority(Notification.PRIORITY_HIGH)    //헤드업이 된다??
                .setContentIntent(pendingIntent);   //알람 눌렀을때 실행할 인텐트

        //[2-1] 빌더 객체를 만들때 Build.VERSION.SDK_INT 상수의 값을 비교해서
        //      단말의 OS버전에 따라 다른 코드가 실행되도록 한다
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //오레오 이후 버전에서는 알림 채널이 지정되어야한다
            String channelName = "Channel Name";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        //[4] Builder 객체의 build()를 호출하면 Notification 객체가 생성된다
        //[5] NotificationManager의 notify() 메서드를 호출하면서 이 Notification 객체를 파라미터로 전달하면
        //      알람이 띄워진다.
        notificationManager.notify(0, builder.build());
    }
}
