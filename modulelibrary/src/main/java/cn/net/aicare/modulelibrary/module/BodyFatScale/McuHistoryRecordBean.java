package cn.net.aicare.modulelibrary.module.BodyFatScale;



public class McuHistoryRecordBean {

    /**
     * 性别,0女,1男
     * Gender, 0 female, 1 male
     */
    private int sex;
    /**
     * 模式,0普通,1运动员
     * Mode, 0 Normal, 1 Athlete
     */
    private int mode;
    private int bodyId;
    /**
     * 年龄 age
     */
    private int age;
    /**
     * 身高 height
     */
    private int height;
    /**
     * 体重 weight
     */
    private float weight;
    /**
     * 阻抗值 Impedance
     */
    private float adc;
    /**
     * 体重单位 Weight unit
     */
    private int weightUnit;
    /**
     * 体重小数位 Decimal weight
     */
    private int weightPoint;
    /**
     * 体脂率 body fat rate
     */
    private float bfr;
    /**
     * 皮下脂肪率 Subcutaneous fat rate
     */
    private float sfr;
    /**
     * 内脏脂肪指数
     */
    private float uvi;
    /**
     * 肌肉率 Rate of muscle
     */
    private float rom;
    /**
     * 基础代谢率 basal metabolic rate
     */
    private float bmr;
    /**
     * 骨骼质量 Bone Mass kg
     */
    private float bm;
    /**
     * 水含量
     */
    private float vwc;
    /**
     * 心率 Heart rate(255代表没有)
     */
    private int heartRate;
    /**
     * 身体年龄 physical bodyAge
     */
    private float bodyAge;
    /**
     * 蛋白率 protein percentage
     */
    private float pp;
    /**
     * 时间,年,月,日,时,分,秒
     */
    private int year,month,day,hh,mm,ss;

    public McuHistoryRecordBean() {
    }

    public void  setUser(int year, int month,int day,int hh,int mm,int ss, int sex, int mode, int bodyId, int age, int height, float weight, int weightunit, int weightPoint) {
        this.year=year;
        this.month=month;
        this.day=day;
        this.hh=hh;
        this.mm=mm;
        this.ss=ss;
        this.sex = sex;
        this.mode = mode;
        this.bodyId = bodyId;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.weightUnit = weightunit;
        this.weightPoint = weightPoint;
    }



    public void setBodyFatRecord_1(float adc, float bfr, float sfr, float uvi, float rom , float bmr, float bodyAge){
        this.adc = adc;
        this.bfr=bfr;
        this.sfr = sfr;
        this.uvi = uvi;
        this.rom = rom;
        this.bmr = bmr;
        this.bodyAge = bodyAge;



    }
    public void setBodyFatRecord_2(float bm,float vwc,float pp,int heartrate ){
        this.bm = bm;
        this.vwc = vwc;
        this.heartRate = heartrate;
        this.pp = pp;
    }



    public int getSex() {
        return sex;
    }

    public int getMode() {
        return mode;
    }

    public int getBodyId() {
        return bodyId;
    }

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }

    public float getAdc() {
        return adc;
    }

    public int getWeightUnit() {
        return weightUnit;
    }

    public int getWeightPoint() {
        return weightPoint;
    }

    public float getBfr() {
        return bfr;
    }

    public float getSfr() {
        return sfr;
    }

    public float getUvi() {
        return uvi;
    }

    public float getRom() {
        return rom;
    }

    public float getBmr() {
        return bmr;
    }

    public float getBm() {
        return bm;
    }

    public float getVwc() {
        return vwc;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public float getBodyAge() {
        return bodyAge;
    }

    public float getPp() {
        return pp;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHh() {
        return hh;
    }

    public int getMm() {
        return mm;
    }

    public int getSs() {
        return ss;
    }

    @Override
    public String toString() {
        return "McuHistoryRecordBean{" +
                "sex=" + sex +
                ", mode=" + mode +
                ", bodyId=" + bodyId +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", adc=" + adc +
                ", weightUnit=" + weightUnit +
                ", weightPoint=" + weightPoint +
                ", bfr=" + bfr +
                ", sfr=" + sfr +
                ", uvi=" + uvi +
                ", rom=" + rom +
                ", bmr=" + bmr +
                ", bm=" + bm +
                ", vwc=" + vwc +
                ", heartRate=" + heartRate +
                ", bodyAge=" + bodyAge +
                ", pp=" + pp +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hh=" + hh +
                ", mm=" + mm +
                ", ss=" + ss +
                '}';
    }
}
