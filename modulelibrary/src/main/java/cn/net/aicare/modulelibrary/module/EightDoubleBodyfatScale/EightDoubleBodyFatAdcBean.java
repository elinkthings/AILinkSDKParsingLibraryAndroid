package cn.net.aicare.modulelibrary.module.EightDoubleBodyfatScale;

/**
 * 双频八电极体脂秤阻抗数据
 *
 * @author xing
 * @date 2024/03/14
 */
public class EightDoubleBodyFatAdcBean {


    /**
     * 频率
     * 0x01:(20kHz )
     * 0x02:(100kHz)
     */
    private int frequencyId;
    /**
     * 左手阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcLeftHand = 0xFFFFFFFFL;
    /**
     * 右手阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcRightHand = 0xFFFFFFFFL;
    /**
     * 左脚阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcLeftFoot = 0xFFFFFFFFL;
    /**
     * 右脚阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcRightFoot = 0xFFFFFFFFL;
    /**
     * 躯干阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcBody = 0xFFFFFFFFL;
    /**
     * 算法Id
     */
    private int algorithmsId;


    public EightDoubleBodyFatAdcBean() {
    }

    public EightDoubleBodyFatAdcBean(int frequencyId, long adcLeftHand, long adcRightHand, long adcLeftFoot, long adcRightFoot, long adcBody, int algorithmsId) {
        this.frequencyId = frequencyId;
        this.adcLeftHand = adcLeftHand;
        this.adcRightHand = adcRightHand;
        this.adcLeftFoot = adcLeftFoot;
        this.adcRightFoot = adcRightFoot;
        this.adcBody = adcBody;
        this.algorithmsId = algorithmsId;
    }

    public int getFrequencyId() {
        return frequencyId;
    }

    public void setFrequencyId(int frequencyId) {
        this.frequencyId = frequencyId;
    }

    public long getAdcLeftHand() {
        return adcLeftHand;
    }

    public void setAdcLeftHand(long adcLeftHand) {
        this.adcLeftHand = adcLeftHand;
    }

    public long getAdcRightHand() {
        return adcRightHand;
    }

    public void setAdcRightHand(long adcRightHand) {
        this.adcRightHand = adcRightHand;
    }

    public long getAdcLeftFoot() {
        return adcLeftFoot;
    }

    public void setAdcLeftFoot(long adcLeftFoot) {
        this.adcLeftFoot = adcLeftFoot;
    }

    public long getAdcRightFoot() {
        return adcRightFoot;
    }

    public void setAdcRightFoot(long adcRightFoot) {
        this.adcRightFoot = adcRightFoot;
    }

    public long getAdcBody() {
        return adcBody;
    }

    public void setAdcBody(long adcBody) {
        this.adcBody = adcBody;
    }

    public int getAlgorithmsId() {
        return algorithmsId;
    }

    public void setAlgorithmsId(int algorithmsId) {
        this.algorithmsId = algorithmsId;
    }

    @Override
    public String toString() {
        return "加密阻抗:" + " \n算法ID=" + algorithmsId + " \n频率ID=" + frequencyId + " \n左手=" + adcLeftHand + " \n右手=" + adcRightHand + " \n左脚=" + adcLeftFoot + " \n右脚=" + adcRightFoot + " \n躯干=" + adcBody;
    }
}
