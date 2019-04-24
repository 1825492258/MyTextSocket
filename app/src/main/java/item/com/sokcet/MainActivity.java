package item.com.sokcet;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.HashMap;

import item.com.sokcet.netty.NettyService;
import item.com.sokcet.receiver.NetChangeReceiver;
import item.com.sokcet.text.SlidingTabActivity;
import item.com.sokcet.text.TextFourActivity;
import item.com.sokcet.utils.SocketFactory;
import item.com.sokcet.netty.SocketMessage;
import item.com.sokcet.utils.GlobalConstant;

/**
 * 这个是我写来测试socket的
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    Button button;

    private TextView textView;
    private EditText editText;
    private TextView textView1;
    private EditText editText1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        textView1 = findViewById(R.id.textView1);
        editText1 = findViewById(R.id.editText1);

        button = findViewById(R.id.btnTwo);
        findViewById(R.id.btnOne).setOnClickListener(this);
        findViewById(R.id.btnTwo).setOnClickListener(this);
        findViewById(R.id.btnThree).setOnClickListener(this);
        findViewById(R.id.btnFour).setOnClickListener(this);
        startService(new Intent(this, NettyService.class)); // 开启服务
        initReceiver();
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                textView.setEnabled(hasFocus);
//            }
//        });

        editText.setOnFocusChangeListener(this);
        editText1.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOne:
                EventBus.getDefault().post(new SocketMessage(GlobalConstant.CODE_KLINE, SocketFactory.ENABLE_SYMBOL, new Gson().toJson(GlobalConstant.getMAP(GlobalConstant.SPOT)).getBytes()));
                break;
            case R.id.btnTwo:
                button.setText("1111111111111111");
                EventBus.getDefault().post(new SocketMessage(GlobalConstant.CODE_KLINE, SocketFactory.SUBSCRIBE_THUMB, new Gson().toJson(GlobalConstant.getMAP(GlobalConstant.SPOT)).getBytes()));
                break;
            case R.id.btnThree:
                SlidingTabActivity.show(this);
                break;
            case R.id.btnFour:
                TextFourActivity.show(this);
                break;
        }
    }

    /**
     * 这里我开始订阅某个币盘口的信息
     */
    private void startTCP(String symbol, long id) {
        String json = new Gson().toJson(setPostTradeData(symbol, id));
        EventBus.getDefault().post(new SocketMessage(GlobalConstant.CODE_MARKET, SocketFactory.SUBSCRIBE_EXCHANGE_TRADE,
                json.getBytes())); // 需要id

    }

    private HashMap<String, String> setPostTradeData(String symbol, long id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("symbol", symbol);
        map.put("service", GlobalConstant.SPOT);
        if (id != 0)
            map.put("uid", id + "");
        return map;
    }

    private NetChangeReceiver mNetReceiver;

    private void initReceiver() {
        // 动态注册广播
        mNetReceiver = new NetChangeReceiver();
        // 创建意图过滤器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new SocketMessage(GlobalConstant.CODE_KLINE, SocketFactory.UN_SUBSCRIBE_THUMB, new Gson().toJson(GlobalConstant.getMAP(GlobalConstant.SPOT)).getBytes()));
        stopService(new Intent(this, NettyService.class));

        // 动态注册，有注册一定要有销毁
        if (mNetReceiver != null) {
            unregisterReceiver(mNetReceiver);
            mNetReceiver = null;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.editText:
                textView.setEnabled(!hasFocus);
                break;
            case R.id.editText1:
                textView1.setEnabled(!hasFocus);
                break;
        }
    }
}
