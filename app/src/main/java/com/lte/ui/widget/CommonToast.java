package com.lte.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import com.lte.R;
import com.lte.utils.ScreenUtil;


/**
 * Created by Administrator on 2016/12/19 0019.
 */

public class CommonToast {

    private CommonToast() {

    }

    public static void show(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.bg_common_toast);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            view.setElevation(10);
        view.setPadding(ScreenUtil.dpToPx(26, context), ScreenUtil.dpToPx(8, context), ScreenUtil.dpToPx(26, context), ScreenUtil.dpToPx(8, context));
        toast.show();
    }

    public static void show(Context context, @StringRes int resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.bg_common_toast);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            view.setElevation(10);
        view.setPadding(ScreenUtil.dpToPx(26, context), ScreenUtil.dpToPx(8, context), ScreenUtil.dpToPx(26, context), ScreenUtil.dpToPx(8, context));
        toast.show();
    }


}
