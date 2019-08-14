package com.example.myapplication;

import android.widget.Toast;

public class mytest {
    private MainActivity.myInterface myInterface;
    mytest(MainActivity.myInterface Interface1){
        myInterface=Interface1;
    }

    void myInterfaceTest()
    {
        myInterface.fun1();
    }
}
