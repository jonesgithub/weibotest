package com.fangjie.weibo.bean;
import android.graphics.Bitmap;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * describe 授权登录用户的信息
 */

public class LoginUser
{
	//微博用户的uid(long)
	private Long id;
	//微博用户的uid(String)
	private String userId;
	//微博用户 用户名
	private String userName;
	//授权微博用户的access_token
	private String token;
	//微博用户的 icon
	private Bitmap  userIcon;

	public static final String TB_NAME="UserInfo";
	public static final String ID="_id";
	public static final String USER_ID="userId";
	public static final String USER_NAME="userName";
	public static final String TOKEN="token";
	public static final String IS_DEFAULT="isDefault";
	public static final String USER_ICON="userIcon";
	
	public LoginUser(String userId,String userName,String token)
	{
		this.userId = userId;
		this.userName = userName;
		this.token = token;
	}	
	
	public LoginUser() {
		super();
		this.userName="";
	}

	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getUserName()
	{
		return userName;
	}
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	public String getToken()
	{
		return token;
	}
	public void setToken(String token)
	{
		this.token = token;
	}
	public Bitmap getUserIcon()
	{
		return userIcon;
	}
	public void setUserIcon(Bitmap userIcon)
	{
		this.userIcon = userIcon;
	}	
}
