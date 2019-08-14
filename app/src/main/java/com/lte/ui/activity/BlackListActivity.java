package com.lte.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.App;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.ImsiData;
import com.lte.data.table.BlackListTable;
import com.lte.ui.adapter.BlackAdapter;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.listener.OnOperItemClickL;
import com.lte.ui.widget.BaseAnimatorSet;
import com.lte.ui.widget.BounceTopEnter;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.DialogMenuItem;
import com.lte.ui.widget.NormalListDialog;
import com.lte.ui.widget.SlideBottomExit;
import com.lte.ui.widget.TitleBar;
import com.lte.ui.widget.menu.MenuAttribute;
import com.lte.ui.widget.menu.MenuItemView;
import com.lte.utils.AppUtils;
import com.lte.utils.DateUtils;
import com.lte.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by chenxiaojun on 2017/12/13.
 */

public class BlackListActivity extends BaseActivity implements BlackAdapter.CheckListener {

    private static final int PICKFILE_RESULT_CODE = 1;
    private RecyclerView imsi_list;

    private BlackAdapter mAdapter;

    private TitleBar titleBar;

    private TextView queueImsi;
    private TextView queueUserName;
    private Button queueButton;
    private Button cancelQueueButton;
    private BlackListActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blackandwhite_activity);
        init();
    }

    private void init() {


        EventBus.getDefault().register(this);
        context=this;

        titleBar = (TitleBar) findViewById(R.id.titlebar);

        queueImsi=(TextView)findViewById(R.id.blacklistQueue_IMSI);
        queueUserName=(TextView)findViewById(R.id.blacklistQueue_UserName);
        queueButton=(Button)findViewById(R.id.blacklistQueue);
        cancelQueueButton=(Button)findViewById(R.id.blacklistCancel);


        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imsi_list = (RecyclerView) findViewById(R.id.imsi_list);
//
                mAdapter = new BlackAdapter(context,DataManager.getInstance().queueBlackData(queueImsi.getText().toString(),
                        queueUserName.getText().toString()), context, imsi_list);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

                imsi_list.setLayoutManager(linearLayoutManager);

                imsi_list.setAdapter(mAdapter);

            }
        });

        cancelQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imsi_list = (RecyclerView) findViewById(R.id.imsi_list);
