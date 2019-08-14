package com.lte.ui.widget.menu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.App;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.BlackListTable;
import com.lte.https.MobileQuery;
import com.lte.tcpserver.TcpManager;
import com.lte.ui.activity.BlackListActivity;
import com.lte.ui.activity.HttpSetActivity;
import com.lte.ui.activity.WhiteListActivity;
import com.lte.ui.listener.OnOperItemClickL;
import com.lte.ui.listener.QueryListener;
import com.lte.ui.widget.CommonToast;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.NormalListDialog;
import com.lte.utils.AppUtils;
import com.lte.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 */
public abstract class MenuItemView extends MenuBuild {
    private MenuAttribute attribute;
    private final String TAG = "MenuItem";
    private AtomicReference<String> localProgressFlag;
    private boolean isSucces;

    public MenuItemView(Context context) {
        super(context);
    }

    @Override
    protected List<MenuDataItem> onCreateItems() {
        return getMenuData(attribute.type);
    }

    @Override
    protected MenuAttribute getMenuAttribute() {
        return attribute;
    }

    /**
     * 显示菜单
     *
     * @param showTitle 是否显示title
     * @param view      插入的自定义View
     */
    public void showMenu(boolean showTitle, View view) {
        attribute = initAttribute();
        if (attribute == null) {
            Log.e(TAG, "showMenu is ERROR, attribute is NULL");
            return;
        }
        String title = null;
        if (showTitle) title = attribute.musicName;
        super.showMenu(title, view);
    }

    /**
     * 显示菜单
     *
     * @param showTitle     是否显示title
     * @param view          插入的自定义View
     * @param onKeylistener
     */
    public void showMenu(boolean showTitle, View view, DialogInterface.OnKeyListener onKeylistener) {
        attribute = initAttribute();
        if (attribute == null) {
            Log.e(TAG, "showMenu is ERROR, attribute is NULL");
            return;
        }
        String title = null;
        if (showTitle) title = attribute.musicName;
        super.showMenu(title, view, onKeylistener);
    }

