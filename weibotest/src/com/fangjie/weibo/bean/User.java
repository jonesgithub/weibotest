package com.fangjie.weibo.bean;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * describe 微博用户信息，对应微博SDK中的user表
 */
public class User {
	//微博SDK中的uid
	public long uid;
	//微博SDK中的username
	public String name;
	//是否为V认证用户，SDK不提供蓝V和黄V用户的
	public boolean isv;
	//微博用户头像的 urk
	public String profile_image_url;
	
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isIsv() {
		return isv;
	}
	public void setIsv(boolean isv) {
		this.isv = isv;
	}
	public String getProfile_image_url() {
		return profile_image_url;
	}
	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}
	
}
