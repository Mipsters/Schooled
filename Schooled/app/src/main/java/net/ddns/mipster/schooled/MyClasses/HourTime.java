package net.ddns.mipster.schooled.MyClasses;


import java.util.Calendar;


public class HourTime
{
    public static int SECONDS_IN_MINUTE = 60;
    public static int MINUTES_IN_HOUR = 60;
    public static int HOURS_IN_DAY = 24;

    private int second;
    private int minute;
    private int hour;


    public HourTime()
    {
        Calendar now = Calendar.getInstance();

        setTotalSeconds(getTotalSeconds(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND)));
    }

    public HourTime(int seconds)
    {
        hour   = seconds / SECONDS_IN_MINUTE / MINUTES_IN_HOUR % HOURS_IN_DAY;
        minute = seconds / SECONDS_IN_MINUTE % MINUTES_IN_HOUR;
        second = seconds % SECONDS_IN_MINUTE;
    }

    public HourTime(int hour, int minute)
    {
        this.hour = hour;
        this.minute = minute;
        second = 0;
    }

    public HourTime(int hour, int minute, int second)
    {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getSecond(){
        return second;
    }

    public void setSecond(int second){
        if (second < SECONDS_IN_MINUTE && second >= 0)
            this.second = second;
        else
            throw new IllegalArgumentException("there are 0 to " + (SECONDS_IN_MINUTE - 1) + " seconds in a minute");
    }

    public int getMinute(){
        return second;
    }

    public void setMinute(int minute){
        if (second < MINUTES_IN_HOUR && minute >= 0)
            this.minute = minute;
        else
            throw new IllegalArgumentException("there are 0 to " + (MINUTES_IN_HOUR - 1) + " seconds in a minute");
    }

    public int getHour(){
        return second;
    }

    public void setHour(int hour){
        if (hour < HOURS_IN_DAY && hour >= 0)
            this.hour = hour;
        else
            throw new IllegalArgumentException("there are 0 to " + (HOURS_IN_DAY - 1) + " seconds in a minute");
    }

    public int getTotalSeconds()
    {
        return getHour() * MINUTES_IN_HOUR * SECONDS_IN_MINUTE + getMinute() * SECONDS_IN_MINUTE + getSecond();
    }

    public static int getTotalSeconds(int hour, int minute, int second){
        return hour * MINUTES_IN_HOUR * SECONDS_IN_MINUTE + minute * SECONDS_IN_MINUTE + second;
    }

    public void setTotalSeconds(int totalSeconds){
        hour   = totalSeconds / SECONDS_IN_MINUTE / MINUTES_IN_HOUR % HOURS_IN_DAY;
        minute = totalSeconds / SECONDS_IN_MINUTE % MINUTES_IN_HOUR;
        second = totalSeconds % SECONDS_IN_MINUTE;
    }

    public boolean isNow()
    {
        Calendar now = Calendar.getInstance();

        return now.get(Calendar.HOUR_OF_DAY) == hour &&
               now.get(Calendar.MINUTE) == minute &&
               now.get(Calendar.SECOND) == second;
    }

    public boolean isInRange(HourTime ht1, HourTime ht2){
        return (ht1.getTotalSeconds() >= getTotalSeconds() && ht2.getTotalSeconds() <= getTotalSeconds()) ||
               (ht1.getTotalSeconds() <= getTotalSeconds() && ht2.getTotalSeconds() >= getTotalSeconds());
    }

    public static boolean isNowInRange(HourTime ht1, HourTime ht2){
        Calendar now = Calendar.getInstance();

        int nowSec = getTotalSeconds(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));

        return (ht1.getTotalSeconds() >= nowSec && nowSec >= ht2.getTotalSeconds()) ||
               (ht2.getTotalSeconds() >= nowSec && nowSec >= ht1.getTotalSeconds());
    }

    public void add(HourTime ht){
        setTotalSeconds(getTotalSeconds() + ht.getTotalSeconds());
    }

    public static HourTime add(HourTime ht1, HourTime ht2)
    {
        return new HourTime(ht1.getTotalSeconds() + ht2.getTotalSeconds());
    }

    public void decrease(HourTime ht){
        int sec = getTotalSeconds() - ht.getTotalSeconds();

        if (sec < 0)
            sec += HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE;

        setTotalSeconds(sec);
    }

    public static HourTime decrease(HourTime ht1, HourTime ht2)
    {
        int sec = ht1.getTotalSeconds() - ht2.getTotalSeconds();

        if (sec < 0)
            sec += HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE;

        return new HourTime(sec);
    }

    public static HourTime parse(String str) throws ParseException
    {
        String[] arg = str.split(":");

        switch (arg.length){
            case 3:
                return new HourTime(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]), Integer.parseInt(arg[2]));
            case 2:
                return new HourTime(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));
            default:
                throw new ParseException("The String is not in the format \"HH:MM:SS\"");
        }
    }

    public static boolean isParsable(String str)
    {
        try
        {
            parse(str);
        }
        catch(ParseException e)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return (getHour()   < 10 ? "0" : "") + getHour()   + ':' +
               (getMinute() < 10 ? "0" : "") + getMinute() + ':' +
               (getSecond() < 10 ? "0" : "") + getSecond();
    }

    public static class ParseException extends Exception {
        public ParseException(String msg){
            super(msg);
        }
    }
}