package com.lte.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lte.R;


public class SweetAlertDialog {

    private OnDialogClickListener mOneBtnListener;
    private CharSequence oneBtnText;
    private Context context;
    private int titleIds;
    private String title;
    private String message;
    private boolean isTwoBtn;
    private TextView mContentTitle;
    private TextView mContentMessage;
    private Dialog mDialog;
    private CharSequence mNegativeButtonText;
    private CharSequence mPositiveButtonText;
    private OnDialogClickListener mNegativeButtonListener;
    private OnDialogClickListener mPositiveButtonListener;
    private TextView mLeftTxt;
    private TextView mRightTxt;
    private View mCenterLine;
    private TextView mOneTxt;
    private LinearLayout mTwoBtnContainer;
    private OnDismissListener onDismissListener;

    private boolean isOnRightNotDismiss;

    private boolean cancelable;
    private ViewStub mViewStub;
    private View mRootView;

    public SweetAlertDialog(Builder builder) {
        this.context = builder.mContext;
        this.titleIds = builder.mTitleResId;
        this.title = builder.mTitle;
        this.message = builder.mMessage;
        this.isTwoBtn = builder.mIsTwoBtn;

        this.oneBtnText = builder.mOneBtnText;
        this.mOneBtnListener = builder.mOneBtnListener;

        this.mNegativeButtonText = builder.mNegativeButtonText;
        this.mPositiveButtonText = builder.mPositiveButtonText;
        this.mNegativeButtonListener = builder.mNegativeButtonListener;
        this.mPositiveButtonListener = builder.mPositiveButtonListener;
        this.cancelable = builder.cancelable;
        this.isOnRightNotDismiss = builder.isOnRightNotDismiss;
        this.initView();
    }


    private void initView() {

        mRootView = LayoutInflater.from(context).inflate(R.layout.sweet_alert_dialog_view, null);
        mContentTitle = (TextView) mRootView.findViewById(R.id.tv_dialog_title);
        mContentMessage = (TextView) mRootView.findViewById(R.id.tv_dialog_message);
        mLeftTxt = (TextView) mRootView.findViewById(R.id.dialog_left_txt);
        mRightTxt = (TextView) mRootView.findViewById(R.id.dialog_right_txt);
        mOneTxt = (TextView) mRootView.findViewById(R.id.tv_one_btn);
        mTwoBtnContainer = (LinearLayout) mRootView.findViewById(R.id.ll_two_btn_container);
        mCenterLine = mRootView.findViewById(R.id.dialog_line);
        mViewStub = (ViewStub) mRootView.findViewById(R.id.view_stub);
        // 定义Dialog布局和参数
        mDialog = new Dialog(context, R.style.Sweet_Alert_Dialog);
        mDialog.setContentView(mRootView);
        mDialog.setCanceledOnTouchOutside(false);

        mDialog.setCancelable(cancelable);

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(mDialog);
                }
            }
        });
        updateDialogUI();
