package item.com.sokcet.netty;


/**
 * 接收数据的实体类
 */

public class SocketResponse {

    private short cmd; // 传的指令
    private String response; // 返回的参数
    private int type;

    public SocketResponse(short cmd, String response, int channel) {
        this.cmd = cmd;
        this.response = response;
        this.type = channel;
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

    public void setCmd(short cmd) {
        this.cmd = cmd;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
