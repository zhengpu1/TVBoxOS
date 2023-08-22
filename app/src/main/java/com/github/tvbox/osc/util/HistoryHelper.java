package com.github.tvbox.osc.util;

public class HistoryHelper {
    private static Integer[] hisNumArray = {30,50,70};
    private static Integer[] hisNumArrayx = {5,8,10,12,15,18,20,22,25};

    public static final String getHistoryNumName(int index){
        Integer value = getHisNum(index);
        return value + "条";
    }

    public static final String getHistoryNumNamex(int index){
        Integer value = getHisNumx(index);
        return value + "";
    }

    public static final int getHisNum(int index){
        Integer value = null;
        if(index>=0 && index < hisNumArray.length){
            value = hisNumArray[index];
        }else{
            value = hisNumArray[0];
        }
        return value;
    }
    public static final int getHisNumx(int index){
        Integer value = null;
        if(index>=0 && index < hisNumArrayx.length){
            value = hisNumArrayx[index];
        }else{
            value = hisNumArrayx[0];
        }
        return value;
    }
}
