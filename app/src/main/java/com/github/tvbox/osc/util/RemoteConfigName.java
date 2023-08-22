package com.github.tvbox.osc.util;

// 远程配置文件名
public class RemoteConfigName extends HawkConfig {
    public static final String IsRecodeLog="IsRecodeLog"; // 是否开启日志记录

    // region 自定义数据
    public static final String CustomData="CustomData"; // 自定义数据

    //远程控制按钮
    public static final String ForceChangeAPIUrl="ForceChangeAPIUrl"; //强制替换首页默认API地址，防止API失效不更新包的手段
    public static final String APIUrl="APIUrl"; // 默认首页API地址
    public static final String APIEPGUrl="APIEPGUrl"; // 默认直播EPG地址
    public static final String CustomData_YOUND="CustomData_YOUND"; // 自定义数据 默认/青年版
    public static final String CustomData_AGED="CustomData_AGED"; // 自定义数据 老年版

    // region 更新相关
    public static final String IsForceUpdate="IsForceUpdate"; // 是否首页强制显示更新
    public static final String UpdateData="UpdateData"; // 更新数据
    public static final String UpdateData_NewVersion="NewVersion"; // 更新数据_新版本号
    public static final String UpdateData_ForceUpdate="ForceUpdate"; // 更新数据_是否强制更新
    public static final String UpdateData_UpdateDesc="UpdateDesc"; // 更新数据_更新描述
    public static final String UpdateData_UpdateDownloadUrl="UpdateDownloadUrl"; // 更新数据_更新下载地址
}
