package com.lte.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lte.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 */
public class ToastUtils {

	private static final String TAG = "ToastUtils";
	private static Toast mToast;
	private static View mToastView;

	/**
	 *
	 * 显示自定义样式的Toast消息
	 * @param duration  显示时长 （Toast.LENGTH_SHORT or Toast.LENGTH_LONG）
	 * @param context   ctx
	 * @param toastMsg  待显示的Toast消息内容
	 */
	public static void showToast(Context context,String toastMsg,int duration){
		if (isNotificationEnabled(context)) {
			Log.d(TAG, TAG + " >>> isNotificationEnabled");
			showSystemToast(context, toastMsg, duration);
		} else {
			Log.d(TAG, TAG + " >>> isNotificationDisable");
//			showSnackbarToast(toastMsg, duration);
		}
	}

	private static void showSystemToast(Context context,String toastMsg,int duration) {
		try {
			if(null!=toastMsg&&!TextUtils.isEmpty(toastMsg.trim())){
				context = context.getApplicationContext();
				mToastView = LayoutInflater.from(context).inflate(R.layout.custom_toast,null);

				TextView textView=(TextView)mToastView.findViewById(R.id.toast_text);
				textView.setMaxLines(2);

				textView.setText(toastMsg);

				if (mToast != null) {
					if(Build.VERSION.SDK_INT<11){
						mToast.cancel();
					}
				} else {
					mToast =new Toast(context.getApplicationContext());
				}

				if(duration>0){
					mToast.setDuration(duration);
				}else{
					mToast.setDuration(Toast.LENGTH_SHORT);
				}

				mToast.setView(mToastView);
				mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ViewUtil.dip2px(context, 60));
				mToast.show();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private static void showSnackbarToast(final String toastMsg,final int duration) {
//		try {
//			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						if (!TextUtils.isEmpty(toastMsg)) {
//							Activity activity = ActivityQueueManager.getInstance().getActivity();
//							if (activity != null) {
//								int snackBarDuration = SnackBarToast.LENGTH_SHORT;
//								if (duration == Toast.LENGTH_LONG) {
//									snackBarDuration = SnackBarToast.LENGTH_LONG;
//								}
//								SnackBarToast.make(activity, toastMsg, snackBarDuration).show();
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}, 100);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static boolean isNotificationEnabled(Context context){
		boolean result = true;
		try {
			if (Build.VERSION.SDK_INT >= 19) {
				AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
				ApplicationInfo appInfo = context.getApplicationInfo();

				String pkg = context.getApplicationContext().getPackageName();

				int uid = appInfo.uid;

				Class appOpsClass = null;

				appOpsClass = Class.forName(AppOpsManager.class.getName());

				Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", int.class, int.class, String.class);

				Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
				int value = (int) opPostNotificationValue.get(Integer.class);

				result = ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	static Toast toast = null;
	public static void show(Context context, String text) {
		try {
			if(toast!=null){
				toast.setText(text);
			}else{
				toast= Toast.makeText(context, text, Toast.LENGTH_SHORT);
			}
			toast.show();
		} catch (Exception e) {
			//解决在子线程中调用Toast的异常情况处理
			Looper.prepare();
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			Looper.loop();
		}
	}
}
