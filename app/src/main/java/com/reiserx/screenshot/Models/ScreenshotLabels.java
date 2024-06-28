package com.reiserx.screenshot.Models;

import com.google.android.gms.ads.nativead.NativeAd;

public class ScreenshotLabels {
    String label, filepath;
    int type;
    NativeAd nativeAd;
    LocationData location;

    public ScreenshotLabels(String label, String filepath) {
        this.label = label;
        this.filepath = filepath;
    }

    public ScreenshotLabels(int type) {
        this.label = label;
        this.filepath = filepath;
        this.type = type;
    }

    public ScreenshotLabels(int type, NativeAd nativeAd) {
        this.label = label;
        this.filepath = filepath;
        this.type = type;
        this.nativeAd = nativeAd;
    }

    public ScreenshotLabels(String label, String filepath, LocationData location) {
        this.label = label;
        this.filepath = filepath;
        this.location = location;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public NativeAd getNativeAd() {
        return nativeAd;
    }

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }

    public LocationData getLocation() {
        return location;
    }

    public void setLocation(LocationData location) {
        this.location = location;
    }
}
