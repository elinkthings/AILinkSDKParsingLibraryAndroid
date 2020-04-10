package cn.net.aicare.modulelibrary.module.BodyFatScale;

public class User {


    /**
     * 性别,0女,1男
     * Gender, 0 female, 1 male
     */
    private int sex;
    /**
     * 年龄 0~120
     * Age 0 ~ 120
     */
    private int age;
    /**
     * 身高cm 0~269
     * Height cm 0 ~ 269
     */
    private int height;
    /**
     * 体重kg 1位小数 0~250
     * Weight kg 1 decimal place 0 ~ 250
     */
    private float weight;
    /**
     * 模式,0普通,1运动员
     * Mode, 0 Normal, 1 Athlete
     */
    private int modeType;
    /**
     * 1到8
     */
    private int id;
    /**
     * 阻抗值,正常0~1000
     * Impedance value, normal 0 ~ 1000
     */
    private int adc;


    public int getAdc() {
        return adc;
    }

    public void setAdc(int adc) {
        this.adc = adc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getModeType() {
        return modeType;
    }

    public void setModeType(int modeType) {
        this.modeType = modeType;
    }

    @Override
    public String toString() {
        return "User{" +
                "sex=" + sex +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", modeType=" + modeType +
                ", id=" + id +
                ", adc=" + adc +
                '}';
    }
}
