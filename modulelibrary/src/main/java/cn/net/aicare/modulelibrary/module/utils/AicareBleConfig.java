package cn.net.aicare.modulelibrary.module.utils;

import cn.net.aicare.GetMoreFatData;
import cn.net.aicare.MoreFatData;
import cn.net.aicare.algorithmutil.AlgorithmUtil;


public class AicareBleConfig {

    /**
     * 计算去脂体重，体重控制量等额外的6项身体指标
     *
     * @param sex    1男,2女
     * @param height cm
     * @param weight kg
     * @param bfr    体脂率
     * @param rom    肌肉率
     * @param pp     蛋白率
     * @return MoreFatData
     */
    public static MoreFatData getMoreFatData(int sex, int height, double weight, double bfr, double rom, double pp) {
        return GetMoreFatData.getMoreFatData(sex, height, weight, bfr, rom, pp);
    }


    /**
     * 通过阻抗计算体脂数据
     *
     * @param type     算法类型
     * @param sex      1男,2女
     * @param age      年龄(0~120)
     * @param weightKg 体重(0~220)
     * @param heightCm 身高(0-269)
     * @param adc      阻抗(0~1000)
     * @return 体脂数据
     */
    public static cn.net.aicare.algorithmutil.BodyFatData getBodyFatData(@AlgorithmUtil.AlgorithmType int type, int sex, int age, double weightKg,
                                                                         int heightCm, int adc) {
        return AlgorithmUtil.getBodyFatData(type, sex, age, weightKg, heightCm, adc);
    }

}
