package com.reiserx.screenshot.Models;

import android.net.Uri;

import java.io.File;

public class Screenshots {
    String filename;
    File file;

    public Screenshots(String filename) {
        this.filename = filename;
        file = new File(filename);
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
}
