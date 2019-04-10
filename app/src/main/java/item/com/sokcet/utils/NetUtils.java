package item.com.sokcet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import item.com.sokcet.MyApplication;

public class NetUtils {
    /**
     * 没有网络连接
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 数据流量
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * wifi
     */
    private static final int NETWORK_WIFI = 1;

    /**
     * 判断网络类型
     *
     * @return 0---流量 1---wifi -1---无网络
     */
    public static int getNetWorkState() {
        // 得到连接管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return NETWORK_WIFI;// wifi
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return NETWORK_MOBILE; // mobile
                }
            } else {
                return NETWORK_NONE;
            }
        }
        return NETWORK_NONE;
    }
}
