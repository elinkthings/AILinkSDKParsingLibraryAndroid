package cn.net.aicare.modulelibrary.module.EightDoubleBodyfatScale;


public class EightDoubleDataBodyFatBean {

    /**
     * 重量
     */
    private float weightKg;     //体重kg，
    /**
     * 体质指数
     */
    private float bmi;     //体质指数，
    /**
     * 体脂率
     */
    private float bfr;     //体脂率，
    /**
     * 皮下脂肪率
     */
    private float sfr;     //皮下脂肪率，
    /**
     * 内脏脂肪率
     */
    private float uvi;     //内脏脂肪率，
    /**
     * 肌肉率
     */
    private float rom;     //肌肉率，
    /**
     * 基础代谢率
     */
    private float bmr;     //基础代谢率，
    /**
     * 骨量(kg)
     */
    private String bm;     //骨骼质量，
    /**
     * 水含量(%)
     */
    private float vwc;     //水含量，
    /**
     * 身体年龄
     */
    private int bodyAge;     //身体年龄，
    /**
     * 蛋白率(%)
     */
    private float pp;     //蛋白率，

    /**
     * 算法ID
     */
    private int arithmetic;
    /**
     * 心率
     */
    private int heartRate;
    /**
     * 创建时间
     */
    private long createTime;     //创建时间，
    /**
     * 上传时间
     */
    private long uploadTime;     //上传时间

    /**
     * 体脂-右上(kg)
     */
    private float fatMassRightTopKg;
    /**
     * 体脂-右下(kg)
     */
    private float fatMassRightBottomKg;
    /**
     * 体脂-左上(kg)
     */
    private float fatMassLeftTopKg;
    /**
     * 体脂-左下(kg)
     */
    private float fatMassLeftBottomKg;
    /**
     * 体脂-躯干(kg)
     */
    private float fatMassBodyKg;

    /**
     * 肌肉-右上(kg)
     */
    private float muscleMassRightTopKg;
    /**
     * 肌肉-右下(kg)
     */
    private float muscleMassRightBottomKg;
    /**
     * 肌肉-左上(kg)
     */
    private float muscleMassLeftTopKg;
    /**
     * 肌肉-左下(kg)
     */
    private float muscleMassLeftBottomKg;
    /**
     * 肌肉-躯干(kg)
     */
    private float muscleMassBodyKg;


    /**
     * 骨骼肌质量
     */
    private float skeletalMuscleMass = 0;

    /**
     * 脂肪控制量-千克
     */
    private Float bodyFatKgCon;
    /**
     * 肌肉控制量-千克
     */
    private Float muscleKgCon;
    /**
     * 水分重-千克
     */
    private Float waterKg;
    /**
     * 细胞质量-千克
     */
    private Float cellMassKg;
    /**
     * 细胞外水分重量-千克
     */
    private Float waterECWKg;
    /**
     * 细胞内水分重量-千克
     */
    private Float waterICWKg;
    /**
     * 矿物质重量-千克
     */
    private Float mineralKg;
    /**
     * 腰臀比
     */
    private Float whr;
    /**
     * 身体形态
     */
    private Float obesity;
    /**
     * 建议摄入量,Kcal/天
     */
    private Integer dci;
    /**
     * 矿物质标准区间
     */
    private String mineralKgStand;
    /**
     * 细胞外水分标准区间
     */
    private String waterECWKgStand;
    /**
     * 细胞质量标准区间
     */
    private String cellMassKgStand;
    /**
     * 细胞内水分标准区间
     */
    private String waterICWKgStand;
    /**
     * 身体评分
     */
    private Integer bodyScore;
    /**
     * 体脂率-右上
     */
    private Float fatMassRightTopRate;
    /**
     * 体脂率-右下
     */
    private Float fatMassRightBottomRate;
    /**
     * 体脂率-左上
     */
    private Float fatMassLeftTopRate;
    /**
     * 体脂率-左下
     */
    private Float fatMassLeftBottomRate;
    /**
     * 体脂率-躯干
     */
    private Float fatMassBodyRate;
    /**
     * 肌肉率-右上
     */
    private Float musleMassRightTopRate;
    /**
     * 肌肉率-右下
     */
    private Float musleMassRightBottomRate;
    /**
     * 肌肉率-左上
     */
    private Float musleMassLeftTopRate;
    /**
     * 肌肉率-左下
     */
    private Float musleMassLeftBottomRate;
    /**
     * 肌肉率-躯干
     */
    private Float musleMassBodyRate;
    /**
     * 20kHz阻抗-左臂
     */
    private Float Z20KhzLeftArmDeCode;
    /**
     * 20kHz阻抗-右臂
     */
    private Float Z20KhzRightArmDeCode;
    /**
     * 20kHz阻抗-左腿
     */
    private Float Z20KhzLeftLegDeCode;
    /**
     * 20kHz阻抗-右腿
     */
    private Float Z20KhzRightLegDeCode;
    /**
     * 20kHz阻抗-躯干
     */
    private Float Z20KhzTrunkDeCode;
    /**
     * 100kHz阻抗-左臂
     */
    private Float Z100KhzLeftArmDeCode;
    /**
     * 100kHz阻抗-右臂
     */
    private Float Z100KhzRightArmDeCode;
    /**
     * 100kHz阻抗-左腿
     */
    private Float Z100KhzLeftLegDeCode;
    /**
     * 100kHz阻抗-右腿
     */
    private Float Z100KhzRightLegDeCode;
    /**
     * 100kHz阻抗-躯干
     */
    private Float Z100KhzTrunkDeCode;


