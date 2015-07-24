package com.ldwx.swipemenulistviewdemo;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

public class MainActivity extends Activity {

	private SwipeMenuListView mListView;
	private AppAdapter mAdapter;
	
	private List<ApplicationInfo> mAppList = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //获取手机中已安装的应用程序
        mAppList = getPackageManager().getInstalledApplications(0);
        
        mListView = (SwipeMenuListView) findViewById(R.id.list_view);
        mAdapter = new AppAdapter(this);
        
        mListView.setAdapter(mAdapter);
        
        initMenuListView();
        
    }
    
    private void initMenuListView() {
    	//创建一个SwipeMenuCreator供ListView使用
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			
			@Override
			public void create(SwipeMenu menu) {
				//创建一个侧滑菜单
				SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
				//给该侧滑菜单设置背景
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
				//设置宽度
				openItem.setWidth(dp2px(80));
				//设置名称
				openItem.setTitle("打开");
				//字体大小
				openItem.setTitleSize(18);
				//字体颜色
				openItem.setTitleColor(Color.WHITE);
				//加入到侧滑菜单中
				menu.addMenuItem(openItem);
				
				//创建一个侧滑菜单
				SwipeMenuItem delItem = new SwipeMenuItem(getApplicationContext());
				//给该侧滑菜单设置背景
				delItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
				//设置宽度
				delItem.setWidth(dp2px(80));
				//设置图片
				delItem.setIcon(R.drawable.icon_del);
				//加入到侧滑菜单中
				menu.addMenuItem(delItem);
			}
		};
		
		mListView.setMenuCreator(creator);
		
		//侧滑菜单的相应事件
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0://第一个添加的菜单的响应时间(打开)
					open(mAppList.get(position));
					break;
				case 1://第二个添加的菜单的响应时间(删除)
					mAppList.remove(position);
					mAdapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
				return false;
			}
		});
	}
    
    /**
     * 打开对应的App
     * @param item
     */
    public void open(ApplicationInfo item){
    	Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(item.packageName);
		List<ResolveInfo> resolveInfoList = getPackageManager()
				.queryIntentActivities(resolveIntent, 0);
		if (resolveInfoList != null && resolveInfoList.size() > 0) {
			ResolveInfo resolveInfo = resolveInfoList.get(0);
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName componentName = new ComponentName(
					activityPackageName, className);

			intent.setComponent(componentName);
			startActivity(intent);
		}
    }

	/**
     * 当前手机中的应用程序适配器
     * @author god is a girl
     *
     */
    public class AppAdapter extends BaseAdapter {

    	private Context mContext;
    	
    	public AppAdapter(Context context){
    		mContext = context;
    	}
    	
		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
				holder = new ViewHolder();
				
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ApplicationInfo item = mAppList.get(position);
			holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
			holder.tv_title.setText(item.loadLabel(getPackageManager()));
			
			return convertView;
		}
		
		class ViewHolder {
			ImageView iv_icon;
			TextView tv_title;
		}
    	
    }
    
    //将dp转化为px
    private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

}
