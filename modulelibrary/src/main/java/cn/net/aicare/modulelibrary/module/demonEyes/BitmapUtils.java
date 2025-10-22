package cn.net.aicare.modulelibrary.module.demonEyes;

import android.graphics.Bitmap;

import java.io.FileOutputStream;

public class BitmapUtils {


    /**
     * 获取bmp图片的字节数组,rgb565格式(elinkHeader的字节序为小端序)
     *
     * @param bitmap 位图
     * @param width  宽度
     * @param height 高度
     * @return {@link byte[]}
     */
    public static byte[] getBmpByte(Bitmap bitmap, int width, int height) {
        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return getBmpByte(bmp);

    }

    /**
     * 获取bitmap的字节数组,rgb565格式(elinkHeader的字节序为小端序)
     *
     * @param bitmap 位图
     * @return {@link byte[]}
     */
    public static byte[] getBmpByte(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int bitmapSize = width * height * 2;
        byte[] header = new byte[54];
        byte[] elinkHeader = new byte[16];
        byte[] bmpData = new byte[bitmapSize];
        byte[] payload = new byte[header.length + elinkHeader.length + bmpData.length];
        //BMP Header
        header[0] = (byte) 'B';
        header[1] = (byte) 'M';
        int fileSize = bmpData.length;
        header[2] = (byte) (fileSize & 0xFF);
        header[3] = (byte) ((fileSize >> 8) & 0xFF);
        header[4] = (byte) ((fileSize >> 16) & 0xFF);
        header[5] = (byte) ((fileSize >> 24) & 0xFF);
        header[10] = (byte) 54;// Offset to pixel data
        header[14] = (byte) 40;// DIB header size
        header[18] = (byte) (width & 0xFF);
        header[19] = (byte) ((width >> 8) & 0xFF);
        header[20] = (byte) ((width >> 16) & 0xFF);
        header[21] = (byte) ((width >> 24) & 0xFF);
        header[22] = (byte) (height & 0xFF);
        header[23] = (byte) ((height >> 8) & 0xFF);
        header[24] = (byte) ((height >> 16) & 0xFF);
        header[25] = (byte) ((height >> 24) & 0xFF);
        header[26] = (byte) 1;// Planes
        header[28] = (byte) 16;// Bits per pixel

        //BMP Data
        int offset = 0;
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                int rgb555 = ((red >> 3) << 10) | ((green >> 3) << 5) | (blue >> 3);
                bmpData[offset++] = (byte) (rgb555 & 0xFF);
                bmpData[offset++] = (byte) ((rgb555 >> 8) & 0xFF);
            }
        }
        //Elink Header
        elinkHeader[0] = (byte) (width);
        elinkHeader[1] = (byte) (width >> 8);
        elinkHeader[2] = (byte) (height);
        elinkHeader[3] = (byte) (height >> 8);
        elinkHeader[4] = 0;
        elinkHeader[5] = 16;
        elinkHeader[6] = 0;
        elinkHeader[7] = 0;
        elinkHeader[8] = (byte) bitmapSize;
        elinkHeader[9] = (byte) (bitmapSize >> 8);
        elinkHeader[10] = (byte) (bitmapSize >> 16);
        elinkHeader[11] = (byte) (bitmapSize >> 24);
        System.arraycopy(header, 0, payload, 0, header.length);
        System.arraycopy(elinkHeader, 0, payload, header.length, elinkHeader.length);
        System.arraycopy(bmpData, 0, payload, header.length + elinkHeader.length, bmpData.length);

        byte[] saveData = new byte[bitmapSize + header.length];
        System.arraycopy(header, 0, saveData, 0, header.length);
        System.arraycopy(bmpData, 0, saveData, header.length, bmpData.length);

        return payload;
    }

    public static byte[] getBmpByte(String path, Bitmap bitmap, int width, int height) {
        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return getBmpByte(path, bmp);

    }

    /**
     * 获取bitmap的字节数组,rgb565格式(elinkHeader的字节序为小端序)
     *
     * @param bitmap 位图
     * @return {@link byte[]}
     */
    public static byte[] getBmpByte(String path, Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int bitmapSize = width * height * 2;
        byte[] header = new byte[54];
        byte[] elinkHeader = new byte[16];
        byte[] bmpData = new byte[bitmapSize];
        byte[] payload = new byte[header.length + elinkHeader.length + bmpData.length];
        //BMP Header
        header[0] = (byte) 'B';
        header[1] = (byte) 'M';
        int fileSize = bmpData.length;
        //本结构体的大小
        header[2] = (byte) (fileSize & 0xFF);
        header[3] = (byte) ((fileSize >> 8) & 0xFF);
        header[4] = (byte) ((fileSize >> 16) & 0xFF);
        header[5] = (byte) ((fileSize >> 24) & 0xFF);
        header[10] = (byte) 54;// Offset to pixel data
        header[14] = (byte) 40;// DIB header size
        //图像的宽度（像素）
        header[18] = (byte) (width & 0xFF);
        header[19] = (byte) ((width >> 8) & 0xFF);
        header[20] = (byte) ((width >> 16) & 0xFF);
        header[21] = (byte) ((width >> 24) & 0xFF);
        //图像的高度（像素）
        header[22] = (byte) (height & 0xFF);
        header[23] = (byte) ((height >> 8) & 0xFF);
        header[24] = (byte) ((height >> 16) & 0xFF);
        header[25] = (byte) ((height >> 24) & 0xFF);
        //颜色平面数，必须设置为1
        header[26] = (byte) 1;
        header[27] = (byte) 0;
        //每个像素的位数（1, 4, 8, 16, 24, 32）
        header[28] = (byte) 16;
        header[29] = (byte) 0;
        //使用的压缩类型;0（无压缩）、1（BI_RLE8，8位游程编码）和2（BI_RLE4，4位游程编码）。大多数真彩色图像不使用压缩。
        header[30] = (byte) 0;

        //BMP Data
        int offset = 0;
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                int rgb565 = ((red >> 3) << 10) | ((green >> 3) << 5) | (blue >> 3);
                bmpData[offset++] = (byte) (rgb565 & 0xFF);
                bmpData[offset++] = (byte) ((rgb565 >> 8) & 0xFF);
            }
        }
        //Elink Header
        elinkHeader[0] = (byte) (width);
        elinkHeader[1] = (byte) (width >> 8);
        elinkHeader[2] = (byte) (height);
        elinkHeader[3] = (byte) (height >> 8);
        elinkHeader[4] = 0;
        elinkHeader[5] = 16;
        elinkHeader[6] = 0;
        elinkHeader[7] = 0;
        elinkHeader[8] = (byte) bitmapSize;
        elinkHeader[9] = (byte) (bitmapSize >> 8);
        elinkHeader[10] = (byte) (bitmapSize >> 16);
        elinkHeader[11] = (byte) (bitmapSize >> 24);
        System.arraycopy(header, 0, payload, 0, header.length);
        System.arraycopy(elinkHeader, 0, payload, header.length, elinkHeader.length);
        System.arraycopy(bmpData, 0, payload, header.length + elinkHeader.length, bmpData.length);

        byte[] saveData = new byte[bitmapSize + header.length];
        System.arraycopy(header, 0, saveData, 0, header.length);
        System.arraycopy(bmpData, 0, saveData, header.length, bmpData.length);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(saveData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return payload;
    }

}
