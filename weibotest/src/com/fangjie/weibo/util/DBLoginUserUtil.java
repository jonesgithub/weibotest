package com.fangjie.weibo.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.fangjie.weibo.bean.LoginUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * @describe 授权用户的本地数据库操作类(存到数据库、从数据库读取)
 */

public class DBLoginUserUtil {
	private DBHelper dbhelper;
	public DBLoginUserUtil(Context context)
	{
		dbhelper = new DBHelper(context);
	}	
	
	//添加授权用户到本地数据库
	public void insertUserInfo(LoginUser user)
	{
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		user.getUserIcon().compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] usericon=baos.toByteArray();
		
		ContentValues values = new ContentValues(5);
		values.put(LoginUser.USER_ID,user.getUserId());
		values.put(LoginUser.USER_NAME, user.getUserName());
		values.put(LoginUser.TOKEN,user.getToken());
		values.put(LoginUser.USER_ICON,usericon);	
		db.insert(LoginUser.TB_NAME, null, values);//表名称
		db.close(); 
	}
	
	//从本地数据库获取授权用户信息(判断uid的用户信息是否已经存到本地数据库)
	public LoginUser getUserInfoByUserId(String uid)
	{
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor =db.query(LoginUser.TB_NAME, new String[]{LoginUser.ID,LoginUser.IS_DEFAULT,LoginUser.TOKEN,
				LoginUser.USER_ID,LoginUser.USER_NAME,LoginUser.USER_ICON},
				LoginUser.USER_ID +"=?",new String[]{uid}, null, null, null);
		LoginUser userInfo =null;
		if(null != cursor)
		{
			if(cursor.getCount() >0)
			{
				cursor.moveToFirst();
				userInfo = new LoginUser();
				Long id =cursor.getLong(cursor.getColumnIndex(LoginUser.ID));
				String uId = cursor.getString(cursor.getColumnIndex(LoginUser.USER_ID));
				String userName = cursor.getString(cursor.getColumnIndex(LoginUser.USER_NAME));
				String token = cursor.getString(cursor.getColumnIndex(LoginUser.TOKEN));
				byte[] byteIcon = cursor.getBlob(cursor.getColumnIndex(LoginUser.USER_ICON));
				userInfo.setId(id);
				userInfo.setUserId(uId);
				userInfo.setToken(token);
				userInfo.setToken(token);
				userInfo.setUserName(userName);
				if(null !=byteIcon)
				{
					Bitmap userIcon=BitmapFactory.decodeByteArray(byteIcon, 0, byteIcon.length);
					userInfo.setUserIcon(userIcon);
				}
			}
		}
		db.close();
		return userInfo;
	}

	//从本地数据库获取所有授权用户的信息
	public List<LoginUser> getAllUserInfo()
	{
		List<LoginUser> users=new ArrayList<LoginUser>();
		
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor =db.query(LoginUser.TB_NAME, new String[]{LoginUser.ID,LoginUser.IS_DEFAULT,LoginUser.TOKEN,
				LoginUser.USER_ID,LoginUser.USER_NAME,LoginUser.USER_ICON},
				null,null, null, null, null);
		LoginUser userInfo =null;
		
		while(cursor.moveToNext())
		{
				userInfo = new LoginUser();
				Long id =cursor.getLong(cursor.getColumnIndex(LoginUser.ID));
				String uId = cursor.getString(cursor.getColumnIndex(LoginUser.USER_ID));
				String userName = cursor.getString(cursor.getColumnIndex(LoginUser.USER_NAME));
				String token = cursor.getString(cursor.getColumnIndex(LoginUser.TOKEN));
				byte[] byteIcon = cursor.getBlob(cursor.getColumnIndex(LoginUser.USER_ICON));
				
				userInfo.setId(id);
				userInfo.setUserId(uId);
				userInfo.setToken(token);
				userInfo.setUserName(userName);
				
				if(null !=byteIcon)
				{
					Bitmap userIcon=BitmapFactory.decodeByteArray(byteIcon, 0, byteIcon.length);
					Log.i("OUTPUT","dbuserinfo"+userIcon);
					userInfo.setUserIcon(userIcon);
				}
				users.add(userInfo);
			}
		db.close();
		return users;
	}
}
