package com.fangjie.weibo.ui;
import java.util.Date;
import java.util.List;
import com.fangjie.weibo.R;
import com.fangjie.weibo.bean.Weibo;
import com.fangjie.weibo.util.AsyncImageLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @author Jay
 * @version 1.0
 * @describe 微博List的Adapter
 */
public class WeiboAdapter extends BaseAdapter {
	
	@SuppressWarnings("unused")
	private Context context;
	private List<Weibo> weibos;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    //用于实现微博List中的图片缓存
	private final int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取当前应用程序所分配的最大内存  
    private final int cacheSize = maxMemory / 5;//只分5分之一用来做图片缓存  
    
    private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(  
            cacheSize) {   
    	protected int sizeOf(String key, Bitmap bitmap) {//复写sizeof()方法  
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024; //这里是按多少KB来算  
        }  
    }; 
	
	public WeiboAdapter(Context context,List<Weibo> weibos) {
		this.context=context;
		this.weibos=weibos;
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return weibos.size();
	}

	public Object getItem(int position) {
		return null;
	}
	
	public long getItemId(int position) {
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        Weibo weibo =weibos.get(position);
        System.out.println("getView " + position + " " + convertView ) ;
        //通过View关联自定义Item布局，进行填充  
		 if(convertView == null)
         {
             convertView = mInflater.inflate(R.layout.wb_item, null); 
             holder=new ViewHolder();
             //获取要显示的组件,注意findViewById的调用对象是上面填充了Item的布局的对象View  
             holder.tv_name = (TextView)convertView.findViewById(R.id.txt_wb_item_uname);  
             holder.tv_content = (TextView)convertView.findViewById(R.id.txt_wb_item_content);  
             holder.tv_time =(TextView)convertView.findViewById(R.id.txt_wb_item_time);  
             holder.tv_from =(TextView)convertView.findViewById(R.id.txt_wb_item_from);  
             holder.tv_comment =(TextView)convertView.findViewById(R.id.txt_wb_item_comment);  
             holder.tv_repost =(TextView)convertView.findViewById(R.id.txt_wb_item_redirect);  
             
             holder.zlayout=(LinearLayout)convertView.findViewById(R.id.lyt_wb_item_sublayout);
             holder.tv_zcontent=(TextView)convertView.findViewById(R.id.txt_wb_item_subcontent); 

             holder.iv_userhead=(ImageView)convertView.findViewById(R.id.img_wb_item_head);
             holder.iv_isv=(ImageView)convertView.findViewById(R.id.img_wb_item_V);
             holder.iv_content_pic=(ImageView)convertView.findViewById(R.id.img_wb_item_content_pic);
             holder.iv_zcontent_pic=(ImageView)convertView.findViewById(R.id.img_wb_item_content_subpic);
             
             convertView.setTag(holder);
         }
		 else {
	         holder = (ViewHolder) convertView.getTag();  
		}

       ///组件添加内容
		holder.tv_content.setText(weibo.getContent());
		holder.tv_name.setText(weibo.getUser().getName());
		holder.tv_from.setText("来自:"+Html.fromHtml(weibo.getFrom()));
		holder.tv_repost.setText(weibo.getReposts_count()+"");
		holder.tv_comment.setText(weibo.getComments_count()+"");
		holder.tv_time.setText(dealTime(weibo.getTime()));
        
        loadBitmap(weibo.getUser().getProfile_image_url(), holder.iv_userhead,80,80);  
        
        if(!weibo.getBmiddle_pic().equals(""))
        {
            loadBitmap(weibo.getBmiddle_pic(), holder.iv_content_pic,0,0);    
        	holder.iv_content_pic.setVisibility(View.VISIBLE);
        }
        else
        {
        	holder.iv_content_pic.setVisibility(View.GONE);        	
        }
        
        if(weibo.getUser().isIsv())
        	holder.iv_isv.setVisibility(View.VISIBLE);
        else
        	holder.iv_isv.setVisibility(View.GONE);
        
        if(weibo.getWeibo()!=null)
        {
        	holder.zlayout.setVisibility(View.VISIBLE);
        	holder.tv_zcontent.setText("@"+weibo.getWeibo().getUser().getName()+":"+weibo.getWeibo().getContent());
            if(!weibo.getWeibo().getBmiddle_pic().equals(""))
            {
                loadBitmap(weibo.getWeibo().getBmiddle_pic(), holder.iv_zcontent_pic,0,0);    
            	holder.iv_zcontent_pic.setVisibility(View.VISIBLE);
            }
        }
        else
        	holder.zlayout.setVisibility(View.GONE);
        return convertView;  
	}
	
	static class ViewHolder{
		public TextView tv_name;
		public TextView tv_content;
		public TextView tv_time;
		public TextView tv_from;
		public TextView tv_comment;
		public TextView tv_repost;
		public LinearLayout zlayout;
		public TextView tv_zcontent;
		public ImageView iv_userhead;
		public ImageView iv_isv;
		public ImageView iv_content_pic;
		public ImageView iv_zcontent_pic;
	}
	
	public void addItem(Weibo weibo)
	{
		weibos.add(weibo);
	}
	
	/* 
	 * @param urlStr 所需要加载的图片的url，以String形式传进来，可以把这个url作为缓存图片的key
	 * @param image ImageView 控件
	 */
	private void loadBitmap(String urlStr, ImageView image,int width,int height) {
		//异步图片加载类
		AsyncImageLoader asyncLoader = new AsyncImageLoader(image, mLruCache,width,height);
		//首先从内存缓存中获取图片
		Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);
		//如果缓存中存在这张图片则直接设置给ImageView
		if (bitmap != null) {
			image.setImageBitmap(bitmap);
		} else {
			image.setImageResource(R.drawable.user_head);//否则先设置成默认的图片
			asyncLoader.execute(urlStr);//然后执行异步任务AsycnTask去网上加载图片
		}
	}
	
	/*
	 * 一个工具方法：作用是把系统时间 转换成当前时间的 前*分钟，前*小时
	 */
	@SuppressWarnings("deprecation")
	public String dealTime(String time)
	{
		Date now=new Date();
		long lnow = now.getTime()/1000; 

		long ldate = Date.parse(time)/1000;
		Date date=new Date(ldate);
		
		if((lnow-ldate)<60)
			return (lnow-ldate)+"秒前";
		else if((lnow-ldate)<60*60)
			return ((lnow-ldate)/60)+"分钟前";
		else
			return date.getHours()+":"+date.getMinutes();
	}
}
