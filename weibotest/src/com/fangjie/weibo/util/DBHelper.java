package com.fangjie.weibo.util;

import com.fangjie.weibo.bean.LoginUser;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * @describe 本地数据库操作帮助类
 */

public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context)
    {
        super(context,"weibo",null,1);//数据库名称
    }
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="CREATE TABLE IF NOT EXISTS  "+LoginUser.TB_NAME
        		+ "( _id INTEGER PRIMARY KEY,userId TEXT, userName TEXT, token TEXT,isDefault TEXT,userIcon BLOB)";
        sqLiteDatabase.execSQL(sql);      //表明
    }  
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
