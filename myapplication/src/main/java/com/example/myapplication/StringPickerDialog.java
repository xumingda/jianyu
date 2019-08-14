package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import static android.view.View.OVER_SCROLL_NEVER;

/**
 * A dialog that prompts the user for the number using a NumberPicker.<br/>
 * 使用NumberPicker获取数值的对话框
 */
public class StringPickerDialog extends AlertDialog implements DialogInterface.OnClickListener, NumberPicker.OnValueChangeListener {

    private final String maxValue = "最大值";
    private final String minValue = "最小值";
    private final String currentValue = "当前值";

    private final NumberPicker mNumberPicker;
    private final NumberPicker.OnValueChangeListener mCallback;

    private int newVal;
    private int oldVal;

    /**
     * @param context 上下文
     * @param callBack 回调器
     * @param ItemNumber 项目数
     * @param Items 项目列表
     * @param CurrentItem 当前值
     */
    public StringPickerDialog(Context context, NumberPicker.OnValueChangeListener callBack, int ItemNumber,String[] Items, String CurrentItem) {

        super(context, 0);
        mCallback = callBack;
        setIcon(0);
        setTitle("设置数字");

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, "设置", this);
        setButton(BUTTON_NEGATIVE, "取消", this);

        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_string_picker_dialog, null);
        setView(view);
        mNumberPicker = (NumberPicker) view.findViewById(R.id.stringnumberPicker);

        mNumberPicker.setMaxValue(ItemNumber-1);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setDisplayedValues(Items);
        mNumberPicker.setWrapSelectorWheel(false);
        int currentValue=ItemNumber;
        for(int i=0;i<ItemNumber;i++)
        {
            if(Items[i].equals(CurrentItem))
            {
                currentValue=i;
                break;
            }
        }
        mNumberPicker.setValue(currentValue);
        mNumberPicker.setOnValueChangedListener(this);
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(maxValue, mNumberPicker.getMaxValue());
        state.putInt(minValue, mNumberPicker.getMinValue());
        state.putInt(currentValue, mNumberPicker.getValue());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int max = savedInstanceState.getInt(maxValue);
        int min = savedInstanceState.getInt(minValue);
        int cur = savedInstanceState.getInt(currentValue);
        mNumberPicker.setMaxValue(max);
        mNumberPicker.setMinValue(min);
        mNumberPicker.setValue(cur);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        this.oldVal = oldVal;
        this.newVal = newVal;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                mCallback.onValueChange(mNumberPicker, oldVal, newVal);
                break;
        }
    }

    /**
     * <b>功能</b>: setCurrentValue，设置NumberPicker的当前值<br/>
     * @author : weiyou.com <br/>
     * @return
     */
    public StringPickerDialog setCurrentValue(int value){
        mNumberPicker.setValue(value);
        return this;
    }
}