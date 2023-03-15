package cn.net.aicare.modulelibrary.module.airDetector;

/**
 * 闹钟声明
 */
public class AlarmClockStatement {
    /**
     * 0=闹钟不设置不显示, 1=直接显示所有闹钟
     */
    private boolean showIcon;
    /**
     * 闹钟数量(最多）
     */
    private int alarmCount;

    /**
     * 模式 0:一次性,当天有效, (0=不支持该项功能 , 1 =支持该项功能)
     */
    private boolean mode0;

    /**
     * 模式 1:每天都响(0=不支持该项功能 , 1 =支持该项功能)
     */
    private boolean mode1;

    /**
     * 模式 2:周一至周五(0=不支持该项功能 , 1 =支持该项功能)
     */
    private boolean mode2;

    /**
     * 模式 3:周一至周六(0=不支持该项功能 , 1 =支持该项功能)
     */
    private boolean mode3;

    /**
     * 模式 4:自定义,从周一到周日
     * 任意搭配
     */
    private boolean mode4;

    /**
     * 是否支持添加删除功能
     */
    private boolean supportDelete;

    public boolean isShowIcon() {
        return showIcon;
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }

    public int getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(int alarmCount) {
        this.alarmCount = alarmCount;
    }

    public boolean isMode0() {
        return mode0;
    }

    public void setMode0(boolean mode0) {
        this.mode0 = mode0;
    }

    public boolean isMode1() {
        return mode1;
    }

    public void setMode1(boolean mode1) {
        this.mode1 = mode1;
    }

    public boolean isMode2() {
        return mode2;
    }

    public void setMode2(boolean mode2) {
        this.mode2 = mode2;
    }

    public boolean isMode3() {
        return mode3;
    }

    public void setMode3(boolean mode3) {
        this.mode3 = mode3;
    }

    public boolean isMode4() {
        return mode4;
    }

    public void setMode4(boolean mode4) {
        this.mode4 = mode4;
    }

    public boolean isSupportDelete() {
        return supportDelete;
    }

    public void setSupportDelete(boolean supportDelete) {
        this.supportDelete = supportDelete;
    }

    public int getModeCount(){
        int count = 0;
        boolean[] modes = {mode0, mode1, mode2, mode3, mode4};
        for (boolean mode : modes) {
            if (mode) {
                count++;
            }
        }
        return count;
    }
}
