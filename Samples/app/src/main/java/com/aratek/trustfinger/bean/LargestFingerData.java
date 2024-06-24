package com.aratek.trustfinger.bean;

import android.graphics.Bitmap;

public class LargestFingerData {
    private byte[] fpImageData;
    private byte[] fpFeatureData;
    private Bitmap fpImage_bitmap;
    private int imgQuality = -1;
    private boolean isrRaise = true;
    private boolean isThreshold = false;
    public byte[] getFpImageData() {
        return fpImageData;
    }

    public boolean isThreshold() {
        return isThreshold;
    }

    public void setThreshold(boolean threshold) {
        isThreshold = threshold;
    }

    public void setFpImageData(byte[] fpImageData) {
        this.fpImageData = fpImageData;
    }

    public byte[] getFpFeatureData() {
        return fpFeatureData;
    }

    public void setFpFeatureData(byte[] fpFeatureData) {
        this.fpFeatureData = fpFeatureData;
    }

    public Bitmap getFpImage_bitmap() {
        return fpImage_bitmap;
    }

    public void setFpImage_bitmap(Bitmap fpImage_bitmap) {
        this.fpImage_bitmap = fpImage_bitmap;
    }

    public int getImgQuality() {
        return imgQuality;
    }

    public void setImgQuality(int imgQuality) {
        this.imgQuality = imgQuality;
    }

    public boolean isIsrRaise() {
        return isrRaise;
    }

    public void setIsrRaise(boolean isrRaise) {
        this.isrRaise = isrRaise;
    }

    public void update(byte[] fpImageData , byte[] fpFeatureData , int imgQuality , Bitmap fpImage_bitmap){
        this.fpImageData = fpImageData;
        this.fpFeatureData = fpFeatureData;
        this.imgQuality = imgQuality;
        this.fpImage_bitmap = fpImage_bitmap;
    }
    public void update(byte[] fpFeatureData , int imgQuality , Bitmap fpImage_bitmap){
        this.fpImageData = fpImageData;
        this.fpFeatureData = fpFeatureData;
        this.imgQuality = imgQuality;
        this.fpImage_bitmap = fpImage_bitmap;
    }
    public void clear(){
        this.fpImageData = null;
        this.fpFeatureData = null;
        this.imgQuality = -1;
        this.isrRaise = true;
        isThreshold = false;
    }
}
