package cn.net.aicare.modulelibrary.module.LeapWatch;

/**
 * 手表闹钟
 */
public class LeapWatchAlarmBean {
    private int mId;
    private boolean mIsOpen;
    private int[] mDayFlag;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    public LeapWatchAlarmBean(int id, boolean isOpen, int[] dayFlag, int year, int month, int day, int hour, int minute) {
        mId = id;
        mIsOpen = isOpen;
        mDayFlag = dayFlag;
        mYear = year;
        mMonth = month;
        mDay = day;
        mHour = hour;
        mMinute = minute;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public boolean isOpen() {
        return mIsOpen;
    }

    public void setOpen(boolean open) {
        mIsOpen = open;
    }

    public int[] getDayFlag() {
        return mDayFlag;
    }

    public void setDayFlag(int[] dayFlag) {
        mDayFlag = dayFlag;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }
}
