package cn.net.aicare.modulelibrary.module.TempInstrument;

import java.util.ArrayList;
import java.util.List;

/**
 * 测温仪配置信息结构体
 */
public class TempInstrumentDeviceConfigBean {

    /**
     * 支持语言列表,保存的是语言ID
     */
    private List<Long> mLanguageIdTypeList = new ArrayList<>();
    /**
     * 当前语言下标
     */
    private int mCurrentLanguageIndex;

    /**
     * 报警音效ID,保存的是报警音效ID
     */
    private List<Integer> mAlarmSoundList = new ArrayList<>();

    /**
     * 当前报警音效ID
     */
    private int mCurrentAlarmSoundId;


    /**
     * 提示音ID,保存的是提示音ID
     */
    private List<Integer> mBeepList = new ArrayList<>();

    /**
     * 当前提示音ID
     */
    private int mCurrentBeepId;

    /**
     * 音量列表,保存的是音量的等级
     */
    private List<Integer> mVolumeList = new ArrayList<>();
    /**
     * 当前音量等级,0=静音
     */
    private int mCurrentVolumeLevel;

    /**
     * 当前单位
     */
    private int mCurrentUnit;

    /**
     * 测温距离列表,保存的是距离ID
     */
    private List<Integer> mTestTempDistanceList = new ArrayList<>();

    /**
     * 当前测温距离ID
     */
    private int mCurrentTestDistanceId;


    /**
     * 灵敏度列表,保存的是灵敏度的等级
     */
    private List<Integer> mSensitivityList = new ArrayList<>();
    /**
     * 当前灵敏度等级
     */
    private int mCurrentSensitivityLevel;


    /**
     * 自动关机列表,保存的自动关机ID
     */
    private List<Integer> mAutoCloseList = new ArrayList<>();
    /**
     * 当前自动关机ID
     */
    private int mCurrentAutoCloseId;

    /**
     * 当前报警值,单位为℃
     */
    private float mCurrentAlarmTempC;
    /**
     * 当前报警值,单位为℉
     */
    private float mCurrentAlarmTempF;

    /**
     * 自动开机状态,-1=不支持,0=关闭自动开机(默认),1=打开自动开机
     */
    private int mAutoOpen;

    /**
     * 温度值播报,-1=不支持,0=关闭(默认),1=打开
     */
    private int mTempBroadcast;

    /**
     * 体温补偿开关
     */
    private boolean mTempBodyCompensateSwitchC;
    /**
     * 体温补偿的值(+ -) 开关开启后才生效,单位为℃
     */
    private float mTempBodyCompensateValueC;
    /**
     * 体温补偿开关
     */
    private boolean mTempBodyCompensateSwitchF;
    /**
     * 体温补偿的值(+ -) 开关开启后才生效,单位为℉
     */
    private float mTempBodyCompensateValueF;
    /**
     * 物温补偿开关
     */
    private boolean mTempObjectCompensateSwitchC;
    /**
     * 物温补偿的值(+ -) 开关开启后才生效,单位为℃
     */
    private float mTempObjectCompensateValueC;
    /**
     * 物温补偿开关
     */
    private boolean mTempObjectCompensateSwitchF;
    /**
     * 物温补偿的值(+ -) 开关开启后才生效,单位为℉
     */
    private float mTempObjectCompensateValueF;

    /**
     * 报警值下限,单位为℃
     */
    private float mAlarmTempMinC;
    /**
     * 报警值上限,单位为℃
     */
    private float mAlarmTempMaxC;



    public TempInstrumentDeviceConfigBean() {
    }

    public List<Long> getLanguageIdTypeList() {
        return mLanguageIdTypeList;
    }

    public void setLanguageIdTypeList(List<Long> languageIdTypeList) {
        mLanguageIdTypeList = languageIdTypeList;
    }

    public int getCurrentLanguageIndex() {
        return mCurrentLanguageIndex;
    }

    public void setCurrentLanguageIndex(int currentLanguageIndex) {
        mCurrentLanguageIndex = currentLanguageIndex;
    }

    public List<Integer> getAlarmSoundList() {
        return mAlarmSoundList;
    }

    public void setAlarmSoundList(List<Integer> alarmSoundList) {
        mAlarmSoundList = alarmSoundList;
    }

