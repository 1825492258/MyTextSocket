package item.com.sokcet.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NettyUtils {
    private static long sequenceId;// 以后用于token
    private static final int requestid = 0;// 请求ID
    private static final int version = 1;
    private static final String terminal = "1001";  // 安卓:1001,苹果:1002,WEB:1003,PC:1004


    public static byte[] buildRequest(int cmd, byte[] body) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            int length = body == null ? 26 : (26 + body.length);
            dos.writeInt(length);
//            if ((!MyApplication.getApp().isLogin() || isSwitch) && cmd != ISocket.CMD.HEART_BEAT)
//                sequenceId = 0;
            dos.writeLong(sequenceId);
            dos.writeShort(cmd);
            dos.writeInt(version);
            byte[] terminalBytes = terminal.getBytes();
            dos.write(terminalBytes);
            dos.writeInt(requestid);
            if (body != null) dos.write(body);
            return bos.toByteArray();
        } catch (IOException ex) {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
