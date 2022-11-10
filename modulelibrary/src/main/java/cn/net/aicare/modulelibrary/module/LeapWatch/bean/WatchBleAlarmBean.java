package cn.net.aicare.modulelibrary.module.LeapWatch.bean;

import java.util.Arrays;

/**
 * 手表闹钟
 */
public class WatchBleAlarmBean {
    private int mId;
    private boolean mIsOpen;
    /**
     * int[1]-0=周1关
     * int[1]-1=周1开
     * int[2]-0=周2关
     * int[2]-1=周2开
     */
    private int[] mDayFlag;
    private int mHour;
    private int mMinute;

    /**
     * 闹钟名称(标签)
     */
    private String mTitle;

    public WatchBleAlarmBean(int mId) {
        this.mId = mId;
    }

    public WatchBleAlarmBean(int id, boolean isOpen, int[] dayFlag, int hour, int minute, String title) {
        mId = id;
        mIsOpen = isOpen;
        mDayFlag = dayFlag;
        mHour = hour;
        mMinute = minute;
        mTitle = title;
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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public String toString() {
        return "WatchBleAlarmBean{" + "mId=" + mId + ", mIsOpen=" + mIsOpen + ", mDayFlag=" + Arrays
                .toString(mDayFlag) + ", mHour=" + mHour + ", mMinute=" + mMinute + ", mTitle='" + mTitle + '\'' + '}';
    }
}
