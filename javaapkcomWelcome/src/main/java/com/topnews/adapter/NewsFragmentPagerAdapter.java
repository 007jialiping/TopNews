package com.topnews.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
//FragmentPagerAdapter  必须实现两个方法getCount(),getItem(),及构造方法
public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments;
	private FragmentManager fm;
//两个构造函数，参数不同（重载）
	public NewsFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}
//参数 父级fm，要显示的Fragment数组
	public NewsFragmentPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

//	将当前Fragment页面内容全部更换。（先移除所有现有的Fragment，在更换为参数内容）
	public void setFragments(ArrayList<Fragment> fragments) {
		if (this.fragments != null) {
//			启动Fragment事务
			FragmentTransaction ft = fm.beginTransaction();
//			将当前Fragment序列中的Fragment 移除。
			for (Fragment f : this.fragments) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
//			执行commit方法后，并非Fragment事务就立即执行，依赖于系统的处理性能。当然你可以调用executePendingTransactions()方法立即执行
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
//		通知Activity，数据发生变化。
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		Object obj = super.instantiateItem(container, position);
		return obj;
	}

}
