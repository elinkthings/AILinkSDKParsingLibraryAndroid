package cn.net.aicare.modulelibrary.module.airDetector;

import java.util.ArrayList;

/**
 * 闹钟信息
 *
 * @author yesp
 */
public class AlarmClockInfoList {

    private ArrayList<AlarmInfo> alarmList;

    public ArrayList<AlarmInfo> getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(ArrayList<AlarmInfo> alarmList) {
        this.alarmList = alarmList;
    }

    public AlarmClockInfoList(ArrayList<AlarmInfo> alarmList) {
        this.alarmList = alarmList;
    }

    public static class AlarmInfo {
        /**
         * 编号
         */
        private int id;
        /**
         * 状态: 0=关闭 , 1=启动
         */
        private int switchStatus;
        /**
         * 是否删除：true=删除
         */
        private boolean deleted;
        /**
         * 模式:0-15
         */
        private int mode;
        /**
         * 时:0-23
         */
        private int hour;
        /**
         * 分:0-59
         */
        private int minute;
        /**
         * 7天【1,1,1,1,1,0,0】 0=改天闹钟不响  1=改天闹钟响
         */
        private int[] alarmDays;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSwitchStatus() {
            return switchStatus;
        }

        public void setSwitchStatus(int switchStatus) {
            this.switchStatus = switchStatus;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public int[] getAlarmDays() {
            return alarmDays;
        }

        public void setAlarmDays(int[] alarmDays) {
            this.alarmDays = alarmDays;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }
    }
}
