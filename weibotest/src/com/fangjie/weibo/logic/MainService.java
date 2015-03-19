package com.fangjie.weibo.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.fangjie.weibo.bean.*;
import com.fangjie.weibo.ui.IWeiboAcitivity;
import com.fangjie.weibo.util.DBLoginUserUtil;
import com.fangjie.weibo.util.LoginUserUtil;
import com.fangjie.weibo.util.LoginSessionUtil;
import com.fangjie.weibo.util.WeiboUtil;
import com.weibo.sdk.android.Oauth2AccessToken;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * @describe 后台处理UI线程的创建得任务队列
 */
public class MainService extends Service implements Runnable{
	private static Queue<Task> tasks=new LinkedList<Task>();
	private static ArrayList<Activity> appActivities=new ArrayList<Activity>();
	private boolean isRun;
	private Handler handler;
	
	@SuppressLint("HandlerLeak")
	public void onCreate() {		
		super.onCreate();
		
		//任务队列的 任务处理完成的消息
		handler =new  Handler(){
			public void handleMessage(Message msg)
			{
				IWeiboAcitivity activity=null;
				switch(msg.what)
				{
					case Task.WEIBO_LOGIN:
						activity=(IWeiboAcitivity)getActivityByName("LoginActivity");
						activity.refresh(Task.WEIBO_LOGIN,msg.obj);
						break;
					case Task.GET_USERINFO_BY_TOKEN:
						activity=(IWeiboAcitivity)getActivityByName("AuthActivity");
						activity.refresh(Task.GET_USERINFO_BY_TOKEN,msg.obj);
						break;		
					case Task.GET_USERINFO_IN_LOGIN:
						activity=(IWeiboAcitivity)getActivityByName("LoginActivity");
						activity.refresh(Task.GET_USERINFO_IN_LOGIN,msg.obj);
						break;		
					case Task.GET_WEIBOS:
						activity=(IWeiboAcitivity)getActivityByName("HomeActivity");
						activity.refresh(Task.GET_WEIBOS,msg.obj);
						break;
					case Task.LOADMORE:
						activity=(IWeiboAcitivity)getActivityByName("HomeActivity");
						activity.refresh(Task.LOADMORE,msg.obj);
						break;
					case Task.UPDATE_WEIBO:
						activity=(IWeiboAcitivity)getActivityByName("UpdateWeibo");
						activity.refresh(Task.UPDATE_WEIBO,msg.obj);
						break;
					case Task.GUANZHU:
						activity=(IWeiboAcitivity)getActivityByName("MoreActivity");
						activity.refresh(Task.GUANZHU,msg.obj);
					default:
						break;
				}
			}
		};
		
		//开启第二线程，做任务队列的循环操作
		isRun=true;		
		Thread thread =new Thread(this);
		thread.start();
	}

	//多线程 就是不断的从Task队列中取 Task，并做处理
	public void run() {
		while(isRun){
			if(!tasks.isEmpty()){
				doTask(tasks.poll());
			}
		}
	}
	
	//UI线程新开任务的接口
	public static void newTask(Task task)
	{
		tasks.add(task);
	}
	
	//UI新开任务时，需要传入Activity实例，为了refresh操作
	public static void addActivty(Activity activity)
	{
		appActivities.add(activity);
	}
	
	//任务完成后，应该remove对应的Activity实例，防止下次同一Activity的不同实例refresh混淆
	public static void reMoveActivty(Activity activity)
	{
		appActivities.remove(activity);
	}
	
	//通过name获取新开任务时传递过来的Activity实例
	public Activity getActivityByName(String name)
	{
		if(!appActivities.isEmpty()){
			for(Activity activity:appActivities){
				if(activity.getClass().getName().indexOf(name)>0){
					return activity;
				}
			}
		}
		return null;
	}

	//处理Tasks堆栈中的task
	public void doTask(Task task)
	{
		Message msg=handler.obtainMessage();
		msg.what=task.getTaskID();
		
		switch(task.getTaskID())
		{
			//登录操作
			case Task.WEIBO_LOGIN:
				LoginUser LoginUser=(LoginUser)(task.getParams().get("LoginUser"));
				Context context=getActivityByName("LoginActivity");
				LoginSessionUtil.SaveLoginUser(context, LoginUser);
				msg.obj="成功";
				break;
				
			//通过Token从微博API获取用户信息，并保存到数据库操作	
			case Task.GET_USERINFO_BY_TOKEN:
			{
				    Oauth2AccessToken access_token=(Oauth2AccessToken)task.getParams().get("token");
					
				    //1.先通过Token获取Uid
				    
					//请求获取uid
		        	String uid="";
		        	LoginUserUtil.reqUID(access_token);
		        	//获取uid
		        	do{
		        		uid=LoginUserUtil.getUID();
		        	}while(uid.equals(""));
		        	
		        	//2.通过uid，token获取UserInfo
		        	
		        	//请求获取用户信息
		        	long _uid=Long.parseLong(uid);
		        	LoginUser user=new LoginUser();
		        	LoginUserUtil.reqUserInfo(access_token, _uid);
		        	//获取UserInfo
		        	do{
		        		user=LoginUserUtil.getUserInfo();
		        	}while(user.getUserName().equals(""));		        	
		        	user.setUserId(uid);

		        	//3.把UserInfo的数据保存到数据库
		        	//创建数据库
		        	DBLoginUserUtil db=new DBLoginUserUtil(getActivityByName("AuthActivity"));
		        	//如果该数据不存在数据库中
		        	if(db.getUserInfoByUserId(uid)==null){
		        		db.insertUserInfo(user);    
		        	}      
		        	msg.obj="成功";
				}
				break;
				
			//登录界面获取用户信息显示操作
			case Task.GET_USERINFO_IN_LOGIN:
			{
	        	DBLoginUserUtil db=new DBLoginUserUtil(getActivityByName("LoginActivity"));
	        	List<LoginUser> users=db.getAllUserInfo();
	        	msg.obj=users;
	        	break;
			}
			//刷新微博
			case Task.GET_WEIBOS:
			{
				String token=(String)task.getParams().get("token");
				WeiboUtil weiboutil=new WeiboUtil();
				List<Weibo> weibos=weiboutil.getWeiboList(token,0);
				msg.obj=weibos;
				break;
			}
			//加载更多
			case Task.LOADMORE:
			{
				String token=(String)task.getParams().get("token");
				long max_id=(Long) task.getParams().get("max_id");
				WeiboUtil weiboutil=new WeiboUtil();
				List<Weibo> weibos=weiboutil.getWeiboList(token,max_id);
				msg.obj=weibos;
				break;
			}
			//发微博
			case Task.UPDATE_WEIBO:
			{
				String token=(String)task.getParams().get("token");
				String text=(String)task.getParams().get("text");
				WeiboUtil weiboutil=new WeiboUtil();
				if(weiboutil.update(token, text)==0){
					msg.obj=0;	
				}
				else{
					msg.obj=1;
				}
				break;
			}
			//关注@方杰_Jay
			case Task.GUANZHU:
			{
				String token=(String)task.getParams().get("token");
				WeiboUtil weiboutil=new WeiboUtil();
				switch(weiboutil.guanzhuMe(token))
				{
					case 0:msg.obj=0;
						break;
					case 1:msg.obj=1;
						break;
					case 2:msg.obj=2;
						break;
					case 3:msg.obj=3;
						break;
				}
				break;				
			}
			default :
				break;
		}
		handler.sendMessage(msg);
	}

	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}