    public int getCurrentAlarmSoundId() {
        return mCurrentAlarmSoundId;
    }

    public void setCurrentAlarmSoundId(int currentAlarmSoundId) {
        mCurrentAlarmSoundId = currentAlarmSoundId;
    }

    public List<Integer> getBeepList() {
        return mBeepList;
    }

    public void setBeepList(List<Integer> beepList) {
        mBeepList = beepList;
    }

    public int getCurrentBeepId() {
        return mCurrentBeepId;
    }

    public void setCurrentBeepId(int currentBeepId) {
        mCurrentBeepId = currentBeepId;
    }

    public List<Integer> getVolumeList() {
        return mVolumeList;
    }

    public void setVolumeList(List<Integer> volumeList) {
        mVolumeList = volumeList;
    }

    public int getCurrentVolumeLevel() {
        return mCurrentVolumeLevel;
    }

    public void setCurrentVolumeLevel(int currentVolumeLevel) {
        mCurrentVolumeLevel = currentVolumeLevel;
    }

    public int getCurrentUnit() {
        return mCurrentUnit;
    }

    public void setCurrentUnit(int currentUnit) {
        mCurrentUnit = currentUnit;
    }

    public List<Integer> getTestTempDistanceList() {
        return mTestTempDistanceList;
    }

    public void setTestTempDistanceList(List<Integer> testTempDistanceList) {
        mTestTempDistanceList = testTempDistanceList;
    }

    public int getCurrentTestDistanceId() {
        return mCurrentTestDistanceId;
    }

    public void setCurrentTestDistanceId(int currentTestDistanceId) {
        mCurrentTestDistanceId = currentTestDistanceId;
    }

    public float getCurrentAlarmTempC() {
        return mCurrentAlarmTempC;
    }

    public void setCurrentAlarmTempC(float currentAlarmTempC) {
        mCurrentAlarmTempC = currentAlarmTempC;
    }

    public float getCurrentAlarmTempF() {
        return mCurrentAlarmTempF;
    }

    public void setCurrentAlarmTempF(float currentAlarmTempF) {
        mCurrentAlarmTempF = currentAlarmTempF;
    }

    public int getAutoOpen() {
        return mAutoOpen;
    }

    public void setAutoOpen(int autoOpen) {
        mAutoOpen = autoOpen;
    }

    public int getTempBroadcast() {
        return mTempBroadcast;
    }

    public void setTempBroadcast(int tempBroadcast) {
        mTempBroadcast = tempBroadcast;
    }

    public boolean isTempBodyCompensateSwitchC() {
        return mTempBodyCompensateSwitchC;
    }

    public void setTempBodyCompensateSwitchC(boolean tempBodyCompensateSwitchC) {
        mTempBodyCompensateSwitchC = tempBodyCompensateSwitchC;
    }

    public float getTempBodyCompensateValueC() {
        return mTempBodyCompensateValueC;
    }

    public void setTempBodyCompensateValueC(float tempBodyCompensateValueC) {
        mTempBodyCompensateValueC = tempBodyCompensateValueC;
    }

    public float getTempBodyCompensateValueF() {
        return mTempBodyCompensateValueF;
    }

    public void setTempBodyCompensateValueF(float tempBodyCompensateValueF) {
        mTempBodyCompensateValueF = tempBodyCompensateValueF;
    }

    public boolean isTempObjectCompensateSwitchC() {
        return mTempObjectCompensateSwitchC;
    }

    public void setTempObjectCompensateSwitchC(boolean tempObjectCompensateSwitchC) {
        mTempObjectCompensateSwitchC = tempObjectCompensateSwitchC;
    }

    public float getTempObjectCompensateValueC() {
        return mTempObjectCompensateValueC;
    }

    public void setTempObjectCompensateValueC(float tempObjectCompensateValueC) {
        mTempObjectCompensateValueC = tempObjectCompensateValueC;
    }

    public float getTempObjectCompensateValueF() {
        return mTempObjectCompensateValueF;
    }

    public void setTempObjectCompensateValueF(float tempObjectCompensateValueF) {
        mTempObjectCompensateValueF = tempObjectCompensateValueF;
    }

