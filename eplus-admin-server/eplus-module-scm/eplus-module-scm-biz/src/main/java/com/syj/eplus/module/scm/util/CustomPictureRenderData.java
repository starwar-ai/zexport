package com.syj.eplus.module.scm.util;

import com.deepoove.poi.data.RenderData;

public class CustomPictureRenderData implements RenderData {

    /**
     * 图片宽度
     */
    private int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getAltMeta() {
        return altMeta;
    }

    public void setAltMeta(String altMeta) {
        this.altMeta = altMeta;
    }

    public int getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }

    public int getTopOffset() {
        return topOffset;
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
    }

    /**
     * 图片高度
     */
    private int height;
    /**
     * 图片路径
     */
    private String path;

    /**
     * 图片二进制数据
     */
    private transient byte[] data;

    /**
     * 当图片不存在时，显示的文字
     */
    private String altMeta = "";

    /**
     * 左偏量
     */
    private int leftOffset;
    /**
     * 上偏移量
     */
    private int topOffset;

    // 省略get/set/构造等方法
}

