package com.topnews.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
/**
 * ***表示深深的看不懂
 * 重写的ListView,将某天的新闻化为一个section，在这个section的上方标注其发布当天的日期。
 * 如：2016.02.29 星期一
 * 新闻1
 * 新闻2
 * 2016.02.28 星期天
 * 新闻3
 * 新闻4
 */
public class HeadListView extends ListView {

	public interface HeaderAdapter {
//		定义了三种状态
		public static final int HEADER_GONE = 0;
		public static final int HEADER_VISIBLE = 1;
		public static final int HEADER_PUSHED_UP = 2;

		int getHeaderState(int position);

		void configureHeader(View header, int position, int alpha);
	}

	private static final int MAX_ALPHA = 255;

	private HeaderAdapter mAdapter;

	private View mHeaderView;
	private boolean mHeaderViewVisible;
	private int mHeaderViewWidth;
	private int mHeaderViewHeight;
// c重载构造函数
	public HeadListView(Context context) {
		super(context);
	}

	public HeadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeadListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mHeaderView != null) {
			mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
//			getFirstVisiblePosition()来获取当前可见的第一个Item的position并记录
			configureHeaderView(getFirstVisiblePosition());
//			System.out.println("getFirstVisiblePosition--"+String.valueOf(getFirstVisiblePosition()));
		}
	}
// onMeasure() 确定视图的长 宽
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mHeaderView != null) {
			measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
			mHeaderViewWidth = mHeaderView.getMeasuredWidth();
			mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		}
	}


//只有调用此方法时，为mHeaderView赋值，mHeaderView存在。
//mHeaderView 加载的list_item_section.xml ,其格式为“日期 星期*"下一行 分割线；
//若mHeaderView不存在，则HeadListView在构建及画图时，与ListView一致
//	若mHeaderView存在，则重新构建布局ListView，
 public void setPinnedHeaderView(View view) {
		mHeaderView = view;
		if (mHeaderView != null) {
			//listview的上边和下边有黑色的阴影。xml中： android:fadingEdge="none"  
			setFadingEdgeLength(0);
		}
//		requestLayout：当view确定自身已经不再适合现有的区域时，该view本身调用这个方法要求parent
// view重新调用他的onMeasure onLayout来对重新设置自己位置。
		requestLayout();
	}

	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = (HeaderAdapter) adapter;
	}

//	根据position的值，设置nHeaderView的显示位置
	public void configureHeaderView(int position) {
		if (mHeaderView == null) {
			return;
		}
		int state = mAdapter.getHeaderState(position);
		switch (state) {
		case HeaderAdapter.HEADER_GONE: {
			mHeaderViewVisible = false;
			break;
		}
//顶部可见，放在ListView 第一行，起始坐标为（0 0）
		case HeaderAdapter.HEADER_VISIBLE: {
			mAdapter.configureHeader(mHeaderView, position, MAX_ALPHA);
//			view.getTop() 获取顶部坐标

			if (mHeaderView.getTop() != 0) {
				mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			}
			mHeaderViewVisible = true;
//			System.out.println("mHeaderView.getTop()="+mHeaderView.getTop());
			break;
		}
//
		case HeaderAdapter.HEADER_PUSHED_UP: {
//			getChildAt(0); 当前可见的第一Item
//			当Item的底边框小于HeaderView的高度时，headerView别逐步挤出Push_up
//

			View firstView = getChildAt(0);
			int bottom = firstView.getBottom();
			int headerHeight = mHeaderView.getHeight();
			int y;
			int alpha;
			if (bottom < headerHeight) {
				y = (bottom - headerHeight);
				alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
			} else {
				y = 0;
				alpha = MAX_ALPHA;
			}
			mAdapter.configureHeader(mHeaderView, position, alpha);
			if (mHeaderView.getTop() != y) {

				mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
			}
			mHeaderViewVisible = true;
			break;
		}
		}
	}

	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mHeaderViewVisible) {
			drawChild(canvas, mHeaderView, getDrawingTime());
		}
	}
}
