package item.com.sokcet.utils;

import java.util.HashMap;

public class GlobalConstant {
    public static final int CODE_BB_TRADE = 0; // 币币交易code
    public static final int CODE_CHAT = 1; // 聊天code
    public static final int CODE_MARKET = 2; // 行情code
    public static final int CODE_KLINE = 3; // k线code
    public static final int CODE_CFD_TRADE = 4; // CFD交易code

    public static final String service = "service"; // 所有的订阅  都加参数service
    public static final String CFD = "CFD"; // CFD
    public static final String SPOT = "SPOT"; // 币币
    public static final String SWAP = "SWAP"; // 有续
    public static final String LowercaseSPOT = "spot"; // 参数传的值

    public static HashMap<String, String> getMAP(String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put(service, type);
        return map;
    }
}
