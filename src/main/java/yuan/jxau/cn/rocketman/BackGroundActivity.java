package yuan.jxau.cn.rocketman;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

/**
 * Created by 编程只服JAVA on 2016.10.07.
 */
public class BackGroundActivity extends Activity{
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        ImageView iv_bottom = (ImageView) findViewById(R.id.iv_bottom);
        ImageView iv_top = (ImageView)findViewById(R.id.iv_top);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);//动画从透明到不透明
        alphaAnimation.setDuration(500);//动画时长

        iv_bottom.startAnimation(alphaAnimation);
        iv_top.startAnimation(alphaAnimation);

        mHandler.sendEmptyMessageDelayed(0,1000);
    }
}
