package com.wtz.tools.test.data;

public class RecorderItem {
    float time;
    String filePath;

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public RecorderItem(float time, String filePath) {
        super();
        this.time = time;
        this.filePath = filePath;
    }
}
