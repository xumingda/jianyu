package com.lte.data.table;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2018/3/8.
 */

public class BbuTable extends RealmObject {

    @PrimaryKey
    private Long id = null;//@Id必须为Long

    private String bbu;
}
