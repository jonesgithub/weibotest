package com.fangjie.weibo.ui;

import java.util.HashMap;
import java.util.Map;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.fangjie.weibo.R;
import com.fangjie.weibo.bean.Task;
import com.fangjie.weibo.logic.MainService;
import com.fangjie.weibo.util.AuthUtil;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * @describe 用户授权界面Activity
 */
public class AuthActivity extends Activity implements IWeiboAcitivity{
    private Button btnOAuth;
	private Dialog mDialog;
	private Oauth2AccessToken Access_Token;
	private AuthUtil OAuthUtil;
	private ProgressDialog progressDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);

		OAuthUtil=new AuthUtil(AuthActivity.this);
		Access_Token=OAuthUtil.getAccessToken();

		/**
		 * 采用access_token判断是开始授权还是授权成功回调
		 * 1.开始授权就显示授权提醒的对话框
		 * 2.授权回调就做获取用户信息然后保存数据库的操作
		 */
		
		//授权完成后重新跳转到该Activity
		if(Access_Token!=null){
			
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在获取用户信息，请稍候...");
			progressDialog.show();
			
			//新开任务：通过token获取登录用户信息
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("token", Access_Token);
			Task task=new Task(Task.GET_USERINFO_BY_TOKEN,params); 
			MainService.newTask(task);
			MainService.addActivty(AuthActivity.this);	
			
		}
		//授权开始时加载该Activity
		else
			init();
    }

	//初始化：开始授权就显示授权提醒的对话框
	public void init() {
		View diaView=View.inflate(this, R.layout.dialog, null);
		mDialog=new Dialog(AuthActivity.this,R.style.dialog);
		mDialog.setContentView(diaView);
		mDialog.show();
		btnOAuth=(Button)diaView.findViewById(R.id.btn_auth);
		btnOAuth.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//请求授权
				OAuthUtil.reqAccessToken();
			}
		});
	}
	
	//AuthActivity 任务更新
	public void refresh(int taskID,Object... objects) {
		if(((String)objects[0]).equals("成功")){
			progressDialog.dismiss();
			MainService.reMoveActivty(AuthActivity.this);
			
			//授权成功，跳转到LoginActivity
			Intent intent=new Intent(AuthActivity.this,LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
