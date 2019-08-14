package com.lte.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.App;
import com.communication.BaseApplication;
import com.lte.R;
import com.lte.ui.activity.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @作者: 许明达
 * @创建时间: 2016-3-23下午3:34:38
 * @版权: 微位科技版权所有
 * @描述: UI操作工具类
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class UIUtils {

	public static Context getContext() {
		return App.get();
	}



	/** dip转换px */
	public static int dip2px(int dip) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	/** pxz转换dip */
	public static int px2dip(int px) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}





	public static View inflate(int resId) {
		return LayoutInflater.from(getContext()).inflate(resId, null);
	}



	/** 获取资源 */
	public static Resources getResources() {
		return getContext().getResources();
	}

	/** 获取文字 */
	public static String getString(int resId) {
		return getResources().getString(resId);
	}

	/** 获取文字数组 */
	public static String[] getStringArray(int resId) {
		return getResources().getStringArray(resId);
	}

	/** 获取dimen */
	public static int getDimens(int resId) {
		return getResources().getDimensionPixelSize(resId);
	}

	/** 获取drawable */
	public static Drawable getDrawable(int resId) {
		return getResources().getDrawable(resId);
	}

	/** 获取颜色 */
	public static int getColor(int resId) {
		return getResources().getColor(resId);
	}

	/** 获取颜色选择器 */
	public static ColorStateList getColorStateList(int resId) {
		return getResources().getColorStateList(resId);
	}

	// 判断当前的线程是不是在主线程
//	public static boolean isRunInMainThread() {
//		return android.os.Process.myTid() == getMainThreadId();
//	}

//	public static void runInMainThread(Runnable runnable) {
//		if (isRunInMainThread()) {
//			runnable.run();
//		} else {
//			post(runnable);
//		}
//	}





	






//	public static void startActivityForResultPreAnim(Intent intent, int requestCode) {
//		BaseActivity activity = BaseActivity.getForegroundActivity();
//		if (activity != null) {
//			activity.startActivityForResult(intent, requestCode);
//			activity.overridePendingTransition(R.anim.animprv_in,
//					R.anim.animprv_out);
//		}
//	}


	/** 对toast的简易封装。线程安全，可以在非UI线程调用。 */
//	public static void showToastSafe(final int resId) {
//		showToastSafe(getString(resId));
//	}

	/** 对toast的简易封装。线程安全，可以在非UI线程调用。 */
//	public static void showToastSafe(final String str) {
//		if (isRunInMainThread()) {
//			showToast(str);
//		} else {
//			post(new Runnable() {
//				@Override
//				public void run() {
//					showToast(str);
//				}
//			});
//		}
//	}

    public static long preTime;
    public static String preStr="";



	public static void showToastCenter(Context mContext, String message){
		Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static String getPackageName() {
		return getContext().getPackageName();
	}

	/* 获取当前时间 */
	public static String getCurrentTimeString() {
		long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(time));
	}

	public static  void full(boolean enable, Activity activity) {
		if (enable) {
			WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
			lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			activity.getWindow().setAttributes(lp);
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else {
			WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
			attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			activity.getWindow().setAttributes(attr);
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	public static String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
	}

	public static void hideInputWindow(Activity context){
		if(context==null){
			return;
		}
		final View v = ((Activity) context).getWindow().peekDecorView();
		if (v != null && v.getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	public static void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

		});

	}

}
