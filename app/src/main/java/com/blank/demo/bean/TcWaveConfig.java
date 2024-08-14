package com.blank.demo.bean;

public class TcWaveConfig {
    private short wave_index;
    private short f0_drift = 10;
    private short playback_count = 5;
    private float playback_interval = 2;

    public int getWave_index() {
        return wave_index;
    }

    public void setWave_index(short wave_index) {
        this.wave_index = wave_index;
    }

    public short getF0_drift() {
        return f0_drift;
    }

    public void setF0_drift(short f0_drift) {
        this.f0_drift = f0_drift;
    }

    public int getPlayback_count() {
        return playback_count;
    }

    public void setPlayback_count(short playback_count) {
        this.playback_count = playback_count;
    }

    public float getPlayback_interval() {
        return playback_interval;
    }

    public void setPlayback_interval(float playback_interval) {
        this.playback_interval = playback_interval;
    }

    public TcWaveConfig() {
    }

    public TcWaveConfig(short wave_index, short f0_drift, short playback_count, float playback_interval) {
        this.wave_index = wave_index;
        this.f0_drift = f0_drift;
        this.playback_count = playback_count;
        this.playback_interval = playback_interval;
    }
}