package com.github.tvbox.osc.util;

import com.github.tvbox.osc.bean.VodInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 剧集分组工具类
 */
public class EpisodeGroupHelper {

    /**
     * 分组结果：key为分组flag，value为该分组的剧集列表
     */
    public static class GroupResult {
        public List<VodInfo.VodSeriesFlag> groupFlags;
        public LinkedHashMap<VodInfo.VodSeriesFlag, List<VodInfo.VodSeries>> groupMap;

        public GroupResult(List<VodInfo.VodSeriesFlag> groupFlags, LinkedHashMap<VodInfo.VodSeriesFlag, List<VodInfo.VodSeries>> groupMap) {
            this.groupFlags = groupFlags;
            this.groupMap = groupMap;
        }
    }

    /**
     * 剧集分组
     * @param allSeries 全部剧集
     * @param groupSize 每组多少集
     * @return 分组结果
     */
    public static GroupResult groupEpisodes(List<VodInfo.VodSeries> allSeries, int groupSize) {
        LinkedHashMap<VodInfo.VodSeriesFlag, List<VodInfo.VodSeries>> mapData = new LinkedHashMap<>();
        List<VodInfo.VodSeriesFlag> classifyData = new ArrayList<>();

        int total = allSeries.size();
        int groupCount = (int) Math.ceil(total * 1.0 / groupSize);

        for (int i = 0; i < groupCount; i++) {
            int start = i * groupSize;
            int end = Math.min(start + groupSize, total);
            List<VodInfo.VodSeries> groupList = allSeries.subList(start, end);

            String groupName = (start + 1) + "-" + end;
            VodInfo.VodSeriesFlag flag = new VodInfo.VodSeriesFlag();
            flag.name = groupName;
            flag.selected = (i == 0); // 默认第一个分组选中
            classifyData.add(flag);

            mapData.put(flag, new ArrayList<>(groupList));
        }

        return new GroupResult(classifyData, mapData);
    }
}