package com.github.tvbox.osc.ui.activity;

import com.github.tvbox.osc.bean.VodInfo;

public class PlayEvent {
    private VodInfo vodInfo;
    public boolean forceReplay; // 加这一行
    public PlayEvent(VodInfo vodInfo) {
        this.vodInfo = vodInfo;
    }

    public VodInfo getVodInfo() {
        return vodInfo;
    }
}
