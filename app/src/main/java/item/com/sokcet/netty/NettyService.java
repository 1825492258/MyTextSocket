package item.com.sokcet.netty;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import item.com.sokcet.R;
import item.com.sokcet.utils.GlobalConstant;

public class NettyService extends Service {

    private HashMap<Integer, NettyClient> hashMap;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setForeground();
        hashMap = new HashMap<Integer, NettyClient>();
        hashMap.put(GlobalConstant.CODE_BB_TRADE, NettyClient.init(GlobalConstant.CODE_BB_TRADE).setSocketIp("47.74.226.97", 28901));
        hashMap.put(GlobalConstant.CODE_MARKET, NettyClient.init(GlobalConstant.CODE_MARKET).setSocketIp("47.74.158.104", 28902));
        hashMap.put(GlobalConstant.CODE_KLINE, NettyClient.init(GlobalConstant.CODE_KLINE).setSocketIp("47.74.158.104", 28903));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // //设置START_STICKY为了使服务被意外杀死后可以重启
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetMessage(SocketMessage message) {
        if (hashMap != null && hashMap.get(message.getType()) != null) {
            NettyClient client = hashMap.get(message.getType());
            if (client != null) {
                client.sendSocketMessage(message);
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hashMap != null && hashMap.size() > 0) {
            for (NettyClient client : hashMap.values()) {
                client.onDestroy(); // socket断开重连
            }
        }
        stopForeground(true);
        EventBus.getDefault().unregister(this);
    }

    private final int PID = android.os.Process.myPid();
    private ServiceConnection mConnection;

    /**
     * 设置为前台Service 使进程被杀死的概率大大降低
     */
    private void setForeground() {
        if (Build.VERSION.SDK_INT < 18) {
            this.startForeground(PID, getNotification());
            return;
        }
        if (null == mConnection) {
            mConnection = new CoverServiceConnection();
        }
        this.bindService(new Intent(this, HelpService.class),
                mConnection, Service.BIND_AUTO_CREATE);
    }

    private Notification getNotification() {
        // 定义一个Notification
        Notification notification;
        Intent notificationIntent = new Intent(this, NettyService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher) // 设置小图片
                .setContentTitle("你好")
                .setContentText("这个是内容")
                .setWhen(System.currentTimeMillis())
                .setOngoing(true); // 设置通知内容
        notification = mBuilder.build();
        return notification;
    }

    private class CoverServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Service helpService = ((HelpService.LocalBinder) binder).getService();
            NettyService.this.startForeground(PID, getNotification());
            helpService.startForeground(PID, getNotification());
            helpService.stopForeground(true);
            NettyService.this.unbindService(mConnection);
            mConnection = null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
}
