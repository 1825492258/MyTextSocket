# MyTextSocket
Android Socket的通信

## 1.建立socket连接
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
                // 开启定时器，定时发送心跳包，保持长连接  这里按理说应该是在Socket连接成功后
                mPingThread = new PingThread();
                isConnect = true;
                sendTime = System.currentTimeMillis();
                mPingThread.start();
            }

        }).start();
    }

## 2.开启读取线程
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

## 3.开启Ping包线程
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
                        Log.i(TAG, type + "----发送心跳包------");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                SystemClock.sleep(SOCKET_ACTIVE_TIME * 1000); //
            }
        }
    }