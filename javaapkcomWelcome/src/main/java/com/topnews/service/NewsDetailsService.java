package com.topnews.service;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

public class NewsDetailsService {
	public static String getNewsDetails(String url, String news_title,
			String news_date) {

		Document document = null;
		String data=null;
		/*****
		 * *//*20160308 注释掉  做自我调整
		String data = "<body>" +
				"<center><h2 style='font-size:16px;'>" + news_title + "</h2></center>";
		data = data + "<p align='left' style='margin-left:10px'>" 
				+ "<span style='font-size:10px;'>" 
				+ news_date
				+ "</span>" 
				+ "</p>";
		data = data + "<hr size='1' />";
//		Jsoup 抓取数据

		try {
			document = Jsoup.connect(url).timeout(9000).get();
			System.out.println("url="+url);
			System.out.println(document);
			Element element = null;
			if (TextUtils.isEmpty(url)) {
				data = "";
				element = document.getElementById("top_nav");
			} else {

				element = document.getElementById("top_nav");


			}

			if (element != null) {
				data = data + element.toString();
			}
			data = data + "</body>";
		} catch (IOException e) {
			e.printStackTrace();
		}*/



		try {
			document = Jsoup.connect(url).timeout(9000).get();

			Elements titleElement;
			Elements pubtimeElement;
			Elements artElement;



			if (TextUtils.isEmpty(url)) {
				data = "网页找不到";
			} else {
				titleElement = document.getElementsByTag("title");
				pubtimeElement = document.select("#pub_time");
				artElement = document.select("div.art-main");
				data = "<body>" +
						"<center><h2 style='font-size:16px;'>" + titleElement.text() + "</h2></center>";
				data = data + "<p align='left' style='margin-left:10px'>"
						+ "<span style='font-size:10px;'>"
						+ pubtimeElement.toString()
						+ "</span>"
						+ "</p>";
				data =data+"<hr size='1' />"+artElement.toString();
			}

		} catch (IOException e) {
			System.out.println("异常");
			e.printStackTrace();
		}
		data = data + "</body>";
		return data;
	}
}
