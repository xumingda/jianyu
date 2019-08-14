package com.lte.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lte.R;
import com.lte.utils.CountDownTimerUtil;


/**
 * Created by kolyn on 2016/12/17.
 */

public class CommBottomDialog {
    private static final String TAG = "CommBottomDialog";

    private Context mContext;
    private String mMessage;
    private OnDialogClickListener mOnDialogClickListener;
    private TextView mTvMessage;
    private View mView;
    private AlertDialog mAlertDialog;
    private Dialog mDialog;
    private ImageView mIvState;
    private DialogInterface.OnDismissListener onDismissListener;
    private static final long MILLIS_DISMISS_DEFAULT = 5 * 1000L;

    private long mDismissMillis;
    private CountDownTimerUtil mCountDownTimerUtil;

    public CommBottomDialog setSuccess(String message) {
        mMessage = message;
        mIvState.setImageResource(R.mipmap.icon_success);
        mTvMessage.setText(mMessage);
        return this;
    }

    public CommBottomDialog setSuccess(@StringRes int resId) {
        mMessage = mContext.getString(resId);
        mIvState.setImageResource(R.mipmap.icon_success);
        mTvMessage.setText(mMessage);
        return this;
    }

    public CommBottomDialog setError(String message) {
        mMessage = message;
        mIvState.setImageResource(R.mipmap.icon_error);
        mTvMessage.setText(mMessage);
        return this;
    }

    public CommBottomDialog setError(@StringRes int resId) {
        mMessage = mContext.getString(resId);
        mIvState.setImageResource(R.mipmap.icon_error);
        mTvMessage.setText(mMessage);
        return this;
    }

    public CommBottomDialog setMessage(String message) {
        mMessage = message;
        mTvMessage.setText(mMessage);
        return this;
    }

    public CommBottomDialog setMessage(@StringRes int resId) {
        mMessage = mContext.getString(resId);
        mTvMessage.setText(mMessage);
        return this;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public CommBottomDialog setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        mOnDialogClickListener = onDialogClickListener;
        return this;
    }

    private CommBottomDialog() {

    }

    private CommBottomDialog(Builder builder) {
        this.mContext = builder.mContext;
        this.mMessage = builder.mMessage;
        this.mDismissMillis = builder.mDismissMillis;
        this.mOnDialogClickListener = builder.mOnDialogClickListener;
        this.onDismissListener = builder.onDismissListener;
        initView();
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_comm_bottom_dialog, null);
        mIvState = (ImageView) mView.findViewById(R.id.iv_state);
        mTvMessage = (TextView) mView.findViewById(R.id.tv_message);

        mTvMessage.setText(mMessage);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dismiss();
            }

        });

//        mDialog = new Dialog(mContext, R.style.AppTheme_Bottom_dialog);
//        mDialog.setCanceledOnTouchOutside(true);
//        mDialog.setContentView(mView);
//        WindowManager.LayoutParams attributes = mDialog.getWindow().getAttributes();
//        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        attributes.gravity = Gravity.BOTTOM;
//        mDialog.getWindow().setAttributes(attributes);
//
//        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                if (onDismissListener != null) {
//                    onDismissListener.onDismiss(dialog);
//                }
//            }
//        });


        mCountDownTimerUtil = new CountDownTimerUtil(mDismissMillis, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
//                if (mDialog.isShowing()) {
                dismiss();
//                }
            }
        };

        mAlertDialog = new AlertDialog.Builder(mContext, R.style.AppTheme_EchoCloud_Bottom_dialog).setView(mView).create();
//        mAlertDialog.setCanceledOnTouchOutside(true);

        mAlertDialog.getWindow().setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);

//        WindowManager.LayoutParams attributes = mAlertDialog.getWindow().getAttributes();
//        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//        mAlertDialog.getWindow().setAttributes(attributes);

        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mCountDownTimerUtil.cancel();
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(dialog);
                }
            }
        });


    }

    public void show() {
        Log.i(TAG, "show: ");
        mCountDownTimerUtil.cancel();
        mCountDownTimerUtil.start();
//        mDialog.show();
        mAlertDialog.show();
//        mAlertDialog.setCanceledOnTouchOutside(false);
//        mDialog.setCanceledOnTouchOutside(true);
    }

    public void dismiss() {
        Log.i(TAG, "dismiss: ");
        mCountDownTimerUtil.cancel();

        mAlertDialog.dismiss();
//        mDialog.dismiss();
    }

    public static class Builder {
        private Context mContext;

        private String mMessage;

        private long mDismissMillis = MILLIS_DISMISS_DEFAULT;

        private OnDialogClickListener mOnDialogClickListener;
        private DialogInterface.OnDismissListener onDismissListener;

        public Builder(Context context) {

            this.mContext = context;
        }

        public Builder setDismissMillis(long dismissMillis) {
            this.mDismissMillis = dismissMillis;
            return this;
        }

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder setMessage(@StringRes int resId) {
            mMessage = mContext.getString(resId);
            return this;
        }

        public Builder setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
            mOnDialogClickListener = onDialogClickListener;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public CommBottomDialog create() {
            return new CommBottomDialog(this);
        }

    }

    public interface OnDialogClickListener {
        void onClick(View view);
    }
}