    public float getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(float weightKg) {
        this.weightKg = weightKg;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getBfr() {
        return bfr;
    }

    public void setBfr(float bfr) {
        this.bfr = bfr;
    }

    public float getSfr() {
        return sfr;
    }

    public void setSfr(float sfr) {
        this.sfr = sfr;
    }

    public float getUvi() {
        return uvi;
    }

    public void setUvi(float uvi) {
        this.uvi = uvi;
    }

    public float getRom() {
        return rom;
    }

    public void setRom(float rom) {
        this.rom = rom;
    }

    public float getBmr() {
        return bmr;
    }

    public void setBmr(float bmr) {
        this.bmr = bmr;
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public float getVwc() {
        return vwc;
    }

    public void setVwc(float vwc) {
        this.vwc = vwc;
    }

    public int getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(int bodyAge) {
        this.bodyAge = bodyAge;
    }

    public float getPp() {
        return pp;
    }

    public void setPp(float pp) {
        this.pp = pp;
    }


    public int getArithmetic() {
        return arithmetic;
    }

    public void setArithmetic(int arithmetic) {
        this.arithmetic = arithmetic;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getHeartRate() {
        return heartRate;
    }


    /**
     * 获取四肢肌肉
     *
     * @return float
     */
    public float getLimbsMuscle() {
        return muscleMassLeftBottomKg+muscleMassRightBottomKg+muscleMassLeftTopKg+muscleMassRightTopKg;
    }

    /**
     * 获取肌肉质量
     *
     * @return float
     */
    public float getMuscleMassKg() {
        return muscleMassBodyKg+muscleMassLeftBottomKg+muscleMassRightBottomKg+muscleMassLeftTopKg+muscleMassRightTopKg;
    }


    /**
     * 获取脂肪质量
     *
     * @return float
     */
    public float getFatMassKg() {
        return fatMassBodyKg+fatMassLeftBottomKg+fatMassRightBottomKg+fatMassLeftTopKg+fatMassRightTopKg;
    }

    public float getFatMassRightTopKg() {
        return fatMassRightTopKg;
    }

    public void setFatMassRightTopKg(float fatMassRightTopKg) {
        this.fatMassRightTopKg = fatMassRightTopKg;
    }

    public float getFatMassRightBottomKg() {
        return fatMassRightBottomKg;
    }

    public void setFatMassRightBottomKg(float fatMassRightBottomKg) {
        this.fatMassRightBottomKg = fatMassRightBottomKg;
    }

    public float getFatMassLeftTopKg() {
        return fatMassLeftTopKg;
    }

    public void setFatMassLeftTopKg(float fatMassLeftTopKg) {
        this.fatMassLeftTopKg = fatMassLeftTopKg;
    }

    public float getFatMassLeftBottomKg() {
        return fatMassLeftBottomKg;
    }

    public void setFatMassLeftBottomKg(float fatMassLeftBottomKg) {
        this.fatMassLeftBottomKg = fatMassLeftBottomKg;
    }

    public float getFatMassBodyKg() {
        return fatMassBodyKg;
    }

    public void setFatMassBodyKg(float fatMassBodyKg) {
        this.fatMassBodyKg = fatMassBodyKg;
    }

    public float getMuscleMassRightTopKg() {
        return muscleMassRightTopKg;
    }

    public void setMuscleMassRightTopKg(float muscleMassRightTopKg) {
        this.muscleMassRightTopKg = muscleMassRightTopKg;
    }

    public float getMuscleMassRightBottomKg() {
        return muscleMassRightBottomKg;
    }

    public void setMuscleMassRightBottomKg(float muscleMassRightBottomKg) {
        this.muscleMassRightBottomKg = muscleMassRightBottomKg;
    }

    public float getMuscleMassLeftTopKg() {
        return muscleMassLeftTopKg;
    }

    public void setMuscleMassLeftTopKg(float muscleMassLeftTopKg) {
        this.muscleMassLeftTopKg = muscleMassLeftTopKg;
    }

    public float getMuscleMassLeftBottomKg() {
        return muscleMassLeftBottomKg;
    }

    public void setMuscleMassLeftBottomKg(float muscleMassLeftBottomKg) {
        this.muscleMassLeftBottomKg = muscleMassLeftBottomKg;
    }

    public float getMuscleMassBodyKg() {
        return muscleMassBodyKg;
    }

    public void setMuscleMassBodyKg(float muscleMassBodyKg) {
        this.muscleMassBodyKg = muscleMassBodyKg;
    }




    public float getSkeletalMuscleMass() {
        return skeletalMuscleMass;
    }

    public void setSkeletalMuscleMass(float skeletalMuscleMass) {
        this.skeletalMuscleMass = skeletalMuscleMass;
    }


    public Float getBodyFatKgCon() {
        return bodyFatKgCon;
    }

    public void setBodyFatKgCon(Float bodyFatKgCon) {
        this.bodyFatKgCon = bodyFatKgCon;
    }

    public Float getMuscleKgCon() {
        return muscleKgCon;
    }

    public void setMuscleKgCon(Float muscleKgCon) {
        this.muscleKgCon = muscleKgCon;
    }

    public Float getWaterKg() {
        return waterKg;
    }

    public void setWaterKg(Float waterKg) {
        this.waterKg = waterKg;
    }

    public Float getCellMassKg() {
        return cellMassKg;
    }

    public void setCellMassKg(Float cellMassKg) {
        this.cellMassKg = cellMassKg;
    }

    public Float getWaterECWKg() {
        return waterECWKg;
    }

    public void setWaterECWKg(Float waterECWKg) {
        this.waterECWKg = waterECWKg;
    }

    public Float getWaterICWKg() {
        return waterICWKg;
    }

    public void setWaterICWKg(Float waterICWKg) {
        this.waterICWKg = waterICWKg;
    }

    public Float getMineralKg() {
        return mineralKg;
    }

    public void setMineralKg(Float mineralKg) {
        this.mineralKg = mineralKg;
    }

    public Float getWhr() {
        return whr;
    }

    public void setWhr(Float whr) {
        this.whr = whr;
    }

    public Float getObesity() {
        return obesity;
    }

    public void setObesity(Float obesity) {
        this.obesity = obesity;
    }

    public Integer getDci() {
        return dci;
    }

    public void setDci(Integer dci) {
        this.dci = dci;
    }

    public String getMineralKgStand() {
        return mineralKgStand;
    }

    public void setMineralKgStand(String mineralKgStand) {
        this.mineralKgStand = mineralKgStand;
    }

    public String getWaterECWKgStand() {
        return waterECWKgStand;
    }

    public void setWaterECWKgStand(String waterECWKgStand) {
        this.waterECWKgStand = waterECWKgStand;
    }

    public String getCellMassKgStand() {
        return cellMassKgStand;
    }

    public void setCellMassKgStand(String cellMassKgStand) {
        this.cellMassKgStand = cellMassKgStand;
    }

    public String getWaterICWKgStand() {
        return waterICWKgStand;
    }

    public void setWaterICWKgStand(String waterICWKgStand) {
        this.waterICWKgStand = waterICWKgStand;
    }

    public Integer getBodyScore() {
        return bodyScore;
    }

    public void setBodyScore(Integer bodyScore) {
        this.bodyScore = bodyScore;
    }

    public Float getFatMassRightTopRate() {
        return fatMassRightTopRate;
    }

    public void setFatMassRightTopRate(Float fatMassRightTopRate) {
        this.fatMassRightTopRate = fatMassRightTopRate;
    }

    public Float getFatMassRightBottomRate() {
        return fatMassRightBottomRate;
    }

    public void setFatMassRightBottomRate(Float fatMassRightBottomRate) {
        this.fatMassRightBottomRate = fatMassRightBottomRate;
    }

    public Float getFatMassLeftTopRate() {
        return fatMassLeftTopRate;
    }

    public void setFatMassLeftTopRate(Float fatMassLeftTopRate) {
        this.fatMassLeftTopRate = fatMassLeftTopRate;
    }

    public Float getFatMassLeftBottomRate() {
        return fatMassLeftBottomRate;
    }

    public void setFatMassLeftBottomRate(Float fatMassLeftBottomRate) {
        this.fatMassLeftBottomRate = fatMassLeftBottomRate;
    }

    public Float getFatMassBodyRate() {
        return fatMassBodyRate;
    }

    public void setFatMassBodyRate(Float fatMassBodyRate) {
        this.fatMassBodyRate = fatMassBodyRate;
    }

    public Float getMusleMassRightTopRate() {
        return musleMassRightTopRate;
    }

    public void setMusleMassRightTopRate(Float musleMassRightTopRate) {
        this.musleMassRightTopRate = musleMassRightTopRate;
    }

    public Float getMusleMassRightBottomRate() {
        return musleMassRightBottomRate;
    }

    public void setMusleMassRightBottomRate(Float musleMassRightBottomRate) {
        this.musleMassRightBottomRate = musleMassRightBottomRate;
    }

    public Float getMusleMassLeftTopRate() {
        return musleMassLeftTopRate;
    }

    public void setMusleMassLeftTopRate(Float musleMassLeftTopRate) {
        this.musleMassLeftTopRate = musleMassLeftTopRate;
    }

    public Float getMusleMassLeftBottomRate() {
        return musleMassLeftBottomRate;
    }

    public void setMusleMassLeftBottomRate(Float musleMassLeftBottomRate) {
        this.musleMassLeftBottomRate = musleMassLeftBottomRate;
    }

    public Float getMusleMassBodyRate() {
        return musleMassBodyRate;
    }

    public void setMusleMassBodyRate(Float musleMassBodyRate) {
        this.musleMassBodyRate = musleMassBodyRate;
    }

    public Float getZ20KhzLeftArmDeCode() {
        return Z20KhzLeftArmDeCode;
    }

    public void setZ20KhzLeftArmDeCode(Float z20KhzLeftArmDeCode) {
        Z20KhzLeftArmDeCode = z20KhzLeftArmDeCode;
    }

    public Float getZ20KhzRightArmDeCode() {
        return Z20KhzRightArmDeCode;
    }

    public void setZ20KhzRightArmDeCode(Float z20KhzRightArmDeCode) {
        Z20KhzRightArmDeCode = z20KhzRightArmDeCode;
    }

    public Float getZ20KhzLeftLegDeCode() {
        return Z20KhzLeftLegDeCode;
    }

    public void setZ20KhzLeftLegDeCode(Float z20KhzLeftLegDeCode) {
        Z20KhzLeftLegDeCode = z20KhzLeftLegDeCode;
    }

    public Float getZ20KhzRightLegDeCode() {
        return Z20KhzRightLegDeCode;
    }

    public void setZ20KhzRightLegDeCode(Float z20KhzRightLegDeCode) {
        Z20KhzRightLegDeCode = z20KhzRightLegDeCode;
    }

    public Float getZ20KhzTrunkDeCode() {
        return Z20KhzTrunkDeCode;
    }

    public void setZ20KhzTrunkDeCode(Float z20KhzTrunkDeCode) {
        Z20KhzTrunkDeCode = z20KhzTrunkDeCode;
    }

    public Float getZ100KhzLeftArmDeCode() {
        return Z100KhzLeftArmDeCode;
    }

    public void setZ100KhzLeftArmDeCode(Float z100KhzLeftArmDeCode) {
        Z100KhzLeftArmDeCode = z100KhzLeftArmDeCode;
    }

    public Float getZ100KhzRightArmDeCode() {
        return Z100KhzRightArmDeCode;
    }

    public void setZ100KhzRightArmDeCode(Float z100KhzRightArmDeCode) {
        Z100KhzRightArmDeCode = z100KhzRightArmDeCode;
    }

    public Float getZ100KhzLeftLegDeCode() {
        return Z100KhzLeftLegDeCode;
    }

    public void setZ100KhzLeftLegDeCode(Float z100KhzLeftLegDeCode) {
        Z100KhzLeftLegDeCode = z100KhzLeftLegDeCode;
    }

    public Float getZ100KhzRightLegDeCode() {
        return Z100KhzRightLegDeCode;
    }

    public void setZ100KhzRightLegDeCode(Float z100KhzRightLegDeCode) {
        Z100KhzRightLegDeCode = z100KhzRightLegDeCode;
    }

    public Float getZ100KhzTrunkDeCode() {
        return Z100KhzTrunkDeCode;
    }

    public void setZ100KhzTrunkDeCode(Float z100KhzTrunkDeCode) {
        Z100KhzTrunkDeCode = z100KhzTrunkDeCode;
    }

    @Override
    public String toString() {
        return "八电极体脂数据{" +
                "重量=" + weightKg + "kg" +
                ", 体质指数=" + bmi +
                ", 体脂率=" + bfr + "%" +
                ", 皮下脂肪率=" + sfr + "%" +
                ", 内脏脂肪率=" + uvi +
                ", 肌肉率=" + rom + "%" +
                ", 基础代谢率=" + bmr + "kcal" +
                ", 骨量=" + (bm != null ? bm + "kg" : "未测量") +
                ", 水含量=" + vwc + "%" +
                ", 身体年龄=" + bodyAge + "岁" +
                ", 蛋白率=" + pp + "%" +
                ", 算法ID=" + arithmetic +
                ", 心率=" + heartRate + "bpm" +
                ", 创建时间=" + createTime +
                ", 上传时间=" + uploadTime +
                ", 体脂右上=" + fatMassRightTopKg + "kg" +
                ", 体脂右下=" + fatMassRightBottomKg + "kg" +
                ", 体脂左上=" + fatMassLeftTopKg + "kg" +
                ", 体脂左下=" + fatMassLeftBottomKg + "kg" +
                ", 体脂躯干=" + fatMassBodyKg + "kg" +
                ", 肌肉右上=" + muscleMassRightTopKg + "kg" +
                ", 肌肉右下=" + muscleMassRightBottomKg + "kg" +
                ", 肌肉左上=" + muscleMassLeftTopKg + "kg" +
                ", 肌肉左下=" + muscleMassLeftBottomKg + "kg" +
                ", 肌肉躯干=" + muscleMassBodyKg + "kg" +
                ", 骨骼肌质量=" + skeletalMuscleMass + "kg" +
                ", 脂肪控制量=" + formatNullableFloat(bodyFatKgCon, "kg") +
                ", 肌肉控制量=" + formatNullableFloat(muscleKgCon, "kg") +
                ", 水分重=" + formatNullableFloat(waterKg, "kg") +
                ", 细胞质量=" + formatNullableFloat(cellMassKg, "kg") +
                ", 细胞外水分重量=" + formatNullableFloat(waterECWKg, "kg") +
                ", 细胞内水分重量=" + formatNullableFloat(waterICWKg, "kg") +
                ", 矿物质重量=" + formatNullableFloat(mineralKg, "kg") +
                ", 腰臀比=" + formatNullableFloat(whr) +
                ", 身体形态=" + formatNullableFloat(obesity) +
                ", 建议摄入量=" + (dci != null ? dci + "kcal/天" : "未设置") +
                ", 矿物质标准区间=" + formatNullableString(mineralKgStand) +
                ", 细胞外水分标准区间=" + formatNullableString(waterECWKgStand) +
                ", 细胞质量标准区间=" + formatNullableString(cellMassKgStand) +
                ", 细胞内水分标准区间=" + formatNullableString(waterICWKgStand) +
                ", 身体评分=" + (bodyScore != null ? bodyScore + "分" : "未评分") +
                ", 体脂率右上=" + formatNullableFloat(fatMassRightTopRate, "%") +
                ", 体脂率右下=" + formatNullableFloat(fatMassRightBottomRate, "%") +
                ", 体脂率左上=" + formatNullableFloat(fatMassLeftTopRate, "%") +
                ", 体脂率左下=" + formatNullableFloat(fatMassLeftBottomRate, "%") +
                ", 体脂率躯干=" + formatNullableFloat(fatMassBodyRate, "%") +
                ", 肌肉率右上=" + formatNullableFloat(musleMassRightTopRate, "%") +
                ", 肌肉率右下=" + formatNullableFloat(musleMassRightBottomRate, "%") +
                ", 肌肉率左上=" + formatNullableFloat(musleMassLeftTopRate, "%") +
                ", 肌肉率左下=" + formatNullableFloat(musleMassLeftBottomRate, "%") +
                ", 肌肉率躯干=" + formatNullableFloat(musleMassBodyRate, "%") +
                ", 20kHz阻抗左臂=" + formatNullableFloat(Z20KhzLeftArmDeCode, "Ω") +
                ", 20kHz阻抗右臂=" + formatNullableFloat(Z20KhzRightArmDeCode, "Ω") +
                ", 20kHz阻抗左腿=" + formatNullableFloat(Z20KhzLeftLegDeCode, "Ω") +
                ", 20kHz阻抗右腿=" + formatNullableFloat(Z20KhzRightLegDeCode, "Ω") +
                ", 20kHz阻抗躯干=" + formatNullableFloat(Z20KhzTrunkDeCode, "Ω") +
                ", 100kHz阻抗左臂=" + formatNullableFloat(Z100KhzLeftArmDeCode, "Ω") +
                ", 100kHz阻抗右臂=" + formatNullableFloat(Z100KhzRightArmDeCode, "Ω") +
                ", 100kHz阻抗左腿=" + formatNullableFloat(Z100KhzLeftLegDeCode, "Ω") +
                ", 100kHz阻抗右腿=" + formatNullableFloat(Z100KhzRightLegDeCode, "Ω") +
                ", 100kHz阻抗躯干=" + formatNullableFloat(Z100KhzTrunkDeCode, "Ω") +
                '}';
    }

    /**
     * 格式化可为空的Float值，添加单位
     */
    private String formatNullableFloat(Float value, String unit) {
        if (value == null) {
            return "未测量";
        }
        return value + unit;
    }

    /**
     * 格式化可为空的Float值（无单位）
     */
    private String formatNullableFloat(Float value) {
        if (value == null) {
            return "未测量";
        }
        return String.valueOf(value);
    }

    /**
     * 格式化可为空的String值
     */
    private String formatNullableString(String value) {
        if (value == null) {
            return "未设置";
        }
        return value;
    }
}
