package com.reiserx.screenshot.Interfaces;

import java.util.List;

public interface ImageLabelCallBack {
    void onSuccess(List<String> labels);
    void onFailure(String e);
}
