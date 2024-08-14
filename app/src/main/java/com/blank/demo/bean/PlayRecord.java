package com.blank.demo.bean;

public class PlayRecord {

    int result;
    short wave_index;
    float temperature;
    float output_f0;
    float relative_bemf;

    public int getResult() {
        return result;
    }

    public short getWave_index() {
        return wave_index;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getOutput_f0() {
        return output_f0;
    }

    public float getRelative_bemf() {
        return relative_bemf;
    }

    public void setWave_index(short wave_index) {
        this.wave_index = wave_index;
    }

    public PlayRecord(short wave_index, float temperature, float output_f0, float relative_bemf) {
        this.wave_index = wave_index;
        this.temperature = temperature;
        this.output_f0 = output_f0;
        this.relative_bemf = relative_bemf;
    }

    public PlayRecord() {
    }

    @Override
    public String toString() {
        return "PlayRecord{" +
                "result=" + result +
                ", wave_index=" + wave_index +
                ", temperature=" + temperature +
                ", output_f0=" + output_f0 +
                ", relative_bemf=" + relative_bemf +
                '}';
    }
}
