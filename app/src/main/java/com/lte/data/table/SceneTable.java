package com.lte.data.table;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2018/1/4.
 */

public class SceneTable extends RealmObject {
    @PrimaryKey
    private Long id;//@Id必须为Long

    private Long addTime = 0L;

    private Long mBeginMillseconds = 0L;

    private Long mEndMillseconds = 0L;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getmBeginMillseconds() {
        return mBeginMillseconds;
    }

    public void setmBeginMillseconds(Long mBeginMillseconds) {
        this.mBeginMillseconds = mBeginMillseconds;
    }

    public Long getmEndMillseconds() {
        return mEndMillseconds;
    }

    public void setmEndMillseconds(Long mEndMillseconds) {
        this.mEndMillseconds = mEndMillseconds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "SceneTable{" +
                "id=" + id +
                ", addTime=" + addTime +
                ", mBeginMillseconds=" + mBeginMillseconds +
                ", mEndMillseconds=" + mEndMillseconds +
                ", name='" + name + '\'' +
                '}';
    }
}
