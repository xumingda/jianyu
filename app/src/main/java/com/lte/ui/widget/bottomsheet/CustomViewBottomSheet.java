/*
 * Copyright 2011, 2015 Kai Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lte.ui.widget.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lte.R;


/**
 * One way to present a set of actions to a user is with bottom sheets, a sheet of paper that slides up from the bottom edge of the screen. Bottom sheets offer flexibility in the display of clear and simple actions that do not need explanation.
 * <p/>
 * https://www.google.com/design/spec/components/bottom-sheets.html
 * <p/>
 * Project: BottomSheet
 * Created by Kai Liao on 2014/9/21.
 */
@SuppressWarnings("unused")
public class CustomViewBottomSheet extends Dialog implements DialogInterface {

    private final SparseIntArray hidden = new SparseIntArray();

    private TranslucentHelper helper;
    private String moreText;
    private Drawable close;
    private Drawable more;
    private int mHeaderLayoutId;
    private int mListItemLayoutId;
    private int mGridItemLayoutId;

    private boolean collapseListIcons;
    private Builder builder;
    private ImageView icon;

    private int limit = -1;
    private boolean cancelOnTouchOutside = true;
    private boolean cancelOnSwipeDown = true;
    private OnDismissListener dismissListener;
    private OnShowListener showListener;

    CustomViewBottomSheet(Context context) {
        super(context, R.style.BottomSheet_Dialog);
    }

