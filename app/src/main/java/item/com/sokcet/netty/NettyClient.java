package item.com.sokcet.netty;
import android.os.SystemClock;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import item.com.sokcet.utils.NettyUtils;

import static item.com.sokcet.utils.SocketFactory.HEART_BEAT;

public class NettyClient {

    private final String TAG = "jiejie";
    // Socket的弱引用
    private WeakReference<Socket> mSocket;
    // 读取服务器端发来的消息的线程
    private ReadThread mReadThread;
    // Ping包的线程
    private PingThread mPingThread;
    // socket 是否连接上
    private boolean isConnect = false;
    private String IP = "";
    private int POST = 0;
    private int type;  // 根据不同的socket来判断，可能有多个socket的情况

    private long sendTime = 0; // 消息发出的时间（不管是心跳包还是普通消息，发送完就会跟新时间）
    private static final int SOCKET_ACTIVE_TIME = 10; // 每隔s发送一次心跳包

    private SocketMessage lastMessage; // 最后一次传来的Message

    private NettyClient(int type) {
        this.type = type;
    }

    static NettyClient init(int type) {
        return new NettyClient(type);
    }

    synchronized NettyClient setSocketIp(String ip, int post) {
        IP = ip;
        POST = post;
        connectSocket();
        return this;
    }
   /* private static final int MSG_WHAT_CONNECT = 111; //连接Socket
    private static final int MSG_WHAT_DISCONNECT = 112;//断开Socket
    private static final int MSG_WHAT_SENDMESSAGE = 113;//发送消息

    static class MyHandler extends Handler {
        private final WeakReference<NettyClient> mClient;

        private MyHandler(NettyClient nettyClients) {
            mClient = new WeakReference<>(nettyClients);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NettyClient nettyClients = mClient.get();
            if (nettyClients != null) {
                switch (msg.what) {
                    case MSG_WHAT_CONNECT:
                        if (nettyClients.isServerClose()) {
                            nettyClients.connectSocket();
                        } else {
                            Log.i(nettyClients.TAG, "Socket  已经连接上了");
                        }
                        break;
                    case MSG_WHAT_DISCONNECT:
                        if (nettyClients.isServerClose()) {
                            Log.i(nettyClients.TAG, "Socket  已经断开了");
                        } else {

                        }
                        break;
                    case MSG_WHAT_SENDMESSAGE: // 发送的消息
                        if (nettyClients.isServerClose()) {
                            Log.i(nettyClients.TAG, "Socket断了 请先连接Socket");
                        } else {
                            Log.i("jiejie","--------------");
                            SocketMessage socketMessage = (SocketMessage) msg.obj;
                            try {
                                nettyClients.sendMessage(socketMessage);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
        }
    }*/

