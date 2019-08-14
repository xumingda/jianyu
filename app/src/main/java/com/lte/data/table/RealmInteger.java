package com.lte.data.table;

import io.realm.RealmObject;

/**
 * Created by chenxiaojun on 2017/9/14.
 */

public class RealmInteger extends RealmObject {
    private Long id;//@Id必须为Long

    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "RealmInteger{" +
                "id=" + id +
                ", number=" + number +
                '}';
    }
}
