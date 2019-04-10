package item.com.sokcet.netty;


/**
 * 发送的实体类
 */

public class SocketMessage {
    private int type; // 对应不同地址的socket
    private int cmd; // 传的指令
    private byte[] body; // 参数

    public SocketMessage(int cmd, byte[] body) {
        this.cmd = cmd;
        this.body = body;
    }

    public SocketMessage(int type, int cmd, byte[] body) {
        this.type = type;
        this.cmd = cmd;
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
