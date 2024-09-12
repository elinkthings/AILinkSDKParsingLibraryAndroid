package cn.net.aicare.modulelibrary.module.ble_nutrition_nutrient;

/**
 * 食品营养豆
 * 美国食品库:https://fdc.nal.usda.gov/fdc-app.html
 * 1, 搜索到食物后优先使用id大的食物,(时间新的)
 *
 * @author xing
 * @date 2024/06/15
 */
public class FoodNutrientBean {

    private double weightToG;
    /**
     * 重量为100g倍数
     */
    private double weightFor100gMultiple;

    /**
     * 卡路里(热量) 精度0.1 Kcal
     * Energy (Atwater General Factors)
     */
    private double kcal;
    /**
     * 脂肪热量 精度0.1 Kcal
     * 1g脂肪热量=9Kcal
     */
    private double fatKcal;
    /**
     * 脂肪 精度0.1 g
     */
    private double fat;
    /**
     * 饱和脂肪 精度0.1 g
     */
    private double satFat;
    /**
     * 反式脂肪 精度0.1 g
     */
    private double transFat;
    /**
     * 胆固醇 精度0.1 mg
     */
    private double cholesterol;

    /**
     * 钠 精度0.1 mg
     */
    private double na;
    /**
     * 钾 精度0.1 mg
     */
    private double k;

    /**
     * 总碳水化合物 精度0.1 g
     * Carbohydrate, by summation
     */
    private double totalCarbohydrate;

    /**
     * 膳食纤维 精度0.1 g
     * Fiber, total dietary
     */
    private double fiber;
    /**
     * 糖 精度0.1 g
     * Sugars, Total
     */
    private double sugar;
    /**
     * 蛋白质 精度0.1 g
     */
    private double protein;

    public FoodNutrientBean() {
    }

    public void setWeightToG(double weightToG) {
        this.weightToG = weightToG;
        //算出100g倍数,用于计算营养元素
        weightFor100gMultiple = weightToG / 100;
    }

    public double getWeightToG() {
        return weightToG;
    }

    public double getKcal() {
        return kcal;
    }

    public int getKcalInt() {
        return getFormatData(kcal);
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }


    public double getFat() {
        return fat;
    }

    public int getFatInt() {
        return getFormatData(fat);
    }

    public double getFatKcal() {
        return fatKcal;
    }

    public int getFatKcalInt() {
        return getFormatData(fatKcal);
    }

    public void setFat(double fat) {
        //1g脂肪热量=9Kcal
        fatKcal = fat * 9;
        this.fat = fat;
    }

    public double getSatFat() {
        return satFat;
    }

    public int getSatFatInt() {
        return getFormatData(satFat);
    }

    public void setSatFat(double satFat) {
        this.satFat = satFat;
    }

    public double getTransFat() {
        return transFat;
    }

    public int getTransFatInt() {
        return getFormatData(transFat);
    }

    public void setTransFat(double transFat) {
        this.transFat = transFat;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public int getCholesterolInt() {
        return getFormatData(cholesterol);
    }

    public void setCholesterol(double cholesterol) {
        this.cholesterol = cholesterol;
    }

    public double getNa() {
        return na;
    }

    public int getNaInt() {
        return getFormatData(na);
    }

    public void setNa(double na) {
        this.na = na;
    }

    public double getK() {
        return k;
    }

    public int getKInt() {
        return getFormatData(k);
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getTotalCarbohydrate() {
        return totalCarbohydrate;
    }

    public int getTotalCarbohydrateInt() {
        return getFormatData(totalCarbohydrate);
    }

    public void setTotalCarbohydrate(double totalCarbohydrate) {
        this.totalCarbohydrate = totalCarbohydrate;
    }

    public double getFiber() {
        return fiber;
    }

    public int getFiberInt() {
        return getFormatData(fiber);
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getSugar() {
        return sugar;
    }

    public int getSugarInt() {
        return getFormatData(sugar);
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getProtein() {
        return protein;
    }

    public int getProteinInt() {
        return getFormatData(protein);
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    @Override
    public String toString() {
        return "FoodNutrientBean{" +
                "kcal=" + kcal +
                ", fatKcal=" + fatKcal +
                ", fat=" + fat +
                ", satFat=" + satFat +
                ", transFat=" + transFat +
                ", cholesterol=" + cholesterol +
                ", na=" + na +
                ", k=" + k +
                ", totalCarbohydrate=" + totalCarbohydrate +
                ", fiber=" + fiber +
                ", sugar=" + sugar +
                ", protein=" + protein +
                '}';
    }

    public String toStringInt() {
        return "FoodNutrientBean{" +
                "weightToG=" + weightToG +
                ", kcalInt=" + getKcalInt() +
                ", fatKcalInt=" + getFatKcalInt() +
                ", fatInt=" + getFatInt() +
                ", satFatInt=" + getSatFatInt() +
                ", transFatInt=" + getTransFatInt() +
                ", cholesterolInt=" + getCholesterolInt() +
                ", naInt=" + getNaInt() +
                ", kInt=" + getKInt() +
                ", totalCarbohydrateInt=" + getTotalCarbohydrateInt() +
                ", fiberInt=" + getFiberInt() +
                ", sugarInt=" + getSugarInt() +
                ", proteinInt=" + getProteinInt() +
                '}';
    }

    /**
     * 获取格式数据(四舍五入)
     *
     * @param data 数据
     * @return int
     */
    private int getFormatData(double data) {
        data = data * weightFor100gMultiple * 10D;
//        double result = new BigDecimal(data).setScale(0, RoundingMode.HALF_UP).doubleValue();
        return (int) Math.round(data);
    }

}
