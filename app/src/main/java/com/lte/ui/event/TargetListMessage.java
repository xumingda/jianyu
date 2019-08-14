package com.lte.ui.event;

import com.lte.data.TargetBean;

/**
 * @createAuthor zfb
 * @createTime 2017/3/22${Time}
 * @describe ${}
 */

public class TargetListMessage {
    public final TargetBean targetBean;

    public final boolean clear;

    public TargetListMessage(TargetBean targetBean,boolean clear) {
        this.targetBean = targetBean;
        this.clear = clear;
    }


}
