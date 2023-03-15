package cn.net.aicare.modulelibrary.module.airDetector;

/**
 * 亮度声明
 *
 * @author yesp
 */
public class BrightnessStatement {
    /**
     * 自动调节， 0 不支持， 1 支持
     */
    private boolean autoAdjust;

    /**
     * 手动调节， 0 不支持， 1 支持
     */
    private boolean manualAdjust;

    /**
     * 模式 0:百分比模式:0-100%， 1:档位模式
     */
    private int mode;

    /**
     * 档位数量，若是百分比模式,则该值为 0
     */
    private int levelCount;

    public boolean isAutoAdjust() {
        return autoAdjust;
    }

    public void setAutoAdjust(boolean autoAdjust) {
        this.autoAdjust = autoAdjust;
    }

    public boolean isManualAdjust() {
        return manualAdjust;
    }

    public void setManualAdjust(boolean manualAdjust) {
        this.manualAdjust = manualAdjust;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(int levelCount) {
        this.levelCount = levelCount;
    }
}
