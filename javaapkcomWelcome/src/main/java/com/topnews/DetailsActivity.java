package com.topnews;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.topnews.base.BaseActivity;
import com.topnews.bean.NewsEntity;
import com.topnews.service.NewsDetailsService;
import com.topnews.tool.BaseTools;
import com.topnews.tool.DataTools;
import com.topnews.tool.DateTools;

import org.apache.http.client.methods.HttpGet;


@SuppressLint("JavascriptInterface")
public class DetailsActivity extends BaseActivity {
	private TextView title;
	private ProgressBar progressBar;
	private FrameLayout customview_layout;
	private String news_url;
	private String news_title;
	private String news_source;
	private String news_date;
	private NewsEntity news;
	private TextView action_comment_count;
	WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		setNeedBackGesture(true);//设置需要手势监听
		getData();
		initView();
		initWebView();
	}
	/* 获取传递过来的数据 */
	private void getData() {
//		getIntent() 获取intent传来的数据
		news = (NewsEntity) getIntent().getSerializableExtra("news");
		news_url = news.getSource_url();
		news_title = news.getTitle();
		news_source = news.getSource();
		news_date = DateTools.getNewsDetailsDate(String.valueOf(news.getPublishTime()));
	}

	private void initWebView() {
		webView = (WebView)findViewById(R.id.wb_details);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		TextUtils.isEmpty(text)  等同于 if(text == null || text.length() == 0)
		if (!TextUtils.isEmpty(news_url)) {
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);//设置可以运行JS脚本
			settings.setDomStorageEnabled(true);
//			 //滚动条在内容内部显示
			webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
			webView.setHorizontalScrollbarOverlay(false);
//			设置webView页面初始缩放比例（尺寸），默认是100
//			webView.setInitialScale(100);


//			支持内容重新布局
			settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);

//			自适应屏幕
			settings.setUseWideViewPort(true); //打开页面时， 自适应屏幕
			settings.setLoadWithOverviewMode(true);//打开页面时， 自适应屏幕
//			支持缩放
			settings.setSupportZoom(true);// 用于设置webview放大
//			设置WebView可触摸放大缩小
			settings.setBuiltInZoomControls(true);
			settings.setTextSize(WebSettings.TextSize.LARGER);

			webView.setBackgroundResource(R.color.transparent);
			// 添加js交互接口类，并起别名 imagelistner
//			webView.addJavascriptInterface(new JavascriptInterface(getApplicationContext()),"imagelistner");
			webView.setWebChromeClient(new MyWebChromeClient());
			webView.setWebViewClient(new MyWebViewClient());
			MyAsnycTask myAsnycTask = new MyAsnycTask();
			myAsnycTask.execute(news_url, news_title, news_source + " " + news_date);

		}
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		progressBar = (ProgressBar) findViewById(R.id.ss_htmlprogessbar);
		customview_layout = (FrameLayout) findViewById(R.id.customview_layout);
		//底部栏目 tool_bar_layout中
		action_comment_count = (TextView) findViewById(R.id.action_comment_count);

		progressBar.setVisibility(View.VISIBLE);
		title.setTextSize(13);
		title.setVisibility(View.VISIBLE);
		title.setText(news_url);
		action_comment_count.setText(String.valueOf(news.getCommentNum()));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	private class MyAsnycTask extends AsyncTask<String, String,String>{

		@Override
		protected String doInBackground(String... urls) {
			String data=NewsDetailsService.getNewsDetails(urls[0],urls[1],urls[2]);
//			System.out.println("data="+data);
			return data;
		}

		@Override
		protected void onPostExecute(String data) {
			webView.loadDataWithBaseURL (null, data, "text/html", "utf-8",null);
		}
	}

	// 注入js函数监听
/*	private void addImageClickListner() {

		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
//		window.imagelistner.openImage(imgurl)  js通信接口window.imagelistner.openImage(imgurl) 定义了openImage方法
//		imgurl 为imageurl1，imageurl2... 是所有图片的src地址组成的字符串
//		并对每个图片添加点击监听事件，点击执行openImage(imgUrL);
		webView.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\");"
				+ "var imgurl=''; " + "for(var i=0;i<objs.length;i++)  " + "{"
				+ "imgurl+=objs[i].src+',';"
				+ "    objs[i].onclick=function()  " + "    {  "
				+ "        window.imagelistner.openImage(imgurl);  "
				+ "    }  " + "}" + "})()");
	}*/

	// js通信接口
	/*public class JavascriptInterface {



		private Context context;

		public JavascriptInterface(Context context) {

			this.context = context;
		}

//		js监听事件的图片响应事件
		public void openImage(String img) {

			//
			String[] imgs = img.split(",");
			ArrayList<String> imgsUrl = new ArrayList<String>();
			for (String s : imgs) {
				imgsUrl.add(s);
				Log.i("图片的URL>>>>>>>>>>>>>>>>>>>>>>>", s);
			}
			Intent intent = new Intent();
			intent.putStringArrayListExtra("infos", imgsUrl);
			intent.setClass(context, ImageShowActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}*/

	//	WebViewClient 帮助WebView处理各种通知、请求事件
	// 监听
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}


		//		页面加载完毕后执行，打开js功能。监听图片点击事件
		@Override
		public void onPageFinished(WebView view, String url) {

			/*view.getSettings().setJavaScriptEnabled(true);
			view.getSettings().setDomStorageEnabled(true);*/

			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
//			addImageClickListner();
			progressBar.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
		}

		//		onPageStarted页面开始加载时调用
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			/*view.getSettings().setJavaScriptEnabled(true);*/
			super.onPageStarted(view, url, favicon);
		}

		//		接受出现错误时调用：进度条隐藏
		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			progressBar.setVisibility(View.GONE);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	//	WebChromeClient 辅助WebView处理js的对话框、网站图标、网站title、加载进度条等。
	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			if(newProgress != 100){
				progressBar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	}

}