    public boolean isTempBodyCompensateSwitchF() {
        return mTempBodyCompensateSwitchF;
    }

    public void setTempBodyCompensateSwitchF(boolean tempBodyCompensateSwitchF) {
        mTempBodyCompensateSwitchF = tempBodyCompensateSwitchF;
    }

    public boolean isTempObjectCompensateSwitchF() {
        return mTempObjectCompensateSwitchF;
    }

    public void setTempObjectCompensateSwitchF(boolean tempObjectCompensateSwitchF) {
        mTempObjectCompensateSwitchF = tempObjectCompensateSwitchF;
    }

    public float getAlarmTempMinC() {
        return mAlarmTempMinC;
    }

    public void setAlarmTempMinC(float alarmTempMinC) {
        mAlarmTempMinC = alarmTempMinC;
    }

    public float getAlarmTempMaxC() {
        return mAlarmTempMaxC;
    }

    public void setAlarmTempMaxC(float alarmTempMaxC) {
        mAlarmTempMaxC = alarmTempMaxC;
    }



    public List<Integer> getSensitivityList() {
        return mSensitivityList;
    }

    public void setSensitivityList(List<Integer> sensitivityList) {
        mSensitivityList = sensitivityList;
    }

    public int getCurrentSensitivityLevel() {
        return mCurrentSensitivityLevel;
    }

    public void setCurrentSensitivityLevel(int currentSensitivityLevel) {
        mCurrentSensitivityLevel = currentSensitivityLevel;
    }

    public List<Integer> getAutoCloseList() {
        return mAutoCloseList;
    }

    public void setAutoCloseList(List<Integer> autoCloseList) {
        mAutoCloseList = autoCloseList;
    }

    public int getCurrentAutoCloseId() {
        return mCurrentAutoCloseId;
    }

    public void setCurrentAutoCloseId(int currentAutoCloseId) {
        mCurrentAutoCloseId = currentAutoCloseId;
    }

    @Override
    public TempInstrumentDeviceConfigBean clone()  {
        TempInstrumentDeviceConfigBean bean=new TempInstrumentDeviceConfigBean();
        bean.setLanguageIdTypeList(mLanguageIdTypeList);
        bean.setCurrentLanguageIndex(mCurrentLanguageIndex);
        bean.setAlarmSoundList(mAlarmSoundList);
        bean.setCurrentAlarmSoundId(mCurrentAlarmSoundId);
        bean.setVolumeList(mVolumeList);
        bean.setCurrentVolumeLevel(mCurrentVolumeLevel);
        bean.setCurrentUnit(mCurrentUnit);
        bean.setTestTempDistanceList(mTestTempDistanceList);
        bean.setCurrentTestDistanceId(mCurrentTestDistanceId);
        bean.setCurrentAlarmTempC(mCurrentAlarmTempC);
        bean.setCurrentAlarmTempF(mCurrentAlarmTempF);
        bean.setAutoOpen(mAutoOpen);
        bean.setTempBodyCompensateSwitchC(mTempBodyCompensateSwitchC);
        bean.setTempBodyCompensateValueC(mTempBodyCompensateValueC);
        bean.setTempBodyCompensateSwitchF(mTempBodyCompensateSwitchF);
        bean.setTempBodyCompensateValueF(mTempBodyCompensateValueF);
        bean.setTempObjectCompensateSwitchC(mTempObjectCompensateSwitchC);
        bean.setTempObjectCompensateValueC(mTempObjectCompensateValueC);
        bean.setTempObjectCompensateSwitchF(mTempObjectCompensateSwitchF);
        bean.setTempObjectCompensateValueF(mTempObjectCompensateValueF);
        bean.setTempBroadcast(mTempBroadcast);
        bean.setBeepList(mBeepList);
        bean.setCurrentBeepId(mCurrentBeepId);
        bean.setAlarmTempMaxC(mAlarmTempMaxC);
        bean.setAlarmTempMaxC(mAlarmTempMaxC);
        bean.setSensitivityList(mSensitivityList);
        bean.setCurrentSensitivityLevel(mCurrentSensitivityLevel);
        bean.setAutoCloseList(mAutoCloseList);
        bean.setCurrentAutoCloseId(mCurrentAutoCloseId);
        return bean;
    }
}
