package cn.net.aicare.modulelibrary.module.demonEyes;

/**
 * 恶魔之眼配置类
 *
 * @author xing
 * @date 2024/09/23
 */
public class DemonEyesConfig {

    public final static int CID = 0x0071;
    public final static int MTU = 517;


    /**
     * a5开始
     */
    public final static byte A5_START = (byte) 0xA5;
    /**
     * a5结束
     */
    public final static byte A5_END = (byte) 0x5A;


    /**
     * CMD显示控制
     */
    public final static int CMD_SHOW_CONTROL = 0x01;
    /**
     * CMD更新图片
     */
    public final static int CMD_UPDATE_IMAGE = 0x02;

}
