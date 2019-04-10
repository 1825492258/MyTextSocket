package item.com.sokcet.netty;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * NettyService 的帮助服务
 */
public class HelpService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public HelpService getService() {
            return HelpService.this;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
