package com.topnews.fragment;

import java.util.ArrayList;

import org.w3c.dom.Text;

import com.topnews.CityListActivity;
import com.topnews.DetailsActivity;
import com.topnews.R;
import com.topnews.adapter.NewsAdapter;
import com.topnews.bean.NewsEntity;
import com.topnews.tool.Constants;
import com.topnews.view.HeadListView;
import com.topnews.view.TopToastView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsFragment extends Fragment {
	private final static String TAG = "NewsFragment";
	Activity activity;
	ArrayList<NewsEntity> newsList = new ArrayList<NewsEntity>();
	HeadListView mListView;
	NewsAdapter mAdapter;
	String text;
	int channel_id;
	ImageView detail_loading;
	public final static int SET_NEWSLIST = 0;
	//Toast提示框
	private RelativeLayout notify_view;
	private TextView notify_view_text;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		获取传递过来的参数，并从中取值text id
//		与MainActivity.java 中的
//		Bundle data = new Bundle();
//		data.putString("text", userChannelList.get(i).getName());
//		data.putInt("id", userChannelList.get(i).getId());
//		newfragment.setArguments(data); 对应
		Bundle args = getArguments();
		text = args != null ? args.getString("text") : "";
		channel_id = args != null ? args.getInt("id", 0) : 0;
//		获取新闻列表
		initData();
		super.onCreate(savedInstanceState);
	}
//onAttach() 当Fragment与Activity关联时调用。
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		super.onAttach(activity);
	}
	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			//fragment可见，且新闻列表newsList存在时，handler发送信息SET_NEWSLIST（设置新闻列表）
//			若新闻列表不存在，则通过子线程来完成这件事情，新建线程并休眠2ms，再次发送handler信息
			if(newsList !=null && newsList.size() !=0){
//	handler.obtainMessage(int what) Returns a new Message from the global message pool.即从一个可回收对象池中获取Message对象
//	sendToTarget()	： Sends this Message to the Handler specified by getTarget().

				handler.obtainMessage(SET_NEWSLIST).sendToTarget();
			}else{
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.obtainMessage(SET_NEWSLIST).sendToTarget();
					}
				}).start();
			}
		}else{
			//fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
//	创建Fragment视图  Fragment展现样式
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
		mListView = (HeadListView) view.findViewById(R.id.mListView);
		TextView item_textview = (TextView)view.findViewById(R.id.item_textview);
//		新闻列表加载前的缓冲图片
		detail_loading = (ImageView)view.findViewById(R.id.detail_loading);
		//Toast提示框
		notify_view = (RelativeLayout)view.findViewById(R.id.notify_view);
		notify_view_text = (TextView)view.findViewById(R.id.notify_view_text);
		item_textview.setText(text);
		return view;
	}

	private void initData() {
//		Constants 新闻列表
		newsList = Constants.getNewsList();
	}

//	在当前主线程（UI线程）中创建handler，处理子线程传来的数据。
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SET_NEWSLIST:
//				detail_loading  listView列表未导入时的缓冲页面。此处设置为不显示
				detail_loading.setVisibility(View.GONE);
//				设置listView的适配器，且判断当前频道是否是地区频道
				if(mAdapter == null){
					mAdapter = new NewsAdapter(activity, newsList);
					//判断是不是城市的频道
					// channel_id是通过Fragment创建时，带进的参数是频道id。若频道id是带城市频道的id 则加载城市选择模块
					if(channel_id == Constants.CHANNEL_CITY){
						//是城市频道 布局城市组件功能
						mAdapter.setCityChannel(true);
						initCityChannel();
					}
				}
				mListView.setAdapter(mAdapter);
				mListView.setOnScrollListener(mAdapter);
//				setPinnedHeaderView 自定义方法，
//				作用：在页面滚动时，将headerView固定在当前section的顶部。当前section的最后一个元素滚出屏幕时，headerView也跟着Push_up 滚出屏幕
				mListView.setPinnedHeaderView(LayoutInflater.from(activity).inflate(R.layout.list_item_section, mListView, false));
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(activity, DetailsActivity.class);
//						在设置城市的频道，listView的position=0 的位置为城市，具体内容是position=1的位置开始的
						if(channel_id == Constants.CHANNEL_CITY){
							if(position != 0){
//								intent 传递参数
								intent.putExtra("news", mAdapter.getItem(position - 1));
								startActivity(intent);
								activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
							}
						}else{
//							//若用户点击的是城市按钮，
							intent.putExtra("news", mAdapter.getItem(position));
							startActivity(intent);
							activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
						}
					}
				});
//				此频道设置更新提示  在最上方 长方块蓝色背景+提示字
				if(channel_id == 1){
					initNotify();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	/* 初始化选择城市的header，并将此组件放在listView上方*/
	public void initCityChannel() {
		View headview = LayoutInflater.from(activity).inflate(R.layout.city_category_list_tip, null);
		TextView chose_city_tip = (TextView) headview.findViewById(R.id.chose_city_tip);
//		点击监听  点击后，跳至地区选择页面CityListActivity.java
		chose_city_tip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity, CityListActivity.class);
				startActivity(intent);
			}
		});
//		addHeaderView 在listView上方添加组件，即将城市选择组件放在listView的上方
//		建议setAdapter需要在addHeaderView和addfooterView之后调用
		mListView.addHeaderView(headview);
	}
	
	/* 初始化通知栏目  设置显示时间*/
	private void initNotify() {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				notify_view_text.setText(String.format(getString(R.string.ss_pattern_update), 10));
				notify_view.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						notify_view.setVisibility(View.GONE);
					}
				}, 2000);
			}
		}, 1000);
	}
	/* 摧毁视图，当Fragment视图被移除是调用 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.d("onDestroyView", "channel_id = " + channel_id);
		mAdapter = null;
	}
	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "channel_id = " + channel_id);
	}
}
