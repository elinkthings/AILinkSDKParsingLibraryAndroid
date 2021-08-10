package cn.net.aicare.modulelibrary.module.RopeSkipping;


import java.util.List;

public class RopeSkipRecord {


    private long createTime;
    private Long deviceId;
    private Long id;
    private Long subUserId;
    private Long appUserId;
    private String timeDay;
    private String timeMoth;
    private Integer skipModel;
    private Integer skipModelValue;
    private Integer totalNum;
    private Integer totalTime;
    private Integer totalCal;
    private Integer stopNum;
    private Integer maxNum;
    private Integer maxLoopNum;
    private Integer avgNum;
    private List<TripRopeBean> stopDetail;
    private String allDetailJSON2;
    private String allDetailJSON;

    public RopeSkipRecord() {
    }

    public RopeSkipRecord(long createTime) {
        this.createTime = createTime;
    }

    public RopeSkipRecord(long createTime, Long deviceId, Long id, Long subUserId, Long appUserId, String timeDay, String timeMoth, Integer skipModel, Integer skipModelValue, Integer totalNum, Integer totalTime, Integer totalCal, Integer stopNum, Integer maxNum, Integer maxLoopNum, Integer avgNum, List<TripRopeBean> stopDetail, String allDetailJSON2, String allDetailJSON) {
        this.createTime = createTime;
        this.deviceId = deviceId;
        this.id = id;
        this.subUserId = subUserId;
        this.appUserId = appUserId;
        this.timeDay = timeDay;
        this.timeMoth = timeMoth;
        this.skipModel = skipModel;
        this.skipModelValue = skipModelValue;
        this.totalNum = totalNum;
        this.totalTime = totalTime;
        this.totalCal = totalCal;
        this.stopNum = stopNum;
        this.maxNum = maxNum;
        this.maxLoopNum = maxLoopNum;
        this.avgNum = avgNum;
        this.stopDetail = stopDetail;
        this.allDetailJSON2 = allDetailJSON2;
        this.allDetailJSON = allDetailJSON;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubUserId() {
        return subUserId;
    }

    public void setSubUserId(Long subUserId) {
        this.subUserId = subUserId;
    }

    public Long getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(Long appUserId) {
        this.appUserId = appUserId;
    }

    public String getTimeDay() {
        return timeDay;
    }

    public void setTimeDay(String timeDay) {
        this.timeDay = timeDay;
    }

    public String getTimeMoth() {
        return timeMoth;
    }

    public void setTimeMoth(String timeMoth) {
        this.timeMoth = timeMoth;
    }

    public Integer getSkipModel() {
        return skipModel;
    }

    public void setSkipModel(Integer skipModel) {
        this.skipModel = skipModel;
    }

    public Integer getSkipModelValue() {
        return skipModelValue;
    }

    public void setSkipModelValue(Integer skipModelValue) {
        this.skipModelValue = skipModelValue;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getTotalCal() {
        return totalCal;
    }

    public void setTotalCal(Integer totalCal) {
        this.totalCal = totalCal;
    }

    public Integer getStopNum() {
        return stopNum;
    }

    public void setStopNum(Integer stopNum) {
        this.stopNum = stopNum;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public Integer getMaxLoopNum() {
        return maxLoopNum;
    }

    public void setMaxLoopNum(Integer maxLoopNum) {
        this.maxLoopNum = maxLoopNum;
    }

    public Integer getAvgNum() {
        return avgNum;
    }

    public void setAvgNum(Integer avgNum) {
        this.avgNum = avgNum;
    }

    public List<TripRopeBean> getStopDetail() {
        return stopDetail;
    }

    public void setStopDetail(List<TripRopeBean> stopDetail) {
        this.stopDetail = stopDetail;
    }

    public String getAllDetailJSON2() {
        return allDetailJSON2;
    }

    public void setAllDetailJSON2(String allDetailJSON2) {
        this.allDetailJSON2 = allDetailJSON2;
    }

    public String getAllDetailJSON() {
        return allDetailJSON;
    }

    public void setAllDetailJSON(String allDetailJSON) {
        this.allDetailJSON = allDetailJSON;
    }


    @Override
    public String toString() {
        return
                "时间=" + createTime/1000 +
                ", 跳绳模式=" + skipModel +
                "\n 模式默认值=" + skipModelValue +
                ", 总个数=" + totalNum +
                ", 总时间=" + totalTime +
                "\n最快频次=" + maxNum +
                ", 平均频次=" + avgNum;
    }
}
