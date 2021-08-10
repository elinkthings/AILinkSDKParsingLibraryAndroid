package cn.net.aicare.modulelibrary.module.RopeSkipping;

import java.util.List;

public interface OnRopeSkipCallBack {


    void onFinish(RopeSkipRecord ropeSkipBean);

    void onBattery(int battery);

    void onCurrentData(int status, int mode, int defaultValue, int currentJumpNum, int currentJumpTime, int batter);

    void onResultTimerAndCountDownNum(int mode, int timer);

    void onResultStatus(int mode, int result);
    void onBindResult( int result);

    void onFinishOffHistory(List<RopeSkipRecord> list);

}