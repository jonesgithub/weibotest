package com.fangjie.weibo.util;

import com.fangjie.weibo.bean.LoginUser;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * @describe 类似于Web中的登录session
 */
public class LoginSessionUtil {
	/*
	 * 保存当前登录用户信息到SharePrefrence
	 * @param context
	 * @param user
	 */
	public static void SaveLoginUser(Context context,LoginUser user)
	{
		SharedPreferences sp=context.getSharedPreferences("LOGIN_USER",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putString(LoginUser.TOKEN, user.getToken());
		editor.putString(LoginUser.USER_ID, user.getUserId());
		editor.putString(LoginUser.USER_NAME, user.getUserName());
		editor.commit();
	}
	
	/*
	 * 从SharePrefrence获取当前登录用户信息
	 * @param context
	 * @return LoginUser
	 */
	public static LoginUser getLoginUser(Context context)
	{
		SharedPreferences sp=context.getSharedPreferences("LOGIN_USER",Context.MODE_PRIVATE);
		String userName=sp.getString(LoginUser.USER_NAME, "");
		String userId=sp.getString(LoginUser.USER_ID, "");
		String token=sp.getString(LoginUser.TOKEN, "");
		if("".equals(userName))
			return null;
		else
			return new LoginUser(userId,userName,token);
	}
	
	/*
	 * 退出登录，从SharePrefrence删除当前登录用户信息
	 * @param context
	 */
	public static void removeLoginUser(Context context)
	{
		SharedPreferences sp=context.getSharedPreferences("LOGIN_USER",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.remove(LoginUser.TOKEN);
		editor.remove(LoginUser.USER_ID);
		editor.remove(LoginUser.USER_NAME);	
		editor.commit();
	}
	
}
