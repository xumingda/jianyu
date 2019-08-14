package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.listeners.OnSingleWheelListener;
import cn.addapp.pickers.picker.SinglePicker;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textviewConfig;
    private Button buttonConfig;
    private Button buttonselect;
    private Button buttonselect3;
    private Button buttonconfig3;
    final int configdataMax=-40;
    final int configdataMin=-120;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    interface myInterface{
        void fun1();
        void fun2();
    }

    private myInterface MyInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textviewConfig=(TextView)findViewById(R.id.textedit_config);
        buttonConfig=(Button)findViewById(R.id.button_config);
        buttonConfig.setOnClickListener(this);
        buttonselect=(Button)findViewById(R.id.textedit_config2);
        buttonselect.setOnClickListener(this);
        buttonselect3=(Button)findViewById(R.id.textedit_config3);
        buttonselect3.setOnClickListener(this);
        buttonconfig3=(Button)findViewById(R.id.button_config3);
        buttonconfig3.setOnClickListener(this);
        preferences=getPreferences(MODE_PRIVATE);
        editor=preferences.edit();

        buttonselect3.setText(preferences.getString("text3","-60"));

        Realm mRealm = Realm.getDefaultInstance(); // opens "myrealm.realm"
        try {
            User user;
            // ... Do something ...
            mRealm.beginTransaction();
            user = mRealm.createObject(User.class); // Create a new object
            user.setName("John2");
            user.setAge(20);
            mRealm.commitTransaction();


            mRealm.beginTransaction();
            user = mRealm.createObject(User.class); // Create a new object
            user.setName("John");
            user.setAge(21);
            mRealm.commitTransaction();


        } finally {
            mRealm.close();
        }


        mRealm = Realm.getDefaultInstance(); // opens "myrealm.realm"
        try {
            User user;
            // ... Do something ...
            mRealm.beginTransaction();
            user = mRealm.createObject(User.class); // Create a new object
            user.setName("John");
            user.setAge(21);
            mRealm.commitTransaction();
        } finally {
            mRealm.close();
        }

        RealmResults<User> userList = mRealm.where(User.class).findAll();

        RealmResults<User> list2= userList.where().equalTo("name","John").findAll();

        Toast.makeText(this,"count"+list2.size(),Toast.LENGTH_SHORT).show();



        initData();
        initView();
    }
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new RecycleViewAdapter(getData());
    }
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.myrecycle); // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager); // 设置adapter
        mRecyclerView.setAdapter(mAdapter);
    }
    private ArrayList<String> getData() {
        ArrayList<String> data = new ArrayList<>();
        String temp = " item";
        for(int i = 0; i < 20; i++) {
            data.add(i + temp);
        } return data;
    }








    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm realm = Realm.getDefaultInstance(); // opens "myrealm.realm"
        realm.close();
    }

    private int checkConfigdata()
    {
        int result=0;
        String str=textviewConfig.getText().toString();
        if(str.isEmpty())
        {
            Toast.makeText(this,"请输入-120至-40的整数",Toast.LENGTH_SHORT).show();
            return 0;
        }
        int number=Integer.parseInt(textviewConfig.getText().toString());
        if(number>configdataMax)
        {
            number=configdataMax;
            Toast.makeText(this,"请输入-120至-40的整数",Toast.LENGTH_SHORT).show();
        }
        if(number<configdataMin)
        {
            number=configdataMin;
            Toast.makeText(this,"请输入-120至-40的整数",Toast.LENGTH_SHORT).show();
        }
        result=number+(number&0x01);
        textviewConfig.setText(Integer.toString(result));
        return result;
    }



       @Override
    public void onClick(View v) {
        //Toast.makeText(this,"hello",Toast.LENGTH_SHORT).show();
        switch (v.getId())
        {
            case R.id.button_config:
                int setresult=checkConfigdata();
                Toast.makeText(this,"配置结果为："+setresult,Toast.LENGTH_SHORT).show();
                mytest x=new mytest( new myInterface() {
                    @Override
                    public void fun1() {
                        Toast.makeText(MainActivity.this,"fun1",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void fun2() {
                        Toast.makeText(MainActivity.this,"fun1",Toast.LENGTH_SHORT).show();
                    }
                });

                x.myInterfaceTest();




                break;
            case R.id.textedit_config2:
//                new NumberPickerDialog(
//                        this,
//                        new NumberPicker.OnValueChangeListener() {
//                            @Override
//                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                                Log.e("ard", "所选值：" + picker.getValue() + "，原值：" + oldVal + "，新值：" + newVal); // 新值即所选值
//                                buttonselect.setText(Integer.toString(newVal));
//                            }
//                        },
//                        -40, // 最大值
//                        -120, // 最小值
//                        -80) // 默认值
//                        .setCurrentValue(-60) // 更新默认值
//                        .show();

                String[] items=new String[41];
                for(int i=0;i<41;i++)
                {
                    items[i]=Integer.toString(-(40+i*2));
                }
                new StringPickerDialog(
                        this,
                        new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                Log.e("ard", "所选值：" + picker.getValue() + "，原值：" + oldVal + "，新值：" + newVal); // 新值即所选值
                                buttonselect.setText(picker.getDisplayedValues()[newVal]);
                            }
                        },
                        items.length,
                        items,
                        "-80"
                        ) // 默认值
                        .show();

                break;
            case R.id.textedit_config3:
                ArrayList<String> list = new ArrayList<>();
                for(int i = -128;i<-40; i+=2){
                    String s = "";
                    s=Integer.toString(i);
                    list.add(s);
                }
//        String[] ss = (String[]) list.toArray();
                SinglePicker<String> picker = new SinglePicker<>(MainActivity.this, list);
                picker.setCanLoop(false);//不禁用循环
                picker.setLineVisible(true);
                picker.setItemWidth(100);
                picker.setTextSize(25);

                picker.setSelectedIndex(list.indexOf(buttonselect3.getText().toString()));
                //picker.setSelectedItem(textview.getText().toString());
                picker.setWheelModeEnable(false);
                //启用权重 setWeightWidth 才起作用
                picker.setLabel("");
                picker.setWeightEnable(true);
                picker.setWeightWidth(1);
                picker.setSelectedTextColor(Color.GREEN);//前四位值是透明度
                picker.setUnSelectedTextColor(Color.RED);
                picker.setOnSingleWheelListener(new OnSingleWheelListener() {
                    @Override
                    public void onWheeled(int index, String item) {
                        //showToast("index=" + index + ", item=" + item);
                    }
                });
                picker.setOnItemPickListener(new OnItemPickListener<String>() {
                    @Override
                    public void onItemPicked(int index, String item) {
                        //Toast.makeText(MainActivity.this,"index=" + index + ", item=" + item,Toast.LENGTH_SHORT).show();
                        //textview.setText(item);
                        buttonselect3.setText(item);
                        buttonselect3.setTextColor(Color.YELLOW);
                        ((TextView)findViewById(R.id.confitstate3)).setText("待配置");
                    }
                });
                picker.show();
                break;
            case R.id.button_config3:
                ((TextView)findViewById(R.id.confitstate3)).setText("配置中...");
                buttonselect3.setTextColor(Color.GREEN);
                ((TextView)findViewById(R.id.confitstate3)).setText("完成");
                editor.putString("text3",buttonselect3.getText().toString());
                editor.commit();

                break;
            default:
                break;
        }
    }
}
