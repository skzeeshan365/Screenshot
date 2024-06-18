package com.reiserx.screenshot.Interfaces;

import com.reiserx.screenshot.Models.Response;

public interface APICallback {
    void onSuccess(Response response);
    void onFailure(String message);
}
