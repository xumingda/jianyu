package com.lte.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lte.R;
import com.lte.utils.AppUtils;
import com.lte.utils.PhoneUtil;

import java.util.UUID;

/**
 * 自定义弹框
 * 
 * @author zhengxh
 * @version 2015-3-26
 */
public class MainDialog extends Dialog {
	private Dialog dialog;
	public String dialog_flag;
	private View mView;
	private TextView titleView, btnCancel, btnOK, contentText;
	private View imgCancel;
	private LinearLayout contentLayout;// body部分
	private View titleLayout;// 标题部分
	/** 确定和取消按钮部分 */
	protected RelativeLayout btnLayout;
	/** 单行确定按钮部分 */
	protected LinearLayout singleBtnLayout;
	/** 底部DIY部分 */
	protected LinearLayout bottomDiyLayout;

	private Button singleBtnOK;
	private DialogManager.IClickListener btnCancelListener = null;
	private DialogManager.IClickListener btnOKListener = null;
	private DialogManager.IClickListener dismissListener = null;

	private Context mContext = null;
	
	public String getDialog_flag() {
		return dialog_flag;
	}

	public MainDialog(Context context) {
		super(context, R.style.dialog);
		mContext = context;
		init();
	}

	private void init() {
		mView = LayoutInflater.from(getContext()).inflate(
				R.layout.dialog_default_v1, null);
		super.setContentView(mView);
		initView();
		dialog = this;
		if (TextUtils.isEmpty(dialog_flag)) {
			dialog_flag = UUID.randomUUID().toString();
		}
		setWindow();
	}

	public void setWindow() {
		WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
		dialog.getWindow().setGravity(Gravity.CENTER);
		
		if(null != mContext){
			Configuration mConfiguration = mContext.getResources().getConfiguration(); //获取设置的配置信息
			int ori = mConfiguration.orientation ; //获取屏幕方向
			if(ori == mConfiguration.ORIENTATION_LANDSCAPE){//横屏
				attributes.width = (int) (PhoneUtil.getInstance(getContext()).getScreenInfo().widthPixels * 0.5);
			} else if(ori == mConfiguration.ORIENTATION_PORTRAIT) {//竖屏
				attributes.width = (int) (PhoneUtil.getInstance(getContext()).getScreenInfo().widthPixels * 0.9);
			}	
		} else {
			attributes.width = (int) (PhoneUtil.getInstance(getContext()).getScreenInfo().widthPixels * 0.9);
		}
		
		dialog.getWindow().setAttributes(attributes);
	}

	private void initView() {
		mView.setBackgroundResource(R.drawable.view_shape);
		titleLayout = mView.findViewById(R.id.dialog_title_layout);
		titleView = (TextView) mView.findViewById(R.id.dialog_titleText);
		titleView.setTextColor(mContext.getResources().getColor(R.color.v6_deep_color));
		contentLayout = (LinearLayout) mView.findViewById(R.id.dialog_content_layout);
		btnLayout = (RelativeLayout) mView.findViewById(R.id.dialog_btn_layout);
		btnLayout.setBackgroundColor(mContext.getResources().getColor(R.color.v6_white_color));
		btnLayout.setVisibility(View.GONE);
		singleBtnLayout = (LinearLayout) mView.findViewById(R.id.dialog_single_okBtn_layout);
		bottomDiyLayout = (LinearLayout) mView.findViewById(R.id.dialog_bottom_diy_layout);

		contentText = (TextView) mView.findViewById(R.id.dialog_content_text);
		contentText.setTextColor(mContext.getResources().getColor(R.color.v6_deep_color));
		imgCancel = mView.findViewById(R.id.dialog_imgCancel);
		btnCancel = (TextView) mView.findViewById(R.id.dialog_btnCancel);
		btnOK = (TextView) mView.findViewById(R.id.dialog_btnOK);
		singleBtnOK = (Button) mView.findViewById(R.id.dialog_single_btnOk);

		btnCancel.setTextColor(mContext.getResources().getColor(R.color.v6_deep_color));
		btnCancel.setBackgroundResource(R.drawable.common_item_unbounded_selector);
		btnOK.setBackgroundResource(R.drawable.common_item_unbounded_selector);
		singleBtnOK.setBackgroundResource(R.drawable.btn_click_bg);
		
		imgCancel.setOnClickListener(clickListener);
		btnCancel.setOnClickListener(clickListener);
		btnOK.setOnClickListener(clickListener);
		singleBtnOK.setOnClickListener(clickListener);
	}

	@Override
	public void setContentView(View view) {
		if (mView != null) {
			try{
				ViewGroup parent = (ViewGroup) view.getParent();
				if (parent != null) {
					parent.removeAllViews();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			contentLayout.removeAllViews();
			if(null !=view){
				contentLayout.addView(view);
			}
		}
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		if (mView != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeAllViews();
			}
			contentLayout.removeAllViews();
			contentLayout.addView(view, params);
		}
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

	public LinearLayout getContentView() {
		return contentLayout;
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_imgCancel:
			case R.id.dialog_btnCancel:
				if (btnCancelListener != null) {
					boolean isCancel = btnCancelListener.click(dialog, v);
					if (isCancel)
						closeDialog(dialog_flag);
				} else {
					closeDialog(dialog_flag);
				}
				break;
			case R.id.dialog_btnOK:
			case R.id.dialog_single_btnOk:
				if (btnOKListener != null) {
					boolean isOk = btnOKListener.click(dialog, v);
					if (isOk)
						closeDialog(dialog_flag);
				} else {
					closeDialog(dialog_flag);
				}
				break;
			}
			AppUtils.hideInputKeyboard(getContext(), mView);
		}
	};

