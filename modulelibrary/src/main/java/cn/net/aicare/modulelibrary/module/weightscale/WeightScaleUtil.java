package cn.net.aicare.modulelibrary.module.weightscale;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @auther ljl
 * on 2023/3/3
 */
public class WeightScaleUtil {

    public static float valueToNum(String value, int point) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        String pattern = "0.0";
        float myNum = Float.parseFloat(value);

        int integer = Math.abs((int) myNum);
        if (String.valueOf(integer).length() == 4) {
            pattern = "0000";
        } else if (String.valueOf(integer).length() == 3) {
            pattern = "000";
        } else if (String.valueOf(integer).length() == 2) {
            pattern = "00";
        } else {
            pattern = "0";
        }
        if (point == 1) {
            pattern = pattern + ".0";
        } else if (point == 2) {
            pattern = pattern + ".00";
        } else if (point == 3) {
            pattern = pattern + ".000";
        }
        decimalFormat.applyPattern(pattern);
        return Float.parseFloat(decimalFormat.format(myNum));
    }
}
