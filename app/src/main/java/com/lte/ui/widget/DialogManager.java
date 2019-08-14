package com.lte.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.lte.R;
import com.lte.utils.PhoneUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class DialogManager {

	public static boolean isMsg;

	private static final Map<String, WeakReference<Dialog>> dialogCache = new HashMap<String, WeakReference<Dialog>>();

	public static boolean isShowing = false;


	public interface IClickListener {
		boolean click(Dialog dlg, View view);
	}

	public static String showAlertDialog(Context context, String title, String msg, boolean canCancel, String okText, IClickListener okBtnClickListener, String cancelText,
										 IClickListener cancelBtnClickListener) {
		return showAlertDialog(context, title, msg, canCancel, okText, okBtnClickListener, cancelText, cancelBtnClickListener, null);
	}

	/**
	 * 可监听dialog被取消后的事件
	 *
	 * @param context
	 * @param title
	 * @param msg
	 * @param canCancel              是否dialog设置setCancelable
	 * @param okText
	 * @param okBtnClickListener
	 * @param cancelText
	 * @param cancelBtnClickListener
	 * @return
	 */
	public static String showAlertDialog(Context context, String title, String msg, boolean canCancel, String okText, IClickListener okBtnClickListener, String cancelText,
										 IClickListener cancelBtnClickListener, DialogInterface.OnDismissListener dismissListener) {
		MainDialog dialog = createDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setOKButton(okText, okBtnClickListener);
		dialog.setCancelButton(cancelText, cancelBtnClickListener);
		if (dismissListener != null)
			dialog.setOnDismissListener(dismissListener);
		dialog.setCancelable(canCancel);
		if (canCancel)
			dialog.setOnKeyListener(new OnKeyListener() {
				boolean isDown = false;
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						if (event.getAction() == KeyEvent.ACTION_UP) {
							try {
								if (isDown)
									dialog.dismiss();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (event.getAction() == KeyEvent.ACTION_DOWN) {
							isDown = true;
						}
						return true;
					}
					return false;
				}
			});
		try {
			if (null != context && context instanceof Activity) {
				Activity currentActivity = (Activity) context;
				if (null != currentActivity && !currentActivity.isFinishing() && !currentActivity.isRestricted()) {
					dialog.show();
					/*解决锁屏开屏后dialog截断的问题*/
//					WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//					Display display = manager.getDefaultDisplay();
//					WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//					params.width = display.getHeight();
//					dialog.getWindow().setAttributes(params);
				}
			} else {
				Log.d("imusic", "currentActivity has finished when show dialog in DialogManager...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog.dialog_flag;
	}
	public static MainDialog createDialog(Context context) {
		isShowing = true;
		MainDialog dialog = new MainDialog(context);
		dialogCache.put(dialog.getDialog_flag(), new WeakReference<Dialog>(dialog));
		return dialog;
	}
	public static synchronized void closeDialog(String dialogFlag) {
		try {
			if (dialogCache != null && dialogCache.containsKey(dialogFlag)) {
				WeakReference<Dialog> dialogRef = dialogCache.get(dialogFlag);
				if (dialogRef != null) {
					Dialog dialog = dialogRef.get();
					if (dialog != null && dialog.isShowing()) {
						boolean close = dialog.getContext() != null;
						if (close && dialog.getContext() instanceof Activity) {
							close = !((Activity) dialog.getContext()).isFinishing();
						}
						if (close) {
							dialog.dismiss();
						}
					}
					dialogRef.clear();
				}
				dialogCache.remove(dialogFlag);
				isShowing = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String showAlertDialog(Context context, String title, String msg, String okText, IClickListener okBtnClickListener, String cancelText,
										 IClickListener cancelBtnClickListener) {
		return showAlertDialog(context, title, msg, true, okText, okBtnClickListener, cancelText, cancelBtnClickListener);
	}

	public static void showGoToSysSettingDialog(final Context context, String title, String content) {
		showAlertDialog(context, title, content, "前往设置", new DialogManager.IClickListener() {

			@Override
			public boolean click(Dialog dlg, View view) {
				try {
					Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					Uri packageUri = Uri.parse("package:" + context.getPackageName());
					intent.setData(packageUri);
					context.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		}, "取消", new DialogManager.IClickListener() {

			@Override
			public boolean click(Dialog dlg, View view) {
				return true;
			}
		});
	}


	public static String showDialog(Context context, String title,
									final View contentView, String okText, IClickListener okBtnClickListener, String cancelText, IClickListener cancelBtnClickListener, final IClickListener backKeyListener) {
		final MainDialog dialog = createDialog(context);
		dialog.setTitle(title);
		dialog.setContentView(contentView);
		dialog.setOKButton(okText, okBtnClickListener);
		dialog.setCancelButton(cancelText, cancelBtnClickListener);
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
				boolean result = false;
				if (KeyEvent.KEYCODE_BACK == keyEvent.getKeyCode() && KeyEvent.ACTION_UP == keyEvent.getAction()) {

					DialogManager.closeDialog(dialog.dialog_flag);
					return false;
				}
				return result;
			}
		});
		boolean isDialogShowed = false;
		if (null != context && context instanceof Activity) {
			Activity currentActivity = (Activity) context;
			if (null != currentActivity && !currentActivity.isFinishing() && !currentActivity.isRestricted()) {
				dialog.show();
				isDialogShowed = true;
			}
		} else {
			Log.d("imusic", "currentActivity has finished when show dialog in DialogManager...");
		}
		if (!isDialogShowed) {
//			removeFlag(dialog.dialog_flag);
		}
		return dialog.dialog_flag;
	}
	public static String showInputDialog(Context context, String title, String msg, boolean canCancel, String okText, IClickListener okBtnClickListener, String cancelText,
										 IClickListener cancelBtnClickListener, DialogInterface.OnDismissListener dismissListener) {
		MainDialog dialog = createDialog(context);
		LinearLayout contentView = dialog.getContentView();
		View layout = LayoutInflater.from(context).inflate(R.layout.dialog_edit_layout,null);
		contentView.addView(layout);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setOKButton(okText, okBtnClickListener);
		dialog.setCancelButton(cancelText, cancelBtnClickListener);
		if (dismissListener != null)
			dialog.setOnDismissListener(dismissListener);
		dialog.setCancelable(canCancel);
		if (canCancel)
			dialog.setOnKeyListener(new OnKeyListener() {
				boolean isDown = false;

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						if (event.getAction() == KeyEvent.ACTION_UP) {
							try {
								if (isDown)
									dialog.dismiss();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (event.getAction() == KeyEvent.ACTION_DOWN) {
							isDown = true;
						}
						return true;
					}
					return false;
				}
			});
		try {
			if (null != context && context instanceof Activity) {
				Activity currentActivity = (Activity) context;
				if (null != currentActivity && !currentActivity.isFinishing() && !currentActivity.isRestricted()) {
					dialog.show();
					/*解决锁屏开屏后dialog截断的问题*/
//					WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//					Display display = manager.getDefaultDisplay();
//					WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//					params.width = display.getHeight();
//					dialog.getWindow().setAttributes(params);
				}
			} else {
				Log.d("imusic", "currentActivity has finished when show dialog in DialogManager...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog.dialog_flag;
	}

	public static String showProgressDialog(Context context, String text,boolean isCancelable) {
		return showProgressDialog(context,text,null,isCancelable);
	}
	public static String showProgressDialog(Context context, String text, final LocalCallInterface localCall,boolean isCancelable) {
		return showProgressDialog(context,text,localCall,0,isCancelable);
	}
	public static String showProgressDialog(final Context context, String text, final LocalCallInterface localCall, int delayTime,boolean isCancelable) {
		final LocalProgressDialog pdlg = LocalProgressDialog.getDialog(context);
		boolean isDialogShowed = false;
		Handler handler = new Handler();
		try {
			pdlg.setMessage(text);
			if(isCancelable){
				pdlg.setCancelable(true);

				pdlg.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if (KeyEvent.KEYCODE_BACK == keyCode && KeyEvent.ACTION_UP == event.getAction()) {
						isProgressDialogCloseByUser = true;
						pdlg.isContinueToShow = false;
						closeDialog(pdlg.flag);
						if (localCall != null)
							localCall.onFailed();
						}
						return true;
					}
				});
			}else {
				pdlg.setCancelable(false);

				pdlg.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if (KeyEvent.KEYCODE_BACK == keyCode && KeyEvent.ACTION_UP == event.getAction()) {
//						isProgressDialogCloseByUser = true;
//						pdlg.isContinueToShow = false;
//						closeDialog(pdlg.flag);
//						if (localCall != null)
//							localCall.onFailed();
						}
						return true;
					}
				});
			}

			if (null != context && context instanceof Activity){
				Activity currentActivity = (Activity) context;
				if (null != currentActivity && !currentActivity.isFinishing() && !currentActivity.isRestricted()) {
					isDialogShowed = true;
					if(handler==null){
						handler = new Handler();
					}
					final Handler finalHandler = handler;
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							try {
								if(pdlg.isContinueToShow){
									pdlg.show();
									finalHandler.postDelayed(new Runnable() {
										@Override
										public void run() {
											try {
												if(pdlg.isShowing()){
//													pdlg.setMessage("加载缓慢...");
												}
											}catch (Exception e){
												e.printStackTrace();
											}
										}
									},OVER_TIME);
								}
							}catch (Exception e){
								e.printStackTrace();
							}
						}
					},delayTime);
				}
			}
			else{
				isDialogShowed = true;
				if(handler==null){
					handler = new Handler();
				}
				final Handler finalHandler1 = handler;
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							if(pdlg.isContinueToShow){
								DialogManager.setSystemWindowType(context,pdlg.getWindow());
								pdlg.show();
								finalHandler1.postDelayed(new Runnable() {
									@Override
									public void run() {
										try {
											if(pdlg.isShowing()){
//												pdlg.setMessage("加载缓慢...");
											}
										}catch (Exception e){
											e.printStackTrace();
										}
									}
								},OVER_TIME);
							}
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				},delayTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!isDialogShowed && pdlg != null) {
			removeFlag(pdlg.flag);
		}
		return pdlg.flag;
	}
	private static final int DELEY_TIME = 500;

	private static final int OVER_TIME = 5000;
	public interface LocalCallInterface {
		/**
		 * finish回调函数
		 *
		 * @param json
		 *            标准json字符串
		 */
		public void onFinished(String json);
		public void onFailed();
	}
	/**
	 * 设置窗口类型
	 * @param context
	 * @param window
	 */
	public static void setSystemWindowType(Context context,Window window) {
//        if(DeviceUtil.isMIUIRom()){ // 小米禁止系统悬浮框的使用
//            return ;
//        }

		if(PhoneUtil.getInstance(context).getPhoneSDK()>=19&& PhoneUtil.getInstance(context).getTargetSdkVersion()>=19){
			window.setType(WindowManager.LayoutParams.TYPE_TOAST);
		}
		else{
			window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		}
	}

	public static class LocalProgressDialog extends ProgressDialog {
		private String flag;
		private boolean isContinueToShow = true;

		public static LocalProgressDialog getDialog(Context context) {
			isProgressDialogCloseByUser = false;
			return new LocalProgressDialog(context);
		}

		public LocalProgressDialog(Context context) {
			super(context);
			setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					removeFlag(flag);
				}
			});
			if (TextUtils.isEmpty(flag)) {
				flag = UUID.randomUUID().toString();
			}
			dialogCache.put(flag, new WeakReference<Dialog>(this));
		}

		public LocalProgressDialog(Context context, int progressBackDialog) {
			super(context,progressBackDialog);
			setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					removeFlag(flag);
				}
			});
			if (TextUtils.isEmpty(flag)) {
				flag = UUID.randomUUID().toString();
			}
			dialogCache.put(flag, new WeakReference<Dialog>(this));
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			try {
				return super.dispatchTouchEvent(ev);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public static synchronized void removeFlag(String dialogFlag) {
		if (dialogCache != null && dialogFlag.contains(dialogFlag)) {
			WeakReference<Dialog> dialogRef = dialogCache.get(dialogFlag);
			if (dialogRef != null && dialogRef.get() != null) {
				dialogRef.clear();
			}
			dialogCache.remove(dialogFlag);
		}
	}
	public static boolean isProgressDialogCloseByUser = false;


}
