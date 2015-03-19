package com.fangjie.weibo.ui;


import android.os.Bundle;
import com.fangjie.weibo.R;
import com.fangjie.weibo.logic.MainService;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * @describe 欢迎界面Activity，包含一个ImageView的alpha动画效果
 */
public class WelcomeActivity extends Activity {
	private ImageView weibo_logo;	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        //设置全屏、无标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);
        
        //开启任务队列服务
		Intent intent=new Intent(WelcomeActivity.this,MainService.class);
		startService(intent);
		
        weibo_logo=(ImageView)findViewById(R.id.weibo_logo);
        
        //为ImageView设置alpha动画效果，并设置监听器监听动画结束时跳转
        AlphaAnimation animation=new AlphaAnimation(0.1f,1.0f);
        animation.setDuration(3000);
        animation.setAnimationListener(new AnimationListener(){
        	//动画结束操作
			public void onAnimationEnd(Animation arg0) {
				Intent intent =new Intent(WelcomeActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
			}
			public void onAnimationRepeat(Animation arg0) {
				
			}
			public void onAnimationStart(Animation arg0) {
				
			}
        	
        });
        weibo_logo.setAnimation(animation);
    }
}
