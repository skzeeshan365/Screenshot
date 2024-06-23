package com.reiserx.screenshot.Interfaces;

import android.location.Location;

public interface FusedLocationResult {
    void onSuccess(Location location);
    void onFailure(Exception e);
}
