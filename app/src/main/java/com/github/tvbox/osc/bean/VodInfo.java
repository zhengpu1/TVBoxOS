package com.github.tvbox.osc.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */
public class VodInfo implements Serializable {
    public String last;//时间
    //内容id
    public String id;
    //父级id
    public int tid;
    //父级id
    public int tyid;
    //影片名称 <![CDATA[老爸当家]]>
    public String name;
    //类型名称
    public String type;
    //视频分类zuidam3u8,zuidall
    public String dt;
    //图片
    public String pic;
    //图片
    public String picthumb;
    //图片
    public String picslide;
    //语言
    public String lang;
    //地区
    public String area;
    //介绍
    public String weekday;
    //评分
    public String score;
    //豆瓣
    public String douban;
    //编剧
    public String writer;
    //年份
    public int year;
    //vip
    public int isend;
    public String state;
    //描述集数或者影片信息<![CDATA[共40集]]>
    public String note;
    //演员<![CDATA[张国立,蒋欣,高鑫,曹艳艳,王维维,韩丹彤,孟秀,王新]]>
    public String actor;
    //导演<![CDATA[陈国星]]>
    public String director;
    public ArrayList<VodSeriesFlag> seriesFlags;
    public LinkedHashMap<String, List<VodSeries>> seriesMap;
    public String des;// <![CDATA[权来]
    public String playFlag = null;
    public int playIndex = 0;
    public String playNote = "";
    public String sourceKey;
    public String playerCfg = "";
    public boolean reverseSort = false;
    public String vodlist;
    public String sourceName;//视频来源

    @Override
    public String toString() {
        return "VodInfo{" +
                "last='" + last + '\'' +
                ", id='" + id + '\'' +
                ", tid=" + tid +
                ", tyid='" + tyid + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", dt='" + dt + '\'' +
                ", pic='" + pic + '\'' +
                ", picthumb='" + picthumb + '\'' +
                ", picslide='" + picslide + '\'' +
                ", lang='" + lang + '\'' +
                ", area='" + area + '\'' +
                ", year=" + year +
                ", state='" + state + '\'' +
                ", note='" + note + '\'' +
                ", actor='" + actor + '\'' +
                ", weekday='" + weekday + '\'' +
                ", score='" + score + '\'' +
                ", douban='" + douban + '\'' +
                ", writer='" + writer + '\'' +
                ", isend='" + isend + '\'' +
                ", director='" + director + '\'' +
                ", seriesFlags=" + seriesFlags +
                ", seriesMap=" + seriesMap +
                ", des='" + des + '\'' +
                ", playFlag='" + playFlag + '\'' +
                ", playIndex=" + playIndex +
                ", playNote='" + playNote + '\'' +
                ", sourceKey='" + sourceKey + '\'' +
                ", playerCfg='" + playerCfg + '\'' +
                ", vodlist='" + vodlist + '\'' +
                ", reverseSort=" + reverseSort +
                '}';
    }

    public void setVideo(Movie.Video video) {
        last = video.last;
        id = video.id;
        tid = video.tid;
        tyid = video.tyid;
        name = video.name;
        type = video.type;
        // dt = video.dt;
        pic = video.pic;
        picthumb = video.picthumb;
        picslide = video.picslide;
        lang = video.lang;
        area = video.area;
        weekday = video.weekday;
        score = video.score;
        douban = video.douban;
        writer = video.writer;
        year = video.year;
        isend = video.isend;
        state = video.state;
        note = video.note;
        actor = video.actor;
        director = video.director;
        vodlist = video.vodlist;
        des = video.des;
        if (video.urlBean != null && video.urlBean.infoList != null && video.urlBean.infoList.size() > 0) {
            LinkedHashMap<String, List<VodSeries>> tempSeriesMap = new LinkedHashMap<>();
            seriesFlags = new ArrayList<>();
            for (Movie.Video.UrlBean.UrlInfo urlInfo : video.urlBean.infoList) {
                if (urlInfo.beanList != null && urlInfo.beanList.size() > 0) {
                    List<VodSeries> seriesList = new ArrayList<>();
                    for (Movie.Video.UrlBean.UrlInfo.InfoBean infoBean : urlInfo.beanList) {
                        seriesList.add(new VodSeries(infoBean.name, infoBean.url));
                    }
                    tempSeriesMap.put(urlInfo.flag, seriesList);
                    seriesFlags.add(new VodSeriesFlag(urlInfo.flag));
                }
            }

            seriesMap = new LinkedHashMap<>();
            for (VodSeriesFlag flag : seriesFlags) {
                seriesMap.put(flag.name, tempSeriesMap.get(flag.name));
            }
        }
    }

    public void reverse() {
        Set<String> flags = seriesMap.keySet();
        for (String flag : flags) {
            Collections.reverse(seriesMap.get(flag));
        }
    }

    //takagen99
    public boolean isSeriesEmpty(){
        return seriesMap == null ? true : seriesMap.isEmpty();
    }

    public List<VodSeries> getFlagSeries(String playFlag){
        List<VodSeries> list = null;
        if(!isSeriesEmpty()){
            list = seriesMap.get(playFlag);
        }else{
            list = new ArrayList<>();
        }
        return list;
    }

    public boolean isFlagSeriesEmpty(String playFlag){
        List<VodSeries> list = getFlagSeries(playFlag);
        return list.isEmpty();
    }

    public VodSeries getVodSeries(String playFlag, int playIndex){
        VodSeries vodSeries = null;
        List<VodSeries> list = getFlagSeries(playFlag);
        if(list != null && !list.isEmpty()){
            if(playIndex >= 0 && playIndex < list.size()){
                vodSeries = list.get(playIndex);
            }
        }
        return vodSeries;
    }

    public static class VodSeriesFlag implements Serializable {

        public String name;
        public String url;
        public boolean selected;

        public VodSeriesFlag() {

        }

        public VodSeriesFlag(String name) {
            this.name = name;
        }
    }

    public static class VodSeries implements Serializable {

        public String name;
        public String url;
        public boolean selected;

        public VodSeries() {
        }

        public VodSeries(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }
}