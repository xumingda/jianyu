package com.lte.ui.widget.menu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.lte.R;
import com.lte.ui.widget.bottomsheet.BottomSheet;
import com.lte.utils.EventHelper;

import java.util.List;

/**
 */
public abstract class MenuBuild {

	protected Context context;

	public MenuBuild(Context context) {
		this.context = context;
	}
	
	/**
	 * 显示从下方弹出的Menu菜单
	 * @param title  Menu上显示的标题
	 * @param customView  自定义View
	 */
	protected void showMenu(CharSequence title, View customView) {
		showMenu(customView,null);
	}
	protected void showMenu(CharSequence title, View customView,DialogInterface.OnKeyListener onKeylistener) {
		showMenu(customView,onKeylistener);
	}
	private BottomSheet bottomSheet;
	private List<MenuDataItem> items;
	private int theme;
	private DialogInterface.OnDismissListener mOnDismissListener = null;

	private void showMenu(View customView, DialogInterface.OnKeyListener onKeylistener) {
		if(EventHelper.isRubbish(context, "bottomsheet_show_click", 1000)){
			return;
		}

		items = onCreateItems();
		if (items == null || items.isEmpty()) {
			return ;
		}

		if (bottomSheet != null && bottomSheet.isShowing()) {
			return ;
		}

		MenuAttribute menuAttribute = getMenuAttribute();

		theme = R.style.BottomSheet_CustomDialog;

		// 歌曲更多
		if(null != customView){
			bottomSheet = new BottomSheet.Builder(context, theme).setCustomView(customView).setOnKeyListener(onKeylistener).listener(new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onItemClick(which);
				}
			}).build();
		} else {
			bottomSheet = new BottomSheet.Builder(context, theme).listener(new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onItemClick(which);
				}
			}).build();

		}

		final Menu menu = bottomSheet.getMenu();

		for (int i = 0; i < items.size(); i++) {
			MenuDataItem item = items.get(i);
			menu.add(Menu.NONE, item.id, Menu.NONE, item.text);
			if (item.bmpid == -1) {
				menu.findItem(item.id).setIcon(null);
			} else {
				menu.findItem(item.id).setIcon(item.bmpid);
			}

			onItemEnable(item, menu.findItem(item.id));
		}
		if (context instanceof Activity && (((Activity) context).isFinishing() || context.isRestricted())) {
			return;
		}
		bottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mOnDismissListener != null) {
					mOnDismissListener.onDismiss(dialog);
				}
			}
		});
		bottomSheet.show();

	}

	protected void refreshMenuItem(MenuDataItem item) {
		Menu menu = bottomSheet.getMenu();
		if(null != menu && menu.size()>0){
			for(int i=0;i<menu.size();i++) {
				if(menu.getItem(i).getItemId() == 16){//评论
					try{
						menu.getItem(i).setTitle(item.text);
						bottomSheet.adapter.notifyDataSetChanged();
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}



	//更换所有数据，刷新所有列表项
	protected void refreshAllItem(List<MenuDataItem> menus){
		if(menus == null || menus.size() == 0){
			return;
		}
		List<MenuDataItem> items = menus;
		try{
			Menu menu = bottomSheet.getMenu();
			menu.clear();
			for (int i = 0; i < items.size(); i++) {
				MenuDataItem item = items.get(i);
				menu.add(Menu.NONE, item.id, Menu.NONE, item.text);
				if (item.bmpid == -1) {
					menu.findItem(item.id).setIcon(null);
				} else {
					menu.findItem(item.id).setIcon(item.bmpid);
				}
				onItemEnable(item, menu.findItem(item.id));
				bottomSheet.adapter.notifyDataSetChanged();
			}
			bottomSheet.setSubTitle("(" + items.size() + ")");
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 取消(菜单被关闭)
	 */
	protected void closeMenu(){}
	
	/**菜单需要展现的功能项列表*/
	protected abstract List<MenuDataItem> onCreateItems();

	/**菜单点击事件*/
	protected abstract void onItemClick(int itemId);

	/**Header点击事件*/
	protected void onHeaderClick(View v) {

	}

	/**菜单是否可被点击*/
	protected abstract void onItemEnable(MenuDataItem item, MenuItem itemView);

	protected abstract MenuAttribute getMenuAttribute();

	public void onDismiss() {
		if (bottomSheet != null) {
			bottomSheet.dismiss();
		}
	}

	public void showProgress() {
		if (bottomSheet != null && bottomSheet.bottomsheetProgress != null) {
			bottomSheet.bottomsheetProgress.setVisibility(View.VISIBLE);
		}
	}

	public boolean isShowingProgress() {
		try {
			if (bottomSheet != null && bottomSheet.bottomsheetProgress != null) {
				return bottomSheet.bottomsheetProgress.getVisibility() == View.VISIBLE;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void closeProgress() {
		if (bottomSheet != null && bottomSheet.bottomsheetProgress != null) {
			bottomSheet.bottomsheetProgress.setVisibility(View.GONE);
		}
	}

	public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
		this.mOnDismissListener = onDismissListener;
	}

}
