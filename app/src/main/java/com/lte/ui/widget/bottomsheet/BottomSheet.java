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
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lte.R;
import com.lte.utils.PhoneUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * One way to present a set of actions to a user is with bottom sheets, a sheet of paper that slides up from the bottom edge of the screen. Bottom sheets offer flexibility in the display of clear and simple actions that do not need explanation.
 * <p/>
 * https://www.google.com/design/spec/components/bottom-sheets.html
 * <p/>
 * Project: BottomSheet
 * Created by Kai Liao on 2014/9/21.
 */
@SuppressWarnings("unused")
public class BottomSheet extends Dialog implements DialogInterface {
    public static final int SHOW_TYPE_MULTI_SCREEN_PLAY_LIST = 400;
    private final SparseIntArray hidden = new SparseIntArray();

    private TranslucentHelper helper;
    private String moreText;
    private Drawable close;
    private Drawable more;
    private int mHeaderLayoutId;
    private int mListItemLayoutId;
    private int mGridItemLayoutId;

    private TextView  subTitle;
    private TextView clearPlayList;
    private boolean collapseListIcons;
    private GridView list;
    public SimpleSectionedGridAdapter adapter;
    private Builder builder;
    private int limit = -1;
    private boolean cancelOnTouchOutside = true;
    private boolean cancelOnSwipeDown = true;
    private ActionMenu fullMenuItem;
    private ActionMenu menuItem;
    private ActionMenu actions;
    private OnDismissListener dismissListener;
    private OnKeyListener keyListener;
    private OnShowListener showListener;
    public RelativeLayout bottomsheetProgress;

    BottomSheet(Context context) {
        super(context, R.style.BottomSheet_Dialog);
    }

    @SuppressWarnings("WeakerAccess")
    BottomSheet(Context context, int theme) {
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

    /**
     * Hacky way to get gridview's column number
     */
    private int getNumColumns() {
        try {
            Field numColumns = GridView.class.getDeclaredField("mRequestedNumColumns");
            numColumns.setAccessible(true);
            return numColumns.getInt(list);
        } catch (Exception e) {
            return 1;
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
        final ClosableSlidingLayout mDialogView = (ClosableSlidingLayout) View.inflate(context, R.layout.bottom_sheet_dialog, null);
        LinearLayout mainLayout = (LinearLayout) mDialogView.findViewById(R.id.bs_main);
        View headerLayout = View.inflate(context, mHeaderLayoutId, null);
        mainLayout.addView(headerLayout, 0);
        if(null != builder.customView){
            LinearLayout moreLayout = (LinearLayout) mDialogView.findViewById(R.id.bs_more);
            moreLayout.addView(builder.customView,0);
        }
        setContentView(mDialogView);
        if (!cancelOnSwipeDown)
            mDialogView.swipeable = cancelOnSwipeDown;
        mDialogView.setSlideListener(new ClosableSlidingLayout.SlideListener() {
            @Override
            public void onClosed() {
                BottomSheet.this.dismiss();
            }

            @Override
            public void onOpened() {
                showFullItems();
            }
        });

        super.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (showListener != null)
                    showListener.onShow(dialogInterface);
                list.setAdapter(adapter);
                list.startLayoutAnimation();
            }
        });
        int[] location = new int[2];
        mDialogView.getLocationOnScreen(location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDialogView.setPadding(0, location[0] == 0 ? helper.mStatusBarHeight : 0, 0, 0);
            mDialogView.getChildAt(0).setPadding(0, 0, 0, helper.mNavBarAvailable ? helper.getNavigationBarHeight(getContext()) + mDialogView.getPaddingBottom() : 0);
        }


        final TextView title = (TextView) mDialogView.findViewById(R.id.bottom_sheet_title);
        if (title != null && builder.title != null) {
            headerLayout.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            title.setText(builder.title);
        }

        subTitle = (TextView) mDialogView.findViewById(R.id.bottom_sheet_subtitle);
        if (subTitle != null && builder.subTitle != null) {
            subTitle.setVisibility(View.VISIBLE);
            subTitle.setText(builder.subTitle);
        }

