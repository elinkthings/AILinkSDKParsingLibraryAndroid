package cn.net.aicare.modulelibrary.module.TempInstrument;

import com.pingwang.bluetoothlib.annotation.IntDef;
import com.pingwang.bluetoothlib.annotation.LongDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * xing<br>
 * 2019/5/11<br>
 * 测温仪指令
 */
public class TempInstrumentBleConfig {

    /**
     * 测温仪
     */
    public final static int DEVICE_CID = 0x003D;


    /**
     * 体温
     */
    public final static int TEMP_BODY = 1;
    /**
     * 物温
     */
    public final static int TEMP_OBJECT = 2;


    @LongDef({LanguageType.LANGUAGE_NULL, LanguageType.LANGUAGE_EN, LanguageType.LANGUAGE_IT, LanguageType.LANGUAGE_KO, LanguageType.LANGUAGE_ES, LanguageType.LANGUAGE_JP, LanguageType.LANGUAGE_JP1
            , LanguageType.LANGUAGE_VI, LanguageType.LANGUAGE_KM, LanguageType.LANGUAGE_MS, LanguageType.LANGUAGE_IN, LanguageType.LANGUAGE_AR, LanguageType.LANGUAGE_TH, LanguageType.LANGUAGE_PL,
            LanguageType.LANGUAGE_ZH, LanguageType.LANGUAGE_ZH0, LanguageType.LANGUAGE_ZH1, LanguageType.LANGUAGE_ZH2, LanguageType.LANGUAGE_ZH3})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LanguageType {
        /**
         * 无语言
         */
        public static final long LANGUAGE_NULL = 0;
        /**
         * 滴
         */
        public static final long LANGUAGE_DI = 1;
        /**
         * 叮咚
         */
        public static final long LANGUAGE_DD = 2;
        /**
         * 英语
         */
        public static final long LANGUAGE_EN = 4;
        /**
         * 意大利
         */
        public static final long LANGUAGE_IT = 8;
        /**
         * 韩语
         */
        public static final long LANGUAGE_KO = 16;
        /**
         * 西班牙语
         */
        public static final long LANGUAGE_ES = 32;
        /**
         * 日语1
         */
        public static final long LANGUAGE_JP = 64;
        /**
         * 日语2
         */
        public static final long LANGUAGE_JP1 = 128;
        /**
         * 法语
         */
        public static final long LANGUAGE_FR = 256;
        /**
         * 越南语
         */
        public static final long LANGUAGE_VI = 512;
        /**
         * 柬埔寨语
         */
        public static final long LANGUAGE_KM = 1024;
        /**
         * 马来语
         */
        public static final long LANGUAGE_MS = 2048;
        /**
         * 印尼语
         */
        public static final long LANGUAGE_IN = 4096;
        /**
         * 阿拉伯语
         */
        public static final long LANGUAGE_AR = 8192;
        /**
         * 泰语
         */
        public static final long LANGUAGE_TH = 16384;
        /**
         * 波兰语
         */
        public static final long LANGUAGE_PL = 32768;
        /**
         * 中文1
         */
        public static final long LANGUAGE_ZH = 65536;
        /**
         * 中文2
         */
        public static final long LANGUAGE_ZH0 = 131072;
        /**
         * 中文3
         */
        public static final long LANGUAGE_ZH1 = 262144;
        /**
         * 中文4
         */
        public static final long LANGUAGE_ZH2 = 524288;
        /**
         * 中文5
         */
        public static final long LANGUAGE_ZH3 = 1048576;


    }

    @IntDef({SoundType.SOUND_8, SoundType.SOUND_7, SoundType.SOUND_6, SoundType.SOUND_5, SoundType.SOUND_4, SoundType.SOUND_3, SoundType.SOUND_2, SoundType.SOUND_1})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SoundType {
        /**
         * 关闭音效
         */
        int SOUND_1 = 0x01;
        /**
         * 叮咚 2
         */
        int SOUND_2 = 0x02;
        /**
         * 叮咚 3
         */
        int SOUND_3 = 0x04;
        /**
         * 叮咚 4
         */
        int SOUND_4 = 0x08;
        /**
         * 叮咚 5
         */
        int SOUND_5 = 0x10;
        /**
         * 叮咚 6
         */
        int SOUND_6 = 0x20;

        /**
         * 叮咚 7
         */
        int SOUND_7 = 0x40;
        /**
         * 嘀 8
         */
        int SOUND_8 = 0x80;


    }


    @IntDef({TestTempDistanceType.DISTANCE_1, TestTempDistanceType.DISTANCE_2, TestTempDistanceType.DISTANCE_3, TestTempDistanceType.DISTANCE_4, TestTempDistanceType.DISTANCE_5,
            TestTempDistanceType.DISTANCE_6, TestTempDistanceType.DISTANCE_7, TestTempDistanceType.DISTANCE_8})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TestTempDistanceType {
        /**
         * 0.6M
         */
        int DISTANCE_1 = 0x01;
        /**
         * 0.8M
         */
        int DISTANCE_2 = 0x02;
        /**
         * 1.0M
         */
        int DISTANCE_3 = 0x04;
        /**
         * 1.2M
         */
        int DISTANCE_4 = 0x08;
        /**
         * 1.4M
         */
        int DISTANCE_5 = 0x10;
        /**
         * 1.6M
         */
        int DISTANCE_6 = 0x20;

        /**
         * 1.8M
         */
        int DISTANCE_7 = 0x40;
        /**
         * 2.0M
         */
        int DISTANCE_8 = 0x80;


    }


    @IntDef({UnitType.TEMP_UNIT_C, UnitType.TEMP_UNIT_F})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UnitType {
        int TEMP_UNIT_C = 0;
        int TEMP_UNIT_F = 1;
    }


    @IntDef({AutoCloseType.AUTO_CLOSE_TYPE_8, AutoCloseType.AUTO_CLOSE_TYPE_7, AutoCloseType.AUTO_CLOSE_TYPE_6, AutoCloseType.AUTO_CLOSE_TYPE_5, AutoCloseType.AUTO_CLOSE_TYPE_4,
            AutoCloseType.AUTO_CLOSE_TYPE_3, AutoCloseType.AUTO_CLOSE_TYPE_2, AutoCloseType.AUTO_CLOSE_TYPE_1})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AutoCloseType {

        /**
         * 24H
         */
        int AUTO_CLOSE_TYPE_8 = 0x80;
        /**
         * 20H
         */
        int AUTO_CLOSE_TYPE_7 = 0x40;
        /**
         * 16H
         */
        int AUTO_CLOSE_TYPE_6 = 0x20;
        /**
         * 12H
         */
        int AUTO_CLOSE_TYPE_5 = 0x10;
        /**
         * 8H
         */
        int AUTO_CLOSE_TYPE_4 = 0x08;
        /**
         * 4H
         */
        int AUTO_CLOSE_TYPE_3 = 0x04;
        /**
         * 2H
         */
        int AUTO_CLOSE_TYPE_2 = 0x02;
        /**
         * OFF,关闭自动关机功能
         */
        int AUTO_CLOSE_TYPE_1 = 0x01;
    }

    /**
     * 错误信息(温度过高,温度过低)
     */
    public static final byte GET_ERR = (byte) 0xFF;


    @IntDef({TempErrType.TEMP_ERR_HI, TempErrType.TEMP_ERR_LO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TempErrType {
        int TEMP_ERR_HI = 0;
        int TEMP_ERR_LO = 1;

    }
}
