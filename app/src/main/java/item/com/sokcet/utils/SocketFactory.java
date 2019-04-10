package item.com.sokcet.utils;

public class SocketFactory {
    public static final int HEART_BEAT = 11004; // 心跳包
    public static final int ENABLE_SYMBOL = 20009;
    public static final short SUBSCRIBE_THUMB = 22011; // 首页缩略图订阅与取消
    public static final short UN_SUBSCRIBE_THUMB = 22012;

    public static final short SUBSCRIBE_EXCHANGE_TRADE = 21000; // 订阅盘口
    public static final short UNSUBSCRIBE_EXCHANGE_TRADE = 21001; // 取消订阅盘口

}
