package me.goldze.mvvmhabit.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    static String dayNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static String getNewChatTime(long timesamp,boolean isDetail) {
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timesamp);
        String timeFormat="M月d日 HH:mm";
        String yearTimeFormat="yyyy年M月d日 HH:mm";
        String am_pm="";
        int hour=otherCalendar.get(Calendar.HOUR_OF_DAY);
        if(hour>=0&&hour<6){
            am_pm="凌晨";
        }else if(hour>=6&&hour<12){
            am_pm="早上";
        }else if(hour==12){
            am_pm="中午";
        }else if(hour>12&&hour<18){
            am_pm="下午";
        }else if(hour>=18){
            am_pm="晚上";
        }
        if(isDetail){
            timeFormat = "M月d日 "+ am_pm +"HH:mm";
            yearTimeFormat = "yyyy年M月d日 "+ am_pm +"HH:mm";
        }else{
            timeFormat = "M月d日";
            yearTimeFormat = "yyyy年M月d日";
        }
        boolean yearTemp = todayCalendar.get(Calendar.YEAR)==otherCalendar.get(Calendar.YEAR);
        if(yearTemp){
            int todayMonth=todayCalendar.get(Calendar.MONTH);
            int otherMonth=otherCalendar.get(Calendar.MONTH);
            if(todayMonth==otherMonth){//表示是同一个月
                int temp=todayCalendar.get(Calendar.DATE)-otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = getHourAndMin(timesamp);
                        break;
                    case 1:
                        if(isDetail){
                            result = "昨天 " + getHourAndMin(timesamp);
                        }else{
                            result = "昨天";
                        }
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth=todayCalendar.get(Calendar.WEEK_OF_MONTH);
                        if(dayOfMonth==todayOfMonth){//表示是同一周
                            int dayOfWeek=otherCalendar.get(Calendar.DAY_OF_WEEK);
                            if(dayOfWeek!=1){//判断当前是不是星期日   如想显示为：周日 12:09 可去掉此判断
                                if(isDetail){
                                    result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK)-1] + " " + getHourAndMin(timesamp);
                                }else{
                                    result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK)-1];
                                }
                            }else{
                                result = getTime(timesamp,timeFormat);
                            }
                        }else{
                            result = getTime(timesamp,timeFormat);
                        }
                        break;
                    default:
                        result = getTime(timesamp,timeFormat);
                        break;
                }
            }else{
                result = getTime(timesamp,timeFormat);
            }
        }else{
            result = getYearTime(timesamp,yearTimeFormat);
        }
        return result;
    }

    private static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    private static String getTime(long time,String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    private static String getYearTime(long time,String yearTimeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
        return format.format(new Date(time));
    }
}
