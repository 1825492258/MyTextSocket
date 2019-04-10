package item.com.sokcet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import item.com.sokcet.netty.SocketMessage;
import item.com.sokcet.utils.GlobalConstant;
import item.com.sokcet.utils.NetUtils;
import item.com.sokcet.utils.SocketFactory;

/**
 * 监听网络变化
 */
public class NetChangeReceiver extends BroadcastReceiver {

    private NetChangeEvent event;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int state = NetUtils.getNetWorkState(); // 判断网络是什么类型，0为流量1为wifi
            Log.e("jiejie","网络=====    " + state);
//            if(state >= 0) {
//                EventBus.getDefault().post(new SocketMessage(GlobalConstant.CODE_KLINE, SocketFactory.HEART_BEAT, null));
//                EventBus.getDefault().post(new SocketMessage(GlobalConstant.CODE_MARKET, SocketFactory.HEART_BEAT, null));
//                EventBus.getDefault().post(new SocketMessage(GlobalConstant.CODE_BB_TRADE, SocketFactory.HEART_BEAT, null));
//            }

        }
    }

    /**
     * 网络的监听
     */
    public interface NetChangeEvent {
        void onNetChange(int state);
    }

}