	public void dismiss() {
		if (dismissListener != null) {
			dismissListener.click(dialog, null);
		}
		super.dismiss();
	};

	/**
	 * 弹框dismiss监听
	 * 
	 * @param onClickListener
	 */
	public void setDismissListener(DialogManager.IClickListener onClickListener) {
		dismissListener = onClickListener;
	}

    /** 设置关闭对话框按钮是否显示 */
    public void setImgCancelVisible(boolean isShow) {
        if (isShow) {
            imgCancel.setVisibility(View.VISIBLE);
        } else {
            imgCancel.setVisibility(View.GONE);
        }
    }

    /** 设置单行确定按钮是否显示 */
	public void setSingleBtnOKVisible(boolean isShow) {
		if (isShow) {
			singleBtnLayout.setVisibility(View.VISIBLE);
		} else {
			singleBtnLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 当设置标题为空时隐藏标题栏
	 */
	@Override
	public void setTitle(CharSequence title) {
		if (!TextUtils.isEmpty(title)) {
			titleView.setText(title);
		} else {
			titleLayout.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 设置标题栏，当设置标题为空时隐藏标题栏，可设置标题颜色<br/>
	 * added by wangganxin 2015-06-17
	 * @param title 标题
	 * @param resId 颜色资源ID
	 */
	public void setTitle(CharSequence title, int resId){
		if (!TextUtils.isEmpty(title)) {
			titleView.setText(title);
			titleView.setTextColor(getContext().getResources().getColor(resId));
		} else {
			titleLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置取消按钮
	 * 
	 * @param text
	 *            当text为空时只显示 确定按钮(控制"单行确定按钮" 和 "确定+取消按钮"切换)
	 * @param onClickListener
	 *            取消监听
	 */
	public void setCancelButton(String text, DialogManager.IClickListener onClickListener) {
		if (mView == null)
			return;
		if (TextUtils.isEmpty(text)) {
			setBtnLayout(false);
		} else {
			setBtnLayout(true);
			btnCancel.setText(text);
		}
		btnCancelListener = onClickListener;
	}
	
	/**
	 * 设置取消按钮，及按钮颜色
	 * @param text 
	 *        当text为空时只显示 确定按钮(控制"单行确定按钮" 和 "确定+取消按钮"切换)
	 * @param resId 
	 *        颜色资源ID
	 * @param onClickListener 
	 *        取消监听
	 */
	public void setCancelButton(String text, int resId, DialogManager.IClickListener onClickListener) {
		if (mView == null)
			return;
		if (TextUtils.isEmpty(text)) {
			setBtnLayout(false);
		} else {
			setBtnLayout(true);
			btnCancel.setText(text);
			btnCancel.setTextColor(getContext().getResources().getColor(resId));
		}
		btnCancelListener = onClickListener;
	}

	/**
	 * 设置确定按钮
	 * 
	 * @param text
	 *            确定按钮文本
	 * @param onClickListener
	 *            取消监听
	 */
	public void setOKButton(String text, final DialogManager.IClickListener onClickListener) {
		if (mView != null && !TextUtils.isEmpty(text)) {
			if ("确定".equals(text)) {
				singleBtnOK.setText("确    定");
			} else {
				singleBtnOK.setText(text);
			}
			btnOK.setText(text);
			btnOKListener = onClickListener;
		}
	}
	
    /**
     * 设置确定按钮，及按钮颜色<br/>
     * 
     * added by wangganxin 2015.06.17
     * @param text 
     *            确定按钮文本
     * @param resId 
     *            颜色资源ID
     * @param onClickListener 
     *            点击的监听
     */
	public void setOKButton(String text, int resId, final DialogManager.IClickListener onClickListener) {
		if (mView != null && !TextUtils.isEmpty(text)) {
			if ("确定".equals(text)) {
				singleBtnOK.setText("确    定");
			} else {
				singleBtnOK.setText(text);
			}
			btnOK.setText(text);
			btnOK.setTextColor(getContext().getResources().getColor(resId));
			btnOKListener = onClickListener;
		}
	}

	/**
	 * 是否显示确定或取消按钮
	 * 
	 * @param isShow
	 *            true表示显示, false表示只显示确定按钮
	 */
	private void setBtnLayout(boolean isShow) {
		if (isShow) {
			btnLayout.setVisibility(View.VISIBLE);
			imgCancel.setVisibility(View.GONE);
			singleBtnLayout.setVisibility(View.GONE);
		} else {
			btnLayout.setVisibility(View.GONE);
			imgCancel.setVisibility(View.VISIBLE);
			singleBtnLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置Message(当自定义contentView后设置该项无效)
	 * 
	 * @param msg
	 */
	public void setMessage(String msg) {
		if (mView != null && !TextUtils.isEmpty(msg)) {
			if (msg.contains("<center>") && msg.contains("</center>")) {
				msg = msg.replace("<center>", "").replace("</center>", "");
				System.out.println("-----------!!!!!!msg:" + msg);
			}
			contentText.setVisibility(View.VISIBLE);
            contentText.setText(Html.fromHtml(msg));
		}
	}

	/**
	 * 设置确定按钮底部diy的view
	 * 
	 * @param view
	 */
	public void setBottomDiyLayout(View view) {
		if (view != null) {
			bottomDiyLayout.setVisibility(View.VISIBLE);
			bottomDiyLayout.removeAllViews();
			bottomDiyLayout.addView(view);
		}
	}

	public LinearLayout getBottomDiyLayout() {
		return bottomDiyLayout;
	}

	public View getDialogView() {
		return mView;
	}

	/**
	 * 关闭dialog
	 * 
	 * @param dialogFlag
	 */
	public void closeDialog(String dialogFlag) {
		DialogManager.closeDialog(dialogFlag);
		// dismiss();
	}
}
