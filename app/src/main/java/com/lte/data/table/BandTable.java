package com.lte.data.table;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2018/3/8.
 */

public class BandTable extends RealmObject {

    @PrimaryKey
    private Long id = null;//@Id必须为Long

    private String name;

    private Long time = 0L;

    private RealmList<RealmInteger> point = new RealmList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<RealmInteger> getPoint() {
        return point;
    }

    public void setPoint(RealmList<RealmInteger> point) {
        this.point = point;
    }
}
