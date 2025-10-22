package cn.net.aicare.modulelibrary.module.EightBodyfatscale;

/**
 * 单频八电极体脂秤阻抗数据
 *
 * @author xing
 * @date 2024/03/14
 */
public class EightOneBodyFatAdcBean {

    /**
     * 未测试的默认阻抗
     */
    public final static long UNTESTED = 0xFFFFFFFFL;

    /**
     * 阻抗双手
     */
    private long adcHand = UNTESTED;
    /**
     * 阻抗双脚
     */
    private long adcFoot = UNTESTED;
    /**
     * 左手阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcLeftHand = UNTESTED;
    /**
     * 右手阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcRightHand = UNTESTED;
    /**
     * 左脚阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcLeftFoot = UNTESTED;
    /**
     * 右脚阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcRightFoot = UNTESTED;


    /**
     * 阻抗左身体
     */
    private long adcLeftBody = UNTESTED;
    /**
     * 阻抗右身体
     */
    private long adcRightBody = UNTESTED;
    /**
     * 阻抗右手左脚
     */
    private long adcRightHandLeftFoot = UNTESTED;
    /**
     * 阻抗左手右脚
     */
    private long adcLeftHandRightFoot = UNTESTED;
    /**
     * 躯干阻抗
     * 0xFFFFFFFF=未测试
     * 0xFFFFFFFE=测量异常
     */
    private long adcBody = UNTESTED;

    /**
     * 算法Id(1~254)
     *
     */
    private int algorithmsId;


    public EightOneBodyFatAdcBean() {
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

    public long getAdcHand() {
        return adcHand;
    }

    public void setAdcHand(long adcHand) {
        this.adcHand = adcHand;
    }

    public long getAdcFoot() {
        return adcFoot;
    }

    public void setAdcFoot(long adcFoot) {
        this.adcFoot = adcFoot;
    }

    public long getAdcLeftBody() {
        return adcLeftBody;
    }

    public void setAdcLeftBody(long adcLeftBody) {
        this.adcLeftBody = adcLeftBody;
    }

    public long getAdcRightBody() {
        return adcRightBody;
    }

    public void setAdcRightBody(long adcRightBody) {
        this.adcRightBody = adcRightBody;
    }

    public long getAdcRightHandLeftFoot() {
        return adcRightHandLeftFoot;
    }

    public void setAdcRightHandLeftFoot(long adcRightHandLeftFoot) {
        this.adcRightHandLeftFoot = adcRightHandLeftFoot;
    }

    public long getAdcLeftHandRightFoot() {
        return adcLeftHandRightFoot;
    }

    public void setAdcLeftHandRightFoot(long adcLeftHandRightFoot) {
        this.adcLeftHandRightFoot = adcLeftHandRightFoot;
    }

    @Override
    public String toString() {
        return "八电极体脂阻抗数据{" +
                "双手阻抗=" + formatAdcValue(adcHand) +
                ", 双脚阻抗=" + formatAdcValue(adcFoot) +
                ", 左手阻抗=" + formatAdcValue(adcLeftHand) +
                ", 右手阻抗=" + formatAdcValue(adcRightHand) +
                ", 左脚阻抗=" + formatAdcValue(adcLeftFoot) +
                ", 右脚阻抗=" + formatAdcValue(adcRightFoot) +
                ", 左身体阻抗=" + formatAdcValue(adcLeftBody) +
                ", 右身体阻抗=" + formatAdcValue(adcRightBody) +
                ", 右手左脚阻抗=" + formatAdcValue(adcRightHandLeftFoot) +
                ", 左手右脚阻抗=" + formatAdcValue(adcLeftHandRightFoot) +
                ", 躯干阻抗=" + formatAdcValue(adcBody) +
                ", 算法Id=" + algorithmsId +
                '}';
    }

    /**
     * 格式化ADC值，将特殊值转换为对应的状态说明
     */
    private String formatAdcValue(long adcValue) {
        if (adcValue == UNTESTED) {
            return "未测试";
        } else if (adcValue == 0xFFFFFFFEL) {
            return "测量异常";
        } else {
            return String.valueOf(adcValue);
        }
    }
}
