package yuan.jxau.cn.rocketman;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by 编程只服JAVA on 2016.10.06.
 */
public class RocketService extends Service{
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();//窗体布局参数
    private View mRocketView;
    private WindowManager.LayoutParams params;
    private int screenWidth;
    private int screenHeight;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            params.y = (Integer) msg.obj;

            mWindowManager.updateViewLayout(mRocketView,params);
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //[1]获取窗体对象,并得到屏幕的宽高
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        screenHeight = mWindowManager.getDefaultDisplay().getHeight();

        //开启火箭
        showRocket();
        super.onCreate();
    }

    /**
     * 展示火箭
     */
    private void showRocket() {
        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置火箭不可以获取焦点，可以被触摸，一直保持在屏幕上面
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;//TRANSLUCENT:透明的，半透明的
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("toast");

        //定义土司所在的位置(将吐司指定在左上角)
        params.gravity = Gravity.LEFT+Gravity.TOP;

        //定义吐司所在的布局，并将其转化成view对象，添加至窗体（权限）
        mRocketView = View.inflate(getApplicationContext(), R.layout.rocket_view, null);

        /**
         * 维护iv_rocket中的动画效果
         */
        ImageView iv_rocket = (ImageView) mRocketView.findViewById(R.id.iv_rocket);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_rocket.getBackground();
        animationDrawable.start();

        mWindowManager.addView(mRocketView,params);

        //为mRocketView设置触摸事件
        mRocketView.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN://按下
                        /**
                         * 关于getRawX()和getX()的区别
                         * 1.getRawX()得到的是控件和屏幕之间的距离
                         * 2.getX()得到的是控件和父窗体之间的距离
                         * 3.因此一般情况下用getRawX()
                         */
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        /**
                         * 计算出每一次移动的距离
                         */
                        int disX = moveX-startX;
                        int disY = moveY-startY;

                        params.x = params.x+disX;
                        params.y = params.y+disY;

                        /**
                         * 进行容错处理，防止控件被移动到外面
                         */
                        if (params.x<0) {
                            params.x = 0;
                        }
                        if (params.y<0) {
                            params.y = 0;
                        }
                        if (params.x>screenWidth-mRocketView.getWidth()) {
                            params.x = screenWidth-mRocketView.getWidth();
                        }
                        if (params.y>screenHeight-mRocketView.getHeight()-22) {
                            params.y = screenHeight-mRocketView.getHeight()-22;
                        }

                        /**
                         * 告知窗体吐司需要按照手势的移动，去做位置的更新
                         */
                        mWindowManager.updateViewLayout(mRocketView, params);

                        /**
                         * 由于移动是一个连续且多次的过程，因此需要将每一次移动后的新的坐标重新进行赋值
                         */
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP://抬起
                        if (params.x>100 && params.x<200 && params.y>300){
                            //发射火箭
                            sendRocket();
                            //开启产生尾气的activity
                            Intent intent = new Intent(getApplication(),BackGroundActivity.class);
                            //开启火箭后，关闭了唯一的activity对应的任务栈，因此需要告知新开启的activity，开启一个新的任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }
                //true:只响应触摸事件；false：响应点击事件和触摸事件
                return true;
            }
        });
    }

    /**
     * 发射火箭
     */
    private void sendRocket() {
        /**
         * 1.在向上移动的过程中不断减少y轴的大小，直到减少到0为止
         * 2.在主线程中不能睡眠，可能会导致主线程阻塞
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0;i<11;i++){
                    int height = 300-30*i;
                    //睡眠50ms以便于向用户展示相关的动画效果
                    SystemClock.sleep(10);

                    // 1.因为子线程中不可以更新ui，因此到主线程中更新UI
                    Message message = Message.obtain();
                    message.obj = height;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        /**
         * 当服务销毁的时候，移除小火箭
         */
        if (mWindowManager!=null&&mRocketView!=null){
            mWindowManager.removeView(mRocketView);
        }
        super.onDestroy();
    }
}