    private List<MenuDataItem> getMenuData(int type) {
        List<MenuDataItem> data = new ArrayList<MenuDataItem>();
//        if (type == 2) {//自建歌单
//            data.add(new MenuDataItem(R.drawable.menu_delete_selected, "删除", 9));
//            return data;
        if (type == 1) {
            if (attribute.imsiDataTable.getMobile() == null) {
                data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "解析手机号", 6));
            }
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "加入黑名单", 1));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "加入白名单", 2));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "打开定位", 8));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "取消定位", 9));
        } else if (type == 2) {
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "解除黑名单", 3));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "修改黑名单", 20));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "打开定位", 8));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "取消定位", 9));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "清空黑名单", 10));
        } else if (type == 3) {
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "解除白名单", 4));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "修改白名单", 21));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "打开定位", 8));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "取消定位", 9));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "清空白名单", 11));
        } else if (type == 4) {
            if (attribute.imsiDataTable.getMobile() == null) {
                data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "解析手机号", 6));
            }
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "解除黑名单", 5));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "打开定位", 8));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "取消定位", 9));
        } else if (type == 5) {
            if (attribute.imsiDataTable.getMobile() == null) {
                data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "解析手机号", 6));
            }
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "解除白名单", 7));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "打开定位", 8));
            data.add(new MenuDataItem(R.drawable.icon_arrow_down_sign, "取消定位", 9));
        }
        return data;
    }

    @Override
    protected void onItemClick(int itemId) {
        switch (itemId) {
            case 1:
                if (context != null && attribute.imsiDataTable != null) {
                    DataManager.getInstance().addBlack(attribute.imsiDataTable);
                }
                break;
            case 2:
                if (attribute.imsiDataTable != null) {
                    DataManager.getInstance().addWhite(attribute.imsiDataTable);
                }
                break;
            case 3:
                if (attribute.blackListTable != null) {
                    DataManager.getInstance().deleteBlack(attribute.blackListTable);
                }
                break;
            case 4:
                if (attribute.whiteListTable != null) {
                    DataManager.getInstance().deleteWhite(attribute.whiteListTable);
                }
                break;
            case 5:
                if (attribute.imsiDataTable != null) {
                    DataManager.getInstance().deleteBlack(attribute.imsiDataTable);
                }
                break;
            case 6:
                if (!attribute.imsiDataTable.getImsi().startsWith("460") || attribute.imsiDataTable.getImsi().startsWith("46003") || attribute.imsiDataTable.getImsi().startsWith("46005")
                        || attribute.imsiDataTable.getImsi().startsWith("46011")) {
                    CommonToast.show(context, R.string.error_tips);
                    return;
                }
                isSucces = false;
                MobileQuery.getInstance().queryMobile(attribute.imsiDataTable, new QueryListener() {
                    @Override
                    public void onSuccess() {
                        try {
                            DialogManager.closeDialog(localProgressFlag.get());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        CommonToast.show(context, R.string.query_succes);
                        isSucces = true;
                    }

                    @Override
                    public void onFail() {
                        if (!isSucces) {
                            try {
                                DialogManager.closeDialog(localProgressFlag.get());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            CommonToast.show(context, R.string.query_fail);
                        }
                    }
                });
                localProgressFlag = new AtomicReference<>();
                localProgressFlag.set(DialogManager.showProgressDialog(context, "查询中，请稍候..",true));
                break;
            case 7:
                if (attribute.imsiDataTable != null) {
                    DataManager.getInstance().deleteWhite(attribute.imsiDataTable);
                }
                break;
            case 8:
                if (attribute.imsiDataTable != null) {
                    DataManager.getInstance().addBlack(attribute.imsiDataTable);
                    ArrayList<String> item = new ArrayList<>();
                    item.add(attribute.imsiDataTable.getImsi());
                    App.get().selectImsi = item;
//                    if( App.get().selectImsi.size()==0) {
//                        App.get().selectImsi = item;
//                    }
//                    else{
//                        App.get().selectImsi.add(attribute.imsiDataTable.getImsi());
//                    }
                    TcpManager.getInstance().setPositionOn();
                } else if (attribute.blackListTable != null) {
                    ArrayList<String> item = new ArrayList<>();
                    item.add(attribute.blackListTable.getImsi());
                    App.get().selectImsi = item;
//                    if( App.get().selectImsi.size()==0) {
//                        App.get().selectImsi = item;
//                    }
//                    else{
//                        App.get().selectImsi.add(attribute.imsiDataTable.getImsi());
//                    }
                    TcpManager.getInstance().setPositionOn();
                }
                break;
            case 9:
                if (attribute.imsiDataTable != null) {
                    DataManager.getInstance().deleteBlack(attribute.imsiDataTable);
                    //TcpManager.getInstance().setPositionOFF();
                    ArrayList<String> item = new ArrayList<>();
                    item.add(attribute.imsiDataTable.getImsi());
                    App.get().selectCloseImsi=item;
                    TcpManager.getInstance().setPositionOFF();

                } else if (attribute.blackListTable != null) {
                    //TcpManager.getInstance().setPositionOFF();
                    ArrayList<String> item = new ArrayList<>();
                    item.add(attribute.blackListTable.getImsi());
                    App.get().selectCloseImsi=item;
                    TcpManager.getInstance().setPositionOFF();
                }
                break;
            case 10:
                DataManager.getInstance().clearBlack();
                break;
            case 11:
                DataManager.getInstance().clearWhite();
                break;

            case 20:
                //修改黑名单
                if (attribute.blackListTable != null) {
                    //DataManager.getInstance().deleteBlack(attribute.blackListTable);

                    LinearLayout container = new LinearLayout(this.context);
                    container.setOrientation(LinearLayout.VERTICAL);

                    final LinearLayout line=new LinearLayout(this.context);
                    final EditText txtInput = new EditText(this.context);
                    final TextView txtLabel=new TextView(this.context);
                    line.setOrientation(LinearLayout.HORIZONTAL);
                    line.addView(txtLabel);
                    line.addView(txtInput);
                    container.addView(line);
                    ((LinearLayout.LayoutParams) line.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);


                    ((LinearLayout.LayoutParams) txtLabel.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入imsi");
                    txtInput.setText(attribute.blackListTable.getImsi());
                    txtInput.setSingleLine();
                    txtInput.setTextSize(16);
                    txtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel.setText("IMSI:");
                    txtLabel.setSingleLine();
                    txtLabel.setTextSize(16);
                    txtLabel.setGravity(Gravity.CENTER_VERTICAL);

                    final LinearLayout line2=new LinearLayout(this.context);
                    final EditText txtInput2 = new EditText(this.context);
                    final TextView txtLabel2=new TextView(this.context);
                    line2.setOrientation(LinearLayout.HORIZONTAL);
                    line2.addView(txtLabel2);
                    line2.addView(txtInput2);
                    container.addView(line2);
                    ((LinearLayout.LayoutParams) line2.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line2.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line2.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line2.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);

                    ((LinearLayout.LayoutParams) txtLabel2.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel2.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel2.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput2.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput2.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput2.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入imei");
                    txtInput2.setText(attribute.blackListTable.getImei());
                    txtInput2.setSingleLine();
                    txtInput2.setTextSize(16);
                    txtInput2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel2.setText("IMEI:");
                    txtLabel2.setSingleLine();
                    txtLabel2.setTextSize(16);
                    txtLabel2.setGravity(Gravity.CENTER_VERTICAL);


                    final LinearLayout line3=new LinearLayout(this.context);
                    final EditText txtInput3 = new EditText(this.context);
                    final TextView txtLabel3=new TextView(this.context);
                    line3.setOrientation(LinearLayout.HORIZONTAL);
                    line3.addView(txtLabel3);
                    line3.addView(txtInput3);
                    container.addView(line3);
                    ((LinearLayout.LayoutParams) line3.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line3.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line3.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line3.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);


                    ((LinearLayout.LayoutParams) txtLabel3.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel3.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel3.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput3.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput3.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput3.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput3.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入手机号");
                    txtInput3.setText(attribute.blackListTable.getMobile());
                    txtInput3.setSingleLine();
                    txtInput3.setTextSize(16);
                    txtInput3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel3.setText("手机号:");
                    txtLabel3.setSingleLine();
                    txtLabel3.setTextSize(16);
                    txtLabel3.setGravity(Gravity.CENTER_VERTICAL);



                    final LinearLayout line4=new LinearLayout(this.context);
                    final EditText txtInput4 = new EditText(this.context);
                    final TextView txtLabel4=new TextView(this.context);
                    line4.setOrientation(LinearLayout.HORIZONTAL);
                    line4.addView(txtLabel4);
                    line4.addView(txtInput4);
                    container.addView(line4);
                    ((LinearLayout.LayoutParams) line4.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line4.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line4.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line4.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);


                    ((LinearLayout.LayoutParams) txtLabel4.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel4.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel4.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput4.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput4.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput4.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput4.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入姓名");
                    txtInput4.setText(attribute.blackListTable.getPhoneUsername());
                    txtInput4.setSingleLine();
                    txtInput4.setTextSize(16);
                    txtInput4.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel4.setText("姓名:");
                    txtLabel4.setSingleLine();
                    txtLabel4.setTextSize(16);
                    txtLabel4.setGravity(Gravity.CENTER_VERTICAL);




                    final LinearLayout line5=new LinearLayout(this.context);
                    final EditText txtInput5 = new EditText(this.context);
                    final TextView txtLabel5=new TextView(this.context);
                    line5.setOrientation(LinearLayout.HORIZONTAL);
                    line5.addView(txtLabel5);
                    line5.addView(txtInput5);
                    container.addView(line5);
                    ((LinearLayout.LayoutParams) line5.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line5.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line5.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line5.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);


                    ((LinearLayout.LayoutParams) txtLabel5.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel5.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel5.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput5.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput5.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput5.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput5.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入部职别");
                    txtInput5.setText(attribute.blackListTable.getPhoneUsername());
                    txtInput5.setSingleLine();
                    txtInput5.setTextSize(16);
                    txtInput5.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel5.setText("部职别:");
                    txtLabel5.setSingleLine();
                    txtLabel5.setTextSize(16);
                    txtLabel5.setGravity(Gravity.CENTER_VERTICAL);


                    DialogManager.showDialog(this.context, "黑名单修改", container, "确定", new DialogManager.IClickListener() {
                        public boolean click(Dialog dlg, View view) {
                            //String newTitle = txtInput.getText().toString();

                            String preImsi=attribute.blackListTable.getImsi();
                            String newTitle = txtInput.getText().toString();
                            String newTitle2 = txtInput2.getText().toString();
                            String newTitle3 = txtInput3.getText().toString();
                            String newTitle4 = txtInput4.getText().toString();
                            String newTitle5 = txtInput5.getText().toString();


                            if ((newTitle == null || newTitle.trim().length() == 0) && (newTitle2 == null || newTitle2.trim().length() == 0 && (newTitle3 == null || newTitle3.trim().length() == 0))) {
                                AppUtils.showToast(context, "还没有输入imsi或imei");
                                return false;
                            }
                            AppUtils.hideInputKeyboard(context, txtInput);


                            updateBlacklist(preImsi,newTitle, newTitle2, newTitle3,newTitle4,newTitle5);

                            return true;
                        }
                    }, "取消", new DialogManager.IClickListener() {
                        public boolean click(Dialog dlg, View view) {
                            AppUtils.hideInputKeyboard(context, txtInput);
                            return true;
                        }
                    }, null);

                }
                break;
            case 21:
                //修改白名单
                if (attribute.whiteListTable != null) {
                    //DataManager.getInstance().deleteWhite(attribute.whiteListTable);
                    LinearLayout container = new LinearLayout(this.context);
                    container.setOrientation(LinearLayout.VERTICAL);

                    final LinearLayout line=new LinearLayout(this.context);
                    final EditText txtInput = new EditText(this.context);
                    final TextView txtLabel=new TextView(this.context);
                    line.setOrientation(LinearLayout.HORIZONTAL);
                    line.addView(txtLabel);
                    line.addView(txtInput);
                    container.addView(line);
                    ((LinearLayout.LayoutParams) line.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);


                    ((LinearLayout.LayoutParams) txtLabel.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入imsi");
                    txtInput.setText(attribute.whiteListTable.getImsi());
                    txtInput.setSingleLine();
                    txtInput.setTextSize(16);
                    txtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel.setText("IMSI:");
                    txtLabel.setSingleLine();
                    txtLabel.setTextSize(16);
                    txtLabel.setGravity(Gravity.CENTER_VERTICAL);

                    final LinearLayout line2=new LinearLayout(this.context);
                    final EditText txtInput2 = new EditText(this.context);
                    final TextView txtLabel2=new TextView(this.context);
                    line2.setOrientation(LinearLayout.HORIZONTAL);
                    line2.addView(txtLabel2);
                    line2.addView(txtInput2);
                    container.addView(line2);
                    ((LinearLayout.LayoutParams) line2.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line2.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line2.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line2.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);

                    ((LinearLayout.LayoutParams) txtLabel2.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel2.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel2.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput2.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput2.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput2.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入imei");
                    txtInput2.setText(attribute.whiteListTable.getImei());
                    txtInput2.setSingleLine();
                    txtInput2.setTextSize(16);
                    txtInput2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel2.setText("IMEI:");
                    txtLabel2.setSingleLine();
                    txtLabel2.setTextSize(16);
                    txtLabel2.setGravity(Gravity.CENTER_VERTICAL);


                    final LinearLayout line3=new LinearLayout(this.context);
                    final EditText txtInput3 = new EditText(this.context);
                    final TextView txtLabel3=new TextView(this.context);
                    line3.setOrientation(LinearLayout.HORIZONTAL);
                    line3.addView(txtLabel3);
                    line3.addView(txtInput3);
                    container.addView(line3);
                    ((LinearLayout.LayoutParams) line3.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line3.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line3.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line3.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);


                    ((LinearLayout.LayoutParams) txtLabel3.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel3.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel3.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput3.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput3.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput3.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput3.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入手机号");
                    txtInput3.setText(attribute.whiteListTable.getMobile());
                    txtInput3.setSingleLine();
                    txtInput3.setTextSize(16);
                    txtInput3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel3.setText("手机号:");
                    txtLabel3.setSingleLine();
                    txtLabel3.setTextSize(16);
                    txtLabel3.setGravity(Gravity.CENTER_VERTICAL);



                    final LinearLayout line4=new LinearLayout(this.context);
                    final EditText txtInput4 = new EditText(this.context);
                    final TextView txtLabel4=new TextView(this.context);
                    line4.setOrientation(LinearLayout.HORIZONTAL);
                    line4.addView(txtLabel4);
                    line4.addView(txtInput4);
                    container.addView(line4);
                    ((LinearLayout.LayoutParams) line4.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line4.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line4.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line4.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);


                    ((LinearLayout.LayoutParams) txtLabel4.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel4.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel4.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput4.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput4.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput4.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput4.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入姓名");
                    txtInput4.setText(attribute.whiteListTable.getPhoneUsername());
                    txtInput4.setSingleLine();
                    txtInput4.setTextSize(16);
                    txtInput4.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel4.setText("姓名:");
                    txtLabel4.setSingleLine();
                    txtLabel4.setTextSize(16);
                    txtLabel4.setGravity(Gravity.CENTER_VERTICAL);




                    final LinearLayout line5=new LinearLayout(this.context);
                    final EditText txtInput5 = new EditText(this.context);
                    final TextView txtLabel5=new TextView(this.context);
                    line5.setOrientation(LinearLayout.HORIZONTAL);
                    line5.addView(txtLabel5);
                    line5.addView(txtInput5);
                    container.addView(line5);
                    ((LinearLayout.LayoutParams) line5.getLayoutParams()).topMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line5.getLayoutParams()).bottomMargin = ViewUtil.dip2px(this.context, 10);
                    ((LinearLayout.LayoutParams) line5.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    ((LinearLayout.LayoutParams) line5.getLayoutParams()).height = ViewUtil.dip2px(this.context, 55);


                    ((LinearLayout.LayoutParams) txtLabel5.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtLabel5.getLayoutParams()).weight =1;
                    ((LinearLayout.LayoutParams) txtLabel5.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    ((LinearLayout.LayoutParams) txtInput5.getLayoutParams()).width=0;
                    ((LinearLayout.LayoutParams) txtInput5.getLayoutParams()).weight =3;
                    ((LinearLayout.LayoutParams) txtInput5.getLayoutParams()).height = ViewGroup.LayoutParams.MATCH_PARENT;

                    txtInput5.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtInput.setHint("请输入部职别");
                    txtInput5.setText(attribute.whiteListTable.getPhoneUsername());
                    txtInput5.setSingleLine();
                    txtInput5.setTextSize(16);
                    txtInput5.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                    txtLabel5.setText("部职别:");
                    txtLabel5.setSingleLine();
                    txtLabel5.setTextSize(16);
                    txtLabel5.setGravity(Gravity.CENTER_VERTICAL);


                    DialogManager.showDialog(this.context, "白名单设置", container, "确定", new DialogManager.IClickListener() {
                        public boolean click(Dialog dlg, View view) {
                            //String newTitle = txtInput.getText().toString();

                            String preImsi=attribute.whiteListTable.getImsi();
                            String newTitle = txtInput.getText().toString();
                            String newTitle2 = txtInput2.getText().toString();
                            String newTitle3 = txtInput3.getText().toString();
                            String newTitle4=txtInput4.getText().toString();
                            String newTitle5=txtInput5.getText().toString();
                            if ((newTitle == null || newTitle.trim().length() == 0) && (newTitle2 == null || newTitle2.trim().length() == 0 && (newTitle3 == null || newTitle3.trim().length() == 0))) {
                                AppUtils.showToast(context, "还没有输入imsi或imei");
                                return false;
                            }
                            AppUtils.hideInputKeyboard(context, txtInput);

                            updateWhitelist(preImsi,newTitle, newTitle2, newTitle3,newTitle4,newTitle5);

                            return true;
                        }
                    }, "取消", new DialogManager.IClickListener() {
                        public boolean click(Dialog dlg, View view) {
                            AppUtils.hideInputKeyboard(context, txtInput);
                            return true;
                        }
                    }, null);

                }
                break;
            default:
                break;
        }
    }


    private void updateBlacklist(String preImsi,String imsi, String imei, String mobile,String phoneUsername,String position) {
        DataManager.getInstance().BlackUpdate(preImsi,imsi, imei, mobile,phoneUsername,position);

    }

    private void updateWhitelist(String preImsi,String imsi, String imei, String mobile,String phoneUsername,String position) {
        DataManager.getInstance().WhiteUpdate(preImsi,imsi, imei, mobile,phoneUsername,position);
    }






    /**
     * 删除功能
     */
    protected void onDelItem() {
    }

    /**
     * 执行其他操作(可在MenuAttribute的otherTag中自行定义类型
     */
    protected void onOtherItem(int otherType) {
    }

    ;

    protected abstract MenuAttribute initAttribute();

    @Override
    protected void onItemEnable(MenuDataItem item, MenuItem itemView) {
    }

}