    @SuppressWarnings("WeakerAccess")
    CustomViewBottomSheet(Context context, int theme) {
        super(context, theme);

        TypedArray a = getContext().obtainStyledAttributes(null, R.styleable.BottomSheet, R.attr.bs_bottomSheetStyle, 0);
        try {
            more = a.getDrawable(R.styleable.BottomSheet_bs_moreDrawable);
            close = a.getDrawable(R.styleable.BottomSheet_bs_closeDrawable);
            moreText = a.getString(R.styleable.BottomSheet_bs_moreText);
            collapseListIcons = a.getBoolean(R.styleable.BottomSheet_bs_collapseListIcons, true);
            mHeaderLayoutId = a.getResourceId(R.styleable.BottomSheet_bs_headerLayout, R.layout.bs_header);
            mListItemLayoutId = a.getResourceId(R.styleable.BottomSheet_bs_listItemLayout, R.layout.bs_list_entry);
            mGridItemLayoutId = a.getResourceId(R.styleable.BottomSheet_bs_gridItemLayout, R.layout.bs_grid_entry);
        } finally {
            a.recycle();
        }

        // https://github.com/jgilfelt/SystemBarTint/blob/master/library/src/com/readystatesoftware/systembartint/SystemBarTintManager.java
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            helper = new TranslucentHelper(this, context);
        }
    }


    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        cancelOnTouchOutside = cancel;
    }

    /**
     * Sets whether this dialog is canceled when swipe it down
     *
     * @param cancel whether this dialog is canceled when swipe it down
     */
    public void setCanceledOnSwipeDown(boolean cancel) {
        cancelOnSwipeDown = cancel;
    }

    @Override
    public void setOnShowListener(OnShowListener listener) {
        this.showListener = listener;
    }

    private void init(final Context context) {
        setCanceledOnTouchOutside(cancelOnTouchOutside);
        final ClosableSlidingLayout mDialogView = (ClosableSlidingLayout) View.inflate(context, R.layout.bottom_sheet_customview_dialog, null);
        LinearLayout mainLayout = (LinearLayout) mDialogView.findViewById(R.id.bs_main);
        mainLayout.addView(View.inflate(context, mHeaderLayoutId, null), 0);
        setContentView(mDialogView);
        if (!cancelOnSwipeDown)
            mDialogView.swipeable = cancelOnSwipeDown;
        mDialogView.setSlideListener(new ClosableSlidingLayout.SlideListener() {
            @Override
            public void onClosed() {
                CustomViewBottomSheet.this.dismiss();
            }

            @Override
            public void onOpened() {

            }
        });

        super.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (showListener != null)
                    showListener.onShow(dialogInterface);
            }
        });
        int[] location = new int[2];
        mDialogView.getLocationOnScreen(location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDialogView.setPadding(0, location[0] == 0 ? helper.mStatusBarHeight : 0, 0, 0);
            mDialogView.getChildAt(0).setPadding(0, 0, 0, helper.mNavBarAvailable ? helper.getNavigationBarHeight(getContext()) + mDialogView.getPaddingBottom() : 0);
        }

        final TextView title = (TextView) mDialogView.findViewById(R.id.bottom_sheet_title);
        if (builder.title != null) {
            title.setVisibility(View.VISIBLE);
            title.setText(builder.title);
        }

        if(null != builder.containerLayout){
            mDialogView.mTarget = builder.containerLayout;
            mainLayout.addView(builder.containerLayout, 0);
        }

        if (builder.dismissListener != null) {
            setOnDismissListener(builder.dismissListener);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        init(getContext());

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;

        TypedArray a = getContext().obtainStyledAttributes(new int[]{android.R.attr.layout_width});
        try {
            params.width = a.getLayoutDimension(0, ViewGroup.LayoutParams.MATCH_PARENT);
        } finally {
            a.recycle();
        }
        super.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dismissListener != null)
                    dismissListener.onDismiss(dialog);
            }
        });
        getWindow().setAttributes(params);
    }


    /**
     * If you make any changes to menu and try to apply it immediately to your bottomsheet, you should call this.
     */
    public void invalidate() {
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }

    /**
     * Constructor using a context for this builder and the {@link com.lte.ui.widget.bottomsheet} it creates.
     */
    public static class Builder {

        private final Context context;
        private View containerLayout;
        private int theme;
        private CharSequence title;
        private OnClickListener listener;
        private OnDismissListener dismissListener;
        private String backgroundUrl;

        /**
         * Constructor using a context for this builder and the {@link com.lte.ui.widget.bottomsheet} it creates.
         *
         * @param context A Context for built BottomSheet.
         */
        public Builder(@NonNull Activity context) {
            this(context, R.style.BottomSheet_Dialog);
            TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{R.attr.bs_bottomSheetStyle});
            try {
                theme = ta.getResourceId(0, R.style.BottomSheet_Dialog);
            } finally {
                ta.recycle();
            }
        }

        /**
         * Constructor using a context for this builder and the {@link com.lte.ui.widget.bottomsheet} it creates with given style
         *
         * @param context A Context for built BottomSheet.
         * @param theme   The theme id will be apply to BottomSheet
         */
        public Builder(Context context, @StyleRes int theme) {
            this.context = context;
            this.theme = theme;
        }


        /**
         * Set title for BottomSheet
         *
         * @param titleRes title for BottomSheet
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder title(@StringRes int titleRes) {
            title = context.getText(titleRes);
            return this;
        }

        /**
         * Set OnclickListener for BottomSheet
         *
         * @param listener OnclickListener for BottomSheet
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder listener(@NonNull OnClickListener listener) {
            this.listener = listener;
            return this;
        }


        /**
         * Show BottomSheet in dark color theme looking
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
//        public Builder darkTheme() {
//            theme = R.style.BottomSheet_Dialog_Dark;
//            return this;
//        }


        /**
         * Show BottomSheet
         *
         * @return Instance of bottomsheet
         */
        public CustomViewBottomSheet show() {
            CustomViewBottomSheet dialog = build();
            dialog.show();
            return dialog;
        }


        /**
         * Create a BottomSheet but not show it
         *
         * @return Instance of bottomsheet
         */
        @SuppressLint("Override")
        public CustomViewBottomSheet build() {
            CustomViewBottomSheet dialog = new CustomViewBottomSheet(context, theme);
            dialog.builder = this;
            return dialog;
        }

        /**
         * Set title for BottomSheet
         *
         * @param title title for BottomSheet
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder title(CharSequence title) {
            this.title = title;
            return this;
        }

        /**
         * Set the OnDismissListener for BottomSheet
         *
         * @param listener OnDismissListener for Bottom
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnDismissListener(@NonNull OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder setContentView(View view) {
            this.containerLayout = view;
            return this;
        }

        /**高斯模糊背景**/
        public Builder setBackgroundUrl(String url) {
            this.backgroundUrl = url;
            return this;
        }
    }


}