    /**
     * 客户端通过Socket与服务端建立连接
     */
    private void connectSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(IP, POST);
                    Log.e(TAG, type + "--Socket连接成功......");
                    if (socket.isConnected()) {
                        mSocket = new WeakReference<Socket>(socket);
                        mReadThread = new ReadThread(socket);
                        mReadThread.start(); // 开启读取线程

                        if(lastMessage !=null) { // 重连之后，发送最后一次的请求
                            sendMessage(lastMessage);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, type + "--Socket连接失败......");
                    e.printStackTrace();
                }
                // 开启定时器，定时发送心跳包，保持长连接  这里按理说应该是在Socket连接成功后，不过写在这里 如果第一次没有连上 会一直尝试建立连接的
                mPingThread = new PingThread();
                isConnect = true;
                sendTime = System.currentTimeMillis();
                mPingThread.start();
            }

        }).start();
    }

    /**
     * 发送消息
     */
     void sendSocketMessage(SocketMessage message) {
        try {
            if (!sendMessage(message)) { // 发送消息如果失败了表示socket连接异常，尝试重新连接一次
                if (mReadThread != null) mReadThread.release();
                releaseLastSocket(mSocket); // 清除socket
                connectSocket(); // 重连socket
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (message != null && message.getCmd() != HEART_BEAT) {
                // 获取最后一次传来的非心跳包信息 保存下
                lastMessage = message;
            }
        }
    }

    private boolean sendMessage(SocketMessage message) {
        if (mSocket == null || mSocket.get() == null) {
            return false;
        }
        Socket socket = mSocket.get();
        try {
            DataOutputStream writer = new DataOutputStream(mSocket.get().getOutputStream());
            if (!socket.isClosed()) {
                byte[] requestBites = NettyUtils.buildRequest(message.getCmd(), message.getBody());
                writer.write(requestBites);
                writer.flush();
                sendTime = System.currentTimeMillis(); // 每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取消息的线程
     */
    class ReadThread extends Thread {

        private WeakReference<Socket> mReadSocket;
        private boolean isStart = true;

        ReadThread(Socket socket) {
            mReadSocket = new WeakReference<>(socket);
        }

        void release() {
            isStart = false;
            releaseLastSocket(mReadSocket);
        }

        @Override
        public void run() {
            super.run();
            Socket socket = mReadSocket.get();
            if (socket != null && !socket.isClosed()) {
                try {
                    DataInputStream reader = new DataInputStream(socket.getInputStream());
                    while (isStart) {
                        startRecTask(reader);
                        Thread.sleep(100); // 每隔0.1秒读取一次，节省点资源
                    }
                } catch (IOException e) {
                    Log.i(TAG, "ReadThread  报错了-----");
                    release();
                    releaseLastSocket(mSocket);
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 心跳包的线程
     */
    class PingThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isConnect) {
                if (System.currentTimeMillis() - sendTime >= SOCKET_ACTIVE_TIME * 1000) {
                    SocketMessage socketMessage = new SocketMessage(HEART_BEAT, null);
                    try {
                        if (!sendMessage(socketMessage)) { //
                            if (mReadThread != null) mReadThread.release();
                            releaseLastSocket(mSocket); // 清除socket
                            connectSocket(); // 重连socket
                        }
                        Log.d(TAG, type + "----发送心跳包------");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                SystemClock.sleep(SOCKET_ACTIVE_TIME * 1000); //
            }
        }
    }

    /**
     * 释放Socket 并关闭
     */
    private void releaseLastSocket(WeakReference<Socket> socket) {
        if (socket != null) {
            Socket so = socket.get();
            try {
                if (so != null && !so.isClosed())
                    so.close();
                socket.clear();

                Log.e(TAG, type + "Socket断开连接。。。。。。");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否断开连接，断开返回true 没有返回false
     */
    private boolean isServerClose() {
        try {
            if (mSocket != null && mSocket.get() != null) {
                mSocket.get().sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
                return false;
            }
        } catch (Exception se) {
            return true;
        }
        return true;
    }

    public void onDestroy() {
        if (mReadThread != null) mReadThread.release();
        releaseLastSocket(mSocket);
        if (mPingThread != null) mPingThread = null;
        isConnect = false;
    }

    /**
     * 开始接受返回的数据
     *
     * @param dis
     */
    private void startRecTask(DataInputStream dis) throws IOException {
        int length = dis.readInt();
        long sequenceId = dis.readLong();
        short cmd = dis.readShort();
        final int responseCode = dis.readInt();
        int requestId = dis.readInt();
        byte[] buffer = new byte[length - 22];
        int nIdx = 0;
        int nReadLen = 0;
        while (nIdx < buffer.length) {
            nReadLen = dis.read(buffer, nIdx, buffer.length - nIdx);
            if (nReadLen > 0) {
                nIdx += nReadLen;
            } else {
                break;
            }
        }
        //if(cmd == HEART_BEAT) return; //处理心跳回执
        String str = new String(buffer);
        Log.i(TAG, "接受的消息 type==" + type + "   cmd=====" + cmd + ",,返回数据" + str);
//            if (sendMsgListener != null) {
//                Log.i("tag", "返回数据指令==" + cmd + ",,,,返回数据==" + str);
//                SocketResponse socketResponse = new SocketResponse(cmd, str, this.channel);
//                sendMsgListener.onMessageResponse(socketResponse);
//            }
        if (responseCode == 200) {
            EventBus.getDefault().post(new SocketResponse(cmd, str, type));
        }
    }
}
