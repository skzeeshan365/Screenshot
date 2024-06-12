package com.reiserx.screenshot.Interfaces;

public interface ScanCallback {
    void onScanSuccess(String text);
    void onScanFailure(String e);
}
