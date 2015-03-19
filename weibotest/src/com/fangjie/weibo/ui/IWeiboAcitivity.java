package com.fangjie.weibo.ui;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * @describe 所有微博界面的接口
 */
public interface IWeiboAcitivity {

	//初始化界面操作
	public void init();
	
	//任务完成后的UI界面更新操作
	public void refresh(int taskID,Object...objects);
	
}
