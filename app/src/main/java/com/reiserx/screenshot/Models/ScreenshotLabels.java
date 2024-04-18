package com.reiserx.screenshot.Models;

public class ScreenshotLabels {
    String label, filepath;

    public ScreenshotLabels(String label, String filepath) {
        this.label = label;
        this.filepath = filepath;
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
}
