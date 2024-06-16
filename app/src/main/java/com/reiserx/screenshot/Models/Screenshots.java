package com.reiserx.screenshot.Models;

import com.google.android.gms.ads.nativead.NativeAd;

import java.io.File;

public class Screenshots {
    String filename;
    File file;
    NativeAd nativeAd;
    int type;

    public Screenshots(String filename) {
        this.filename = filename;
        file = new File(filename);
    }

    public Screenshots(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }

    public Screenshots(int type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public NativeAd getNativeAd() {
        return nativeAd;
    }

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
