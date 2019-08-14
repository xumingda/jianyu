package com.lte.data.table;

import com.lte.data.ScanSet;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/9/14.
 */

public class MobileResultTable extends RealmObject {

    @PrimaryKey
    private Long id;//@Id必须为Long

    private String mobile;

    public MobileResultTable() {
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