//
                mAdapter = new BlackAdapter(context,DataManager.getInstance().findBlackData(), context, imsi_list);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

                imsi_list.setLayoutManager(linearLayoutManager);

                imsi_list.setAdapter(mAdapter);

            }
        });

        titleBar.setTitle(R.string.black);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.addAction(new TitleBar.TextAction("添加") {
            @Override
            public void performAction(View view) {
                NormalListDialog();
            }
        });

        imsi_list = (RecyclerView) findViewById(R.id.imsi_list);

        mAdapter = new BlackAdapter(this, DataManager.getInstance().findBlackData(), this, imsi_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        imsi_list.setLayoutManager(linearLayoutManager);

        imsi_list.setAdapter(mAdapter);

        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();

        testItems.add(new DialogMenuItem("手动添加"));
        testItems.add(new DialogMenuItem("从SD卡导入"));
    }
    private ArrayList<DialogMenuItem> testItems = new ArrayList<DialogMenuItem>();
    private String[] stringItems = {"手动添加", "从SD卡导入"};
    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;

    private void NormalListDialog() {
        final NormalListDialog dialog = new NormalListDialog(BlackListActivity.this, testItems);
        dialog.title("请选择")//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        LinearLayout container = new LinearLayout(BlackListActivity.this);
                        container.setOrientation(LinearLayout.VERTICAL);
                        final EditText txtInput = new EditText(BlackListActivity.this);
                        container.addView(txtInput);
                        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).topMargin = ViewUtil.dip2px(BlackListActivity.this, 10);
                        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).bottomMargin = ViewUtil.dip2px(BlackListActivity.this, 10);
                        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).height = ViewUtil.dip2px(BlackListActivity.this, 55);
                        txtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtInput.setHint("请输入imsi");
                        txtInput.setSingleLine();
                        txtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                        final EditText txtInput2 = new EditText(BlackListActivity.this);
                        container.addView(txtInput2);
                        ((LinearLayout.LayoutParams) txtInput2.getLayoutParams()).bottomMargin = ViewUtil.dip2px(BlackListActivity.this, 10);
                        ((LinearLayout.LayoutParams) txtInput2.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                        ((LinearLayout.LayoutParams) txtInput2.getLayoutParams()).height = ViewUtil.dip2px(BlackListActivity.this, 55);
                        txtInput2.setHint("请输入imei");
                        txtInput2.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtInput2.setSingleLine();
                        txtInput2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                        final EditText txtInput3 = new EditText(BlackListActivity.this);
                        container.addView(txtInput3);
                        ((LinearLayout.LayoutParams) txtInput3.getLayoutParams()).bottomMargin = ViewUtil.dip2px(BlackListActivity.this, 10);
                        ((LinearLayout.LayoutParams) txtInput3.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                        ((LinearLayout.LayoutParams) txtInput3.getLayoutParams()).height = ViewUtil.dip2px(BlackListActivity.this, 55);
                        txtInput3.setHint("请输入手机号");
                        txtInput3.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtInput3.setSingleLine();
                        txtInput3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});


                        final EditText txtInput4 = new EditText(BlackListActivity.this);
                        container.addView(txtInput4);
                        ((LinearLayout.LayoutParams) txtInput4.getLayoutParams()).bottomMargin = ViewUtil.dip2px(BlackListActivity.this, 10);
                        ((LinearLayout.LayoutParams) txtInput4.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                        ((LinearLayout.LayoutParams) txtInput4.getLayoutParams()).height = ViewUtil.dip2px(BlackListActivity.this, 55);
                        txtInput4.setHint("请输入姓名");
                        txtInput4.setInputType(InputType.TYPE_CLASS_TEXT);
                        txtInput4.setSingleLine();
                        txtInput4.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});


                        final EditText txtInput5 = new EditText(BlackListActivity.this);
                        container.addView(txtInput5);
                        ((LinearLayout.LayoutParams) txtInput5.getLayoutParams()).bottomMargin = ViewUtil.dip2px(BlackListActivity.this, 10);
                        ((LinearLayout.LayoutParams) txtInput5.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
                        ((LinearLayout.LayoutParams) txtInput5.getLayoutParams()).height = ViewUtil.dip2px(BlackListActivity.this, 55);
                        txtInput5.setHint("请输入部职别");
                        txtInput5.setInputType(InputType.TYPE_CLASS_TEXT);
                        txtInput5.setSingleLine();
                        txtInput5.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});


                        DialogManager.showDialog(BlackListActivity.this, "黑名单设置", container, "确定", new DialogManager.IClickListener() {
                            public boolean click(Dialog dlg, View view) {
                                String newTitle = txtInput.getText().toString();
                                String newTitle2 = txtInput2.getText().toString();
                                String newTitle3 = txtInput3.getText().toString();
                                String newTitle4 = txtInput4.getText().toString();
                                String newTitle5 = txtInput5.getText().toString();


                                if ((newTitle == null || newTitle.trim().length() == 0) && (newTitle2 == null || newTitle2.trim().length() == 0 && (newTitle3 == null || newTitle3.trim().length() == 0))) {
                                    AppUtils.showToast(BlackListActivity.this, "还没有输入imsi或imei");
                                    return false;
                                }
                                AppUtils.hideInputKeyboard(BlackListActivity.this, txtInput);

                                saveBlacklist(newTitle, newTitle2, newTitle3,newTitle4,newTitle5);

                                return true;
                            }
                        }, "取消", new DialogManager.IClickListener() {
                            public boolean click(Dialog dlg, View view) {
                                AppUtils.hideInputKeyboard(BlackListActivity.this, txtInput);
                                return true;
                            }
                        }, null);
                        break;
                    case 1:
                        if (isGrantExternalRW(BlackListActivity.this)) {
                            pickFileToImport();
                        } else {
                            Toast.makeText(BlackListActivity.this, "请检查是否开启读写权限", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                dialog.dismiss();
            }
        });
    }

    private void pickFileToImport() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }
    private void pickFileToImport1() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 2);
    }

    @Subscribe
    public void onSystemOut(SystemOutEvent outEvent) {
        if (outEvent.isOut()) {
            this.finish();
        }
    }

    private void saveBlacklist(String imsi, String imei, String mobile,String phoneUsername,String position) {
        DataManager.getInstance().crateOrBlackUpdate(imsi, imei, mobile,phoneUsername,position);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view, final BlackListTable blackListTable) {
        MenuItemView menu = new MenuItemView(BlackListActivity.this) {
            @Override
            protected MenuAttribute initAttribute() {
                MenuAttribute attribute = new MenuAttribute();
                attribute.blackListTable = blackListTable;
                attribute.type = 2;
                return attribute;
            }
        };

        menu.showMenu(false, null);
    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

    String filePath;
    String fileName;
    File file;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("mainXlxs", resultCode + "---" + requestCode);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_opening_file), Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            //获取到选中文件的路径
            filePath = uri.getPath();

            //判断是否是外部打开
            if (filePath.contains("external")) {
                isExternal(uri);
            }
            //获取的是否是真实路径
            if (file == null) {
                isWhetherTruePath(uri);
            }
            //如果前面都获取不到文件，则自己拼接路径
            if (file == null) {
                splicingPath(uri);
            }
            if (file != null) {
                try {
                    readExcel(file.getPath());
                } catch (BiffException e) {
                    Log.d("mainXlxs1", e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("mainXlxs2", e.toString());
                }
            }
//            Uri uri = data.getData();
//            Log.d("mainXlxs", uri.getPath());
//
        }else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_opening_file), Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            //获取到选中文件的路径
            filePath = uri.getPath();

            //判断是否是外部打开
            if (filePath.contains("external")) {
                isExternal(uri);
            }
            //获取的是否是真实路径
            if (file == null) {
                isWhetherTruePath(uri);
            }
            //如果前面都获取不到文件，则自己拼接路径
            if (file == null) {
                splicingPath(uri);
            }
            if (file != null) {
                try {
                    readExcel1(file.getPath());
                } catch (BiffException e) {
                    Log.d("mainXlxs1", e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("mainXlxs2", e.toString());
                }
            }
//            Uri uri = data.getData();
//            Log.d("mainXlxs", uri.getPath());
//
        }
    }

    /**
     * 如果前面两种都获取不到文件
     * 则使用此种方法拼接路径
     * 此方法在Andorid7.0系统中可用
     */
    private void splicingPath(Uri uri) {
        Log.i("hxl", "获取文件的路径filePath=========" + filePath);
        if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {
            Log.i("hxl", "===调用拼接路径方法===" + uri.getPath());
            String string = uri.getPath();
            String a[] = new String[2];
            //判断文件是否在sd卡中
            if (string.indexOf(String.valueOf(Environment.getExternalStorageDirectory())) != -1) {
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getExternalStorageDirectory()));
                //获取到file
                file = new File(Environment.getExternalStorageDirectory(), a[1]);
            } else if (string.indexOf(String.valueOf(Environment.getDataDirectory())) != -1) { //判断文件是否在手机内存中
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getDataDirectory()));
                //获取到file
                file = new File(Environment.getDataDirectory(), a[1]);
            }