        bottomsheetProgress =  (RelativeLayout) mDialogView.findViewById(R.id.bottomsheet_base_progress);
        list = (GridView) mDialogView.findViewById(R.id.bottom_sheet_gridview);
        mDialogView.mTarget = list;
        if (!builder.grid) {
            list.setNumColumns(1);
        }

        if (builder.grid) {
            for (int i = 0; i < getMenu().size(); i++) {
                if (getMenu().getItem(i).getIcon() == null)
                    throw new IllegalArgumentException("You must set icon for each items in grid style");
            }
        }

        if (builder.limit > 0)
            limit = builder.limit * getNumColumns();
        else
            limit = Integer.MAX_VALUE;

        mDialogView.setCollapsible(false);

        actions = builder.menu;
        menuItem = actions;
        // over the initial numbers
        if (getMenu().size() > limit) {
            fullMenuItem = builder.menu;
            menuItem = builder.menu.clone(limit - 1);
            ActionMenuItem item = new ActionMenuItem(context, 0, R.id.bs_more, 0, limit - 1, moreText);
            item.setIcon(more);
            menuItem.add(item);
            actions = menuItem;
            mDialogView.setCollapsible(true);
        }

        BaseAdapter baseAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return actions.size() - hidden.size();
            }

            @Override
            public MenuItem getItem(int position) {
                return actions.getItem(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEnabled(int position) {
                return getItem(position).isEnabled();
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ViewHolder holder;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    if (builder.grid) {
                        convertView = inflater.inflate(mGridItemLayoutId, parent, false);
                    } else {
                        convertView = inflater.inflate(mListItemLayoutId, parent, false);
                    }

                    holder = new ViewHolder();
                    holder.title = (TextView) convertView.findViewById(R.id.bs_list_title);
                    holder.title.setTextColor(context.getResources().getColor(R.color.v6_deep_color));
                    try{
                        holder.bs_list_diviver = (TextView) convertView.findViewById(R.id.bs_list_diviver);
                        if (holder.bs_list_diviver != null) {
                            holder.bs_list_diviver.setBackgroundColor(context.getResources().getColor(R.color.gray_d0));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    holder.bs_list_fav = (ImageView) convertView.findViewById(R.id.bs_list_fav);
                    holder.singer = (TextView) convertView.findViewById(R.id.bs_list_singer);
                    holder.deleteImg = (ImageView) convertView.findViewById(R.id.bs_list_delete);
                    holder.labelImg = (ImageView) convertView.findViewById(R.id.bs_list_playing_label);
                    holder.priceflag = (TextView) convertView.findViewById(R.id.bs_list_priceflag);
                    holder.hqImg = (ImageView) convertView.findViewById(R.id.bs_list_hq);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                for (int i = 0; i < hidden.size(); i++) {
                    if (hidden.valueAt(i) <= position)
                        position++;
                }

                final MenuItem item = getItem(position);

                String title = item.getTitle().toString();
                String imageUrl = null;
                String singer = "";
                String jsonFlag = "";
                String redId = "";
                long pmId = 0;
                int pmType = -1;
                String musicUrl = "";
                try {
                    holder.title.setTextColor(context.getResources().getColor(R.color.v6_deep_color));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (holder.title != null && !TextUtils.isEmpty(title)) {
                    holder.title.setText(title);
                }
                holder.title.setEnabled(item.isEnabled());

                if (builder.showType == 300) {
                    if (holder.singer != null && !TextUtils.isEmpty(singer)) {
                        holder.singer.setText(singer);
                    }
                }

                return convertView;
            }



            class ViewHolder {
                TextView title;
                TextView bs_list_diviver;
                TextView singer;
                ImageView labelImg;
                ImageView deleteImg;
                TextView priceflag;
                ImageView hqImg;
                ImageView bs_list_fav;
            }
        };

        adapter = new SimpleSectionedGridAdapter(context, baseAdapter, R.layout.bs_list_divider, R.id.headerlayout, R.id.header);
        list.setAdapter(adapter);
        adapter.setGridView(list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (((MenuItem) adapter.getItem(position)).getItemId() == R.id.bs_more) {
                        showFullItems();
                        mDialogView.setCollapsible(false);
                        return;
                    }

                    if (!((ActionMenuItem) adapter.getItem(position)).invoke()) {
                        if (builder.menulistener != null)
                            builder.menulistener.onMenuItemClick((MenuItem) adapter.getItem(position));
                        else if (builder.listener != null)
                            builder.listener.onClick(BottomSheet.this, ((MenuItem) adapter.getItem(position)).getItemId());
                    }
                    dismiss();
                } catch (Exception e) {
                    dismiss();
                    e.printStackTrace();
                }
            }
        });

        if (builder.dismissListener != null) {
            setOnDismissListener(builder.dismissListener);
        }

        if (builder.keyListener != null) {
            setOnKeyListener(builder.keyListener);
        }
        setListLayout();
    }



    private void updateSection() {
        actions.removeInvisible();

        if (!builder.grid && actions.size() > 0) {
            int groupId = actions.getItem(0).getGroupId();
            ArrayList<SimpleSectionedGridAdapter.Section> sections = new ArrayList<>();
            for (int i = 0; i < actions.size(); i++) {
                if (actions.getItem(i).getGroupId() != groupId) {
                    groupId = actions.getItem(i).getGroupId();
                    sections.add(new SimpleSectionedGridAdapter.Section(i, null));
                }
            }
            if (sections.size() > 0) {
                SimpleSectionedGridAdapter.Section[] s = new SimpleSectionedGridAdapter.Section[sections.size()];
                sections.toArray(s);
                adapter.setSections(s);
            } else {
                adapter.mSections.clear();
            }
        }
    }

    private void showFullItems() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Transition changeBounds = new ChangeBounds();
            changeBounds.setDuration(300);
            TransitionManager.beginDelayedTransition(list, changeBounds);
        }
        actions = fullMenuItem;
        updateSection();
        adapter.notifyDataSetChanged();
        list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        setListLayout();
    }

    private void showShortItems() {
        actions = menuItem;
        updateSection();
        adapter.notifyDataSetChanged();
        setListLayout();

    }

    @Override
    protected void onStart() {
        super.onStart();
        showShortItems();
    }

    private boolean hasDivider() {
        return adapter.mSections.size() > 0;
    }

    private void setListLayout() {
        // without divider, the height of gridview is correct
        if (!hasDivider())
            return;
        list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    //noinspection deprecation
                    list.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                View lastChild = list.getChildAt(list.getChildCount() - 1);
                if (lastChild != null)
                    list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, lastChild.getBottom() + lastChild.getPaddingBottom() + list.getPaddingBottom()));
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        TypedArray a = getContext().obtainStyledAttributes(new int[]{android.R.attr.layout_width});
        try {

            Configuration mConfiguration = getContext().getResources().getConfiguration(); //获取设置的配置信息
            int ori = mConfiguration.orientation;
            if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
                // 横屏: 限制弹框宽度
                params.width = (int) (PhoneUtil.getInstance(getContext()).getScreenInfo().widthPixels * 0.618f);
            } else {
                params.width = a.getLayoutDimension(0, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            a.recycle();
        }
        super.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyListener != null){
                    keyListener.onKey(dialog,keyCode,event);
                }
                return false;
            }
        });
        super.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                try {
                    if (dismissListener != null) {
                        dismissListener.onDismiss(dialog);
                    }

                    if (limit != Integer.MAX_VALUE) {
                        showShortItems();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        getWindow().setAttributes(params);
    }

    public Menu getMenu() {
        return builder.menu;
    }



    /**
     * If you make any changes to menu and try to apply it immediately to your bottomsheet, you should call this.
     */
    public void invalidate() {
        updateSection();
        adapter.notifyDataSetChanged();
        setListLayout();
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }

    @Override
    public void setOnKeyListener(OnKeyListener onKeyListener) {
        this.keyListener = onKeyListener;
    }

    /**
     * Constructor using a context for this builder and the {@link com} it creates.
     */
    public static class Builder {

        private final Context context;
        private final ActionMenu menu;
        private int theme;

        private CharSequence title;
        private String subTitle;
        private String pic;

        private boolean grid;
        private OnClickListener listener;
        private OnDismissListener dismissListener;
        private OnKeyListener keyListener;
        private Drawable icon;
        private int limit = -1;
        private MenuItem.OnMenuItemClickListener menulistener;

        private View.OnClickListener headerListener;

        private int showType;
        private View customView;
        /**
         * Constructor using a context for this builder and the {@link com.} it creates.
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
         * Constructor using a context for this builder and the {@link com。} it creates with given style
         *
         * @param context A Context for built BottomSheet.
         * @param theme   The theme id will be apply to BottomSheet
         */
        public Builder(Context context, @StyleRes int theme) {
            this.context = context;
            this.theme = theme;
            this.menu = new ActionMenu(context);
        }

        /**
         * Set menu resources as list item to display in BottomSheet
         *
         * @param xmlRes menu resource id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder sheet(@MenuRes int xmlRes) {
            new MenuInflater(context).inflate(xmlRes, menu);
            return this;
        }


        /**
         * Add one item into BottomSheet
         *
         * @param id      ID of item
         * @param iconRes icon resource
         * @param textRes text resource
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder sheet(int id, @DrawableRes int iconRes, @StringRes int textRes) {
            ActionMenuItem item = new ActionMenuItem(context, 0, id, 0, 0, context.getText(textRes));
            item.setIcon(iconRes);
            menu.add(item);
            return this;
        }

        /**
         * Add one item into BottomSheet
         *
         * @param id   ID of item
         * @param icon icon
         * @param text text
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder sheet(int id, @NonNull Drawable icon, @NonNull CharSequence text) {
            ActionMenuItem item = new ActionMenuItem(context, 0, id, 0, 0, text);
            item.setIcon(icon);
            menu.add(item);
            return this;
        }

        /**
         * Add one item without icon into BottomSheet
         *
         * @param id      ID of item
         * @param textRes text resource
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder sheet(int id, @StringRes int textRes) {
            menu.add(0, id, 0, textRes);
            return this;
        }

        /**
         * Add one item without icon into BottomSheet
         *
         * @param id   ID of item
         * @param text text
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder sheet(int id, @NonNull CharSequence text) {
            menu.add(0, id, 0, text);
            return this;
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
         * Remove an item from BottomSheet
         *
         * @param id ID of item
         * @return This Builder object to allow for chaining of calls to set methods
         */
        @Deprecated
        public Builder remove(int id) {
            menu.removeItem(id);
            return this;
        }

        /**
         * Set title for BottomSheet
         *
         * @param icon icon for BottomSheet
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder icon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Set title for BottomSheet
         *
         * @param iconRes icon resource id for BottomSheet
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder icon(@DrawableRes int iconRes) {
            this.icon = context.getResources().getDrawable(iconRes);
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
         * Set OnMenuItemClickListener for BottomSheet
         *
         * @param listener OnMenuItemClickListener for BottomSheet
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder listener(@NonNull MenuItem.OnMenuItemClickListener listener) {
            this.menulistener = listener;
            return this;
        }


        /**
         * Show BottomSheet
         *
         * @return Instance of bottomsheet
         */
        public BottomSheet show() {
            BottomSheet dialog = build();
            dialog.show();
            return dialog;
        }

        /**
         * Show items in grid instead of list
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder grid() {
            this.grid = true;
            return this;
        }

        /**
         * Set initial number of actions which will be shown in current sheet.
         * If more actions need to be shown, a "more" action will be displayed in the last position.
         *
         * @param limitRes resource id for initial number of actions
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder limit(@IntegerRes int limitRes) {
            limit = context.getResources().getInteger(limitRes);
            return this;
        }


        /**
         * Create a BottomSheet but not show it
         *
         * @return Instance of bottomsheet
         */
        @SuppressLint("Override")
        public BottomSheet build() {
            BottomSheet dialog = new BottomSheet(context, theme);
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

        public Builder setOnKeyListener(@NonNull OnKeyListener listener) {
            this.keyListener = listener;
            return this;
        }

        public Builder header(String title, String subTitle, String pic_url) {
            this.title = title;
            this.subTitle = subTitle;
            this.pic = pic_url;
            return this;
        }

        public Builder headerListener(View.OnClickListener listener) {
            this.headerListener = listener;
            return this;
        }

        public Builder showType(int type) {
            this.showType = type;
            return this;
        }
        public Builder setCustomView(View customView) {
            this.customView = customView;
            return this;
        }


    }
    public void setSubTitle(String title){
        if(subTitle != null){
            subTitle.setText(title);
        }
    }


}
