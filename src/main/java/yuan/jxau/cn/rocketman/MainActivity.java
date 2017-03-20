package yuan.jxau.cn.rocketman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("哈哈");
        initUI();
    }

    private void initUI() {
        Button bt_start = (Button) findViewById(R.id.bt_start);
        Button bt_stop = (Button) findViewById(R.id.bt_stop);

        /**
         * 为两个按钮添加点击事件
         */
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("我点击了");
                //开启火箭所在的服务
                startService(new Intent(getApplication(),RocketService.class));
                finish();
            }
        });

        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("我关闭了");
                //关闭火箭服务
                stopService(new Intent(getApplication(),RocketService.class));
                finish();
            }
        });
    }
}
