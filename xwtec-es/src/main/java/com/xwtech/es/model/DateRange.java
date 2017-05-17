package com.xwtech.es.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangq on 2017/3/10.
 */
public class DateRange {

    DateEnum dateEnum;

    public DateRange(Date start, Date end) {

        this.dateEnum = DateEnum.Custom;
        this.start = start;
        this.end = end;
        init();

    }

    public DateRange(DateEnum dateEnum) {
        this.dateEnum = dateEnum;
        init();
    }


    Date start;

    Date end;


    long interval;

    String format;

    public final static long SECOND = 1000;

    public final static long MINUTE = 1000 * 60;

    public final static long HOUR = 1000 * 60 * 60;

    public final static long DAY = 1000 * 60 * 60 * 24;


    void init() {
        Calendar calendar = Calendar.getInstance();
        switch (this.dateEnum) {
            case OneHours:
                end = calendar.getTime();
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                start = calendar.getTime();
                break;
            case SixHours:
                end = calendar.getTime();
                calendar.add(Calendar.HOUR_OF_DAY, -6);
                start = calendar.getTime();
                break;
            case TwelveHours:
                end = calendar.getTime();
                calendar.add(Calendar.HOUR_OF_DAY, -12);
                start = calendar.getTime();
                break;
            case OneDays:
                end = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                start = calendar.getTime();
                break;
            case SevenDays:
                end = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                start = calendar.getTime();
                break;
            case FourteenDays:
                end = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, -14);
                start = calendar.getTime();
                break;
            default:
                break;

        }

        long time = end.getTime() - start.getTime();
        if (time <= HOUR) {
            long m = time / MINUTE;
            if (m < 0) {
                m = 1;
            }
            this.interval = SECOND * m;
            format = "yyyy-MM-dd HH:mm:ss";
        }
        else if (time <= DAY) {
            this.interval = MINUTE * (time / HOUR);
            format = "yyyy-MM-dd HH:mm";
        } else {
            interval = HOUR *  (time / DAY) ;
            format = "yyyy-MM-dd HH";
        }

    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
