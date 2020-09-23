package com.offcn.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppDateUtils {

    public static String getFormatTime(){
        //创建日期时间格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public static String getFormatTime(String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
      return   dateFormat.format(new Date());
    }

    public static String getFormatTime(String pattern,Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }
}