//        mDialog.show();
    }

    private void updateDialogUI() {
        // title resId
        if (titleIds != 0) {
            mContentTitle.setVisibility(View.VISIBLE);
            mContentTitle.setText(titleIds);
        }
        // title
        if (hasNull(title)) {
            mContentTitle.setVisibility(View.VISIBLE);
            mContentTitle.setText(title);
        }

        // message
        if (hasNull(message)) {
            mContentMessage.setText(message);
        }else {
            mContentMessage.setVisibility(View.GONE);
        }


        if (!isTwoBtn) {// 一个按钮
            mOneTxt.setVisibility(View.VISIBLE);
            mTwoBtnContainer.setVisibility(View.GONE);

            if (hasNull(oneBtnText)) {
                mOneTxt.setText(oneBtnText);
                mOneTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mDialog != null)
                            mDialog.dismiss();
                        if (mOneBtnListener != null)
                            mOneBtnListener.onClick(mDialog, 2);
                    }
                });
            }

        } else {

            mOneTxt.setVisibility(View.GONE);
            mTwoBtnContainer.setVisibility(View.VISIBLE);
            //左侧文字为空,
            if (!hasNull(mNegativeButtonText) && hasNull(mPositiveButtonText)) {
                mLeftTxt.setVisibility(View.GONE);
                mCenterLine.setVisibility(View.GONE);
            }

            if (hasNull(mPositiveButtonText)) {
                mRightTxt.setVisibility(View.VISIBLE);
                mRightTxt.setText(mPositiveButtonText);
                mRightTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isOnRightNotDismiss) {
                            if (mDialog != null)
                                mDialog.dismiss();
                        }
                        if (mPositiveButtonListener != null)
                            mPositiveButtonListener.onClick(mDialog, 1);
                    }
                });
            }

            // 默认显示取消按钮 自定义字体
            if (hasNull(mNegativeButtonText)) {
                mLeftTxt.setVisibility(View.VISIBLE);
                mLeftTxt.setText(mNegativeButtonText);
                mLeftTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDialog != null)
                            mDialog.dismiss();
                        if (mNegativeButtonListener != null)
                            mNegativeButtonListener.onClick(mDialog, 0);
                    }
                });
            }

        }

    }


    public View addContentView(@LayoutRes int layoutId) {
        mViewStub.setLayoutResource(layoutId);
        return mViewStub.inflate();
    }

    public View findView(@IdRes int viewId) {
        return mRootView.findViewById(viewId);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void setMessage(@StringRes int stringResId) {
        this.message = context.getString(stringResId);
        updateDialogUI();
    }

    public void setMessage(String message) {
        this.message = message;
        updateDialogUI();
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }


    private boolean hasNull(CharSequence msg) {
        return !TextUtils.isEmpty(msg);
    }

    public static class Builder {
        private Context mContext;
        private int mTitleResId;
        private String mTitle;
        private String mMessage;
        private boolean mIsTwoBtn = true;
        private CharSequence mNegativeButtonText;
        private CharSequence mPositiveButtonText;
        private OnDialogClickListener mNegativeButtonListener;
        private OnDialogClickListener mPositiveButtonListener;
        private CharSequence mOneBtnText;
        private OnDialogClickListener mOneBtnListener;

        private boolean cancelable;

        private boolean isOnRightNotDismiss;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTitle(@StringRes int titleId) {
            this.mTitleResId = titleId;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder setMessage(@StringRes int msgResId) {
            this.mMessage = mContext.getString(msgResId);
            return this;
        }


        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setHasTwoBtn(boolean hasTwoBtn) {
            this.mIsTwoBtn = hasTwoBtn;
            return this;
        }

        public Builder setOneButton(CharSequence text, final OnDialogClickListener listener) {
            this.mOneBtnText = text;
            this.mOneBtnListener = listener;
            return this;
        }

        public Builder setOneButton(int mOneResId, final OnDialogClickListener listener) {
            this.mOneBtnText = mContext.getString(mOneResId);
            this.mOneBtnListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final OnDialogClickListener listener) {
            this.mNegativeButtonText = text;
            this.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(@StringRes int mLeftResId, final OnDialogClickListener listener) {
            this.mNegativeButtonText = mContext.getString(mLeftResId);
            this.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text) {
            this.mNegativeButtonText = text;
            return this;
        }

        public Builder setNegativeButton(@StringRes int mLeftResId) {
            this.mNegativeButtonText = mContext.getString(mLeftResId);
            return this;
        }

        public Builder setNegativeButtonOnClickListener(final OnDialogClickListener listener) {
            this.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final OnDialogClickListener listener) {
            this.mPositiveButtonText = text;
            this.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(@StringRes int mRightResId, final OnDialogClickListener listener) {
            this.mPositiveButtonText = mContext.getString(mRightResId);
            this.mPositiveButtonListener = listener;
            return this;
        }

        public void show() {
            create().show();
        }

        public SweetAlertDialog create() {
            return new SweetAlertDialog(this);
        }

        public Builder setOnRightNotDismiss(boolean onRightNotDismiss) {
            isOnRightNotDismiss = onRightNotDismiss;
            return this;
        }
    }

    public interface OnDialogClickListener {
        void onClick(Dialog dialog, int which);
    }

    public interface OnDismissListener {
        void onDismiss(Dialog dialog);
    }
}