//            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            Log.i("hxl", "file=========" + file);
        } else {
            Toast.makeText(BlackListActivity.this, "您选中的文件不是Excel文档", Toast.LENGTH_LONG).show();
        }
    }

    //获取文件的真实路径
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    /**
     * 获取到的路径是否真实路径
     *
     * @param uri
     */
    private void isWhetherTruePath(Uri uri) {
        try {
            Log.i("hxl", "获取文件的路径filePath=========" + filePath);
            if (filePath != null) {
                if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {
                    if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                        filePath = getPath(this, uri);
                        fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                        file = new File(filePath);
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                        Log.i("hxl", "===调用4.4以后系统方法===");
                        filePath = getRealPathFromURI(uri);
                        fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                        file = new File(filePath);
                    } else {//4.4以下系统调用方法
                        filePath = getRealPathFromURI(uri);
                        Log.i("hxl", "===调用4.4以下系统方法===");
                        fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                        file = new File(filePath);
                    }
                } else {
                    Toast.makeText(BlackListActivity.this, "您选中的文件格式不是Excel文档", Toast.LENGTH_LONG).show();
                }
//                Log.i("hxl", "file========="+file);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拿到文件外部路径，通过外部路径遍历出真实路径
     *
     * @param uri
     */
    private void isExternal(Uri uri) {
        Log.i("mainXlxs", "获取文件的路径filePath=========" + filePath);
        Log.i("mainXlxs", "===调用外部遍历出路径方法===");
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = this.managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        file = new File(img_path);
        filePath = file.getAbsolutePath();
        if (!filePath.endsWith(".xlsx") && !filePath.endsWith(".xls")) {
            Toast.makeText(BlackListActivity.this, "您选中的文件不是Excel文档", Toast.LENGTH_LONG).show();
            filePath = null;
            return;
        }
        fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void readExcel(String url) throws BiffException, IOException {
        //创建一个list 用来存储读取的内容
        List list = new ArrayList();
        Workbook rwb = null;
        Cell cell = null;

        //创建输入流
        InputStream stream = new FileInputStream(url);

        //获取Excel文件对象
        rwb = Workbook.getWorkbook(stream);

        //获取文件的指定工作表 默认的第一个
        Sheet sheet = rwb.getSheet(0);

        //行数(表头的目录不需要，从1开始)
        for (int i = 1; i < sheet.getRows(); i++) {

            //创建一个数组 用来存储每一列的值
            String[] str = new String[sheet.getColumns()];
            String imsi = null;
            String imei = null;
            String mobile = null;
            String phoneUsername=null;
            String  position=null;
            //列数
            for (int j = 0; j < sheet.getColumns(); j++) {

                //获取第i行，第j列的值
                cell = sheet.getCell(j, i);
                String data = cell.getContents();

                if (j == 0) {
                    if (data != null) {
                        imsi = data;
                    }
                }
                if (j == 1) {
                    if (data != null) {
                        imei = data;
                    }
                }
                if (j == 2) {
                    if (data != null) {
                        mobile = data;
                    }
                }
                if(j==3){
                    if(data!=null){
                        phoneUsername=data;
                    }
                }

                if(j==4){
                    if(data!=null){
                        position=data;
                    }
                }

            }
            if (imsi != null || imei != null || mobile != null|| phoneUsername != null) {
                saveBlacklist(imsi, imei, mobile,phoneUsername,position);
            }
            //把刚获取的列存入list
//            list.add(str);
        }
//        for (int i = 0; i < list.size(); i++) {
//            String[] str = (String[]) list.get(i);
//            for (String aStr : str) {
//                Log.d("mainXlxs", aStr);
//            }
//        }
    }
    public void readExcel1(String url) throws BiffException, IOException {
        //创建一个list 用来存储读取的内容
        List list = new ArrayList();
        Workbook rwb = null;
        Cell cell = null;

        //创建输入流
        InputStream stream = new FileInputStream(url);

        //获取Excel文件对象
        rwb = Workbook.getWorkbook(stream);

        //获取文件的指定工作表 默认的第一个
        Sheet sheet = rwb.getSheet(0);

        //行数(表头的目录不需要，从1开始)
        for (int i = 1; i < sheet.getRows(); i++) {

            //创建一个数组 用来存储每一列的值
            String[] str = new String[sheet.getColumns()];
//            String imsi = null;
//            String imei = null;
//            String mobile = null;
            ImsiData imsiData = new ImsiData();
            imsiData.setId(++App.get().imsiId);
            //列数
            for (int j = 0; j < sheet.getColumns(); j++) {

                //获取第i行，第j列的值
                cell = sheet.getCell(j, i);
                String data = cell.getContents();

                if (j == 0) {
                    if (data != null) {
                        imsiData.setDeviceId( data);
                    }
                }
                if (j == 1) {
                    if (data != null) {
                        imsiData.setTime(DateUtils.getTime(data));
                    }
                }
                if (j == 2) {
                    if (data != null) {
                        imsiData.setImsi( data);
                    }
                }
                if (j == 3) {
                    if (data != null) {
                        imsiData.setImei( data);
                    }
                }
                if (j == 4) {
                    if (data != null) {
                        imsiData.setMobile( data);
                    }
                }
                if (j == 5) {
                    if (data != null) {
                        imsiData.setAttribuation( data);
                    }
                }

            }
//            if (imsi != null || imei != null || mobile != null) {
//                saveBlacklist(imsi, imei, mobile);
//            }
            EventBus.getDefault().post(new MessageEvent(imsiData));
            //把刚获取的列存入list
//            list.add(str);
        }
//        for (int i = 0; i < list.size(); i++) {
//            String[] str = (String[]) list.get(i);
//            for (String aStr : str) {
//                Log.d("mainXlxs", aStr);
//            }
//        }
    }
}
