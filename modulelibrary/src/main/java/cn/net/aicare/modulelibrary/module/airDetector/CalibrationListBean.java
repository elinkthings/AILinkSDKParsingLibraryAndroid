package cn.net.aicare.modulelibrary.module.airDetector;

import java.util.ArrayList;

public class CalibrationListBean {
    ArrayList<CalibrationBean> calibrationBeanList;

    public CalibrationListBean(ArrayList<CalibrationBean> calibrationBeanList) {
        this.calibrationBeanList = calibrationBeanList;
    }

    public ArrayList<CalibrationBean> getCalibrationBeanList() {
        return calibrationBeanList;
    }

    public void setCalibrationBeanList(ArrayList<CalibrationBean> calibrationBeanList) {
        this.calibrationBeanList = calibrationBeanList;
    }

    public static class CalibrationBean{
        /**
         * 校准类型
         */
        private int calType;
        /**
         * 校准值
         */
        private double calValue;
        /**
         * 小数点
         */
        private int calPoint;

        /**
         * 操作：0=加 1，1=减 1
         */
        private int calOperate;

        /**
         * 恢复默认值, 设置值为 0
         */
        private boolean calReset;

        public CalibrationBean(int calType, double calValue) {
            this.calType = calType;
            this.calValue = calValue;
        }

        public CalibrationBean(int calType, int calOperate, boolean calReset) {
            this.calType = calType;
            this.calOperate = calOperate;
            this.calReset = calReset;
        }

        public int getCalType() {
            return calType;
        }

        public void setCalType(int calType) {
            this.calType = calType;
        }

        public double getCalValue() {
            return calValue;
        }

        public void setCalValue(double calValue) {
            this.calValue = calValue;
        }

        public int getCalPoint() {
            return calPoint;
        }

        public void setCalPoint(int calPoint) {
            this.calPoint = calPoint;
        }

        public int getCalOperate() {
            return calOperate;
        }

        public void setCalOperate(int calOperate) {
            this.calOperate = calOperate;
        }

        public boolean isCalReset() {
            return calReset;
        }

        public void setCalReset(boolean calReset) {
            this.calReset = calReset;
        }
    }
}
