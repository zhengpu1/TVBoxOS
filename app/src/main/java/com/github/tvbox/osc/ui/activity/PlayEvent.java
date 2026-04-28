package com.github.tvbox.osc.ui.activity;

import com.github.tvbox.osc.bean.VodInfo;

public class PlayEvent {
    private VodInfo vodInfo;

    public PlayEvent(VodInfo vodInfo) {
        this.vodInfo = vodInfo;
    }

    public VodInfo getVodInfo() {
        return vodInfo;
    }
}
