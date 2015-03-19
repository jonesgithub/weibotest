package com.fangjie.weibo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fangjie.weibo.R;
import com.fangjie.weibo.bean.Task;
import com.fangjie.weibo.bean.LoginUser;
import com.fangjie.weibo.logic.MainService;
import com.fangjie.weibo.util.LoginUserUtil;
import com.fangjie.weibo.util.LoginSessionUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * @describe 登录界面Activity
 */
public class LoginActivity extends Activity implements IWeiboAcitivity{
	
    private Button btn_AddAcount,btn_Login;
    private Spinner sp_Username;
    private ImageView iv_HeadIcon;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter adapter;			
    private List<LoginUser> LoginUsers;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        //当前如果有登录用户就直接登录
        if(null!=LoginSessionUtil.getLoginUser(this)){
        	Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        	startActivity(intent);
        	finish();
        }      
		
		//开启任务从本地数据库获取登录授权用户信息
		Task task=new Task(Task.GET_USERINFO_IN_LOGIN,null);
		MainService.newTask(task);
		MainService.addActivty(LoginActivity.this);

    }
	/*
	 * 初始化LoginActivity界面操作
	 * 设置按钮监听、findViewById等...
	 */
	public void init() {
		
        sp_Username=(Spinner)findViewById(R.id.spinner_user_list);
		iv_HeadIcon=(ImageView)findViewById(R.id.image_head);
        btn_AddAcount = (Button) findViewById(R.id.btn_add_account);           
        btn_Login = (Button) findViewById(R.id.btn_login); 
        
        //增加用户，进行授权（按钮监听）
        btn_AddAcount.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        //登录按钮监听
        btn_Login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				LoginUser LoginUser=LoginUsers.get(sp_Username.getSelectedItemPosition());
				//新开任务：登录操作
				Map<String,Object> params=new HashMap<String,Object>();
				params.put("LoginUser", LoginUser);
				Task task =new Task(Task.WEIBO_LOGIN,params);
				MainService.newTask(task);
				MainService.addActivty(LoginActivity.this);
			}
		});
        
        //Spinner选择用户监听，选择不同的用户显示相应的用户头像
        sp_Username.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				iv_HeadIcon.setImageBitmap(LoginUserUtil.scaleImg(LoginUsers.get(position).getUserIcon() , 200, 200));
			}
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	
	//任务队列 更新操作
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void refresh(int taskID,Object... objects) {
		switch(taskID)
		{
			//获取到当前所有授权用户信息 任务更新
			case Task.GET_USERINFO_IN_LOGIN:
				LoginUsers=(List<LoginUser>)objects[0];
				
				//当前有授权的用户
				if(LoginUsers.size()>0)
				{
					//初始化登录界面
					init();
					
					//将得到的授权用户List的username提出来放到Spinner的Adapter中
					List<String> list=new ArrayList<String>();
					for(LoginUser user:LoginUsers){
						list.add(user.getUserName());
					}
					adapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,list);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
					adapter.notifyDataSetChanged();
					sp_Username.setAdapter(adapter);	
					
					iv_HeadIcon.setImageBitmap(LoginUserUtil.scaleImg(LoginUsers.get(0).getUserIcon() , 200, 200));
				}
				//当前没有授权用户时，自动跳转到授权界面
				else
				{
	                Intent intent=new Intent(LoginActivity.this, AuthActivity.class);
	                startActivity(intent);
	                finish();
				}
				
				MainService.reMoveActivty(LoginActivity.this);
				break;
				
			//登录任务完成时
			case Task.WEIBO_LOGIN:	
				MainService.reMoveActivty(LoginActivity.this);
				Intent intent =new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
				break;
		}
	}
}
