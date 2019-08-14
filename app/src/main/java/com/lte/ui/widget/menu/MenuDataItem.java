package com.lte.ui.widget.menu;

import android.graphics.Bitmap;

public class MenuDataItem {

	public Bitmap bmp;//功能图片
	public int bmpid;//功能图片id
	public CharSequence text;//功能文字
	public int id;
	public boolean canClick = true;
	/**
	 * @param bmp 功能图片
	 * @param text 功能文字
	 */
	public MenuDataItem(Bitmap bmp, CharSequence text){
		this.bmp=bmp;
		this.text=text;
	}
	/**
	 * @param bmpid 功能图片id
	 * @param text 功能文字
	 */
	public MenuDataItem(int bmpid, CharSequence text){
		this.bmpid=bmpid;
		this.text=text;
	}
	/**
	 * @param bmp 功能图片
	 * @param text 功能文字
	 * @param id 唯一识别值
	 */
	public MenuDataItem(Bitmap bmp, CharSequence text, int id){
		this.bmp=bmp;
		this.text=text;
		this.id=id;
	}
	/**
	 * @param bmpid 功能图片id
	 * @param text 功能文字
	 * @param id 唯一识别值
	 */
	public MenuDataItem(int bmpid, CharSequence text, int id){
		this.bmpid=bmpid;
		this.text=text;
		this.id=id;
	}

	/**
	 * @param bmpid 功能图片id
	 * @param text 功能文字
	 * @param id 唯一识别值
	 */
	public MenuDataItem(int bmpid, CharSequence text, int id, boolean canClick){
		this.bmpid=bmpid;
		this.text=text;
		this.id=id;
		this.canClick=canClick;
	}
}
