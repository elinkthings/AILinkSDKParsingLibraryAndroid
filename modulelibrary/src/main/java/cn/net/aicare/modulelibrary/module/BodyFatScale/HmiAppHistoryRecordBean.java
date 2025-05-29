package cn.net.aicare.modulelibrary.module.BodyFatScale;

public class HmiAppHistoryRecordBean {

    private long time;
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
     * 阻抗值,正常0~1000
     * Impedance value, normal 0 ~ 1000
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
     * 算法标识位 Algorithm flag
     */
    private int arithmetic;
    /**
     * 心率 Heart rate
     */
    private int heartRate;
    /**
     * 年 月 日 时 分 秒
     */
    private int year,month,day,hh,mm,ss;

    public HmiAppHistoryRecordBean() {
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



    public void setBodyFatRecord(float adc, int arithmetic, int heartrate ){
                 this.adc=adc;
                 this.arithmetic=arithmetic;
                 this.heartRate =heartrate;

    }

    public long getTime() {
        return time;
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

    public int getArithmetic() {
        return arithmetic;
    }

    public int getHeartRate() {
        return heartRate;
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
        return "HmiAppHistoryRecordBean{" +
                "time=" + time +
                ", sex=" + sex +
                ", mode=" + mode +
                ", bodyId=" + bodyId +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", adc=" + adc +
                ", weightUnit=" + weightUnit +
                ", weightPoint=" + weightPoint +
                ", arithmetic=" + arithmetic +
                ", heartRate=" + heartRate +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hh=" + hh +
                ", mm=" + mm +
                ", ss=" + ss +
                '}';
    }
}
