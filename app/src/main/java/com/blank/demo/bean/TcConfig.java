package com.blank.demo.bean;

/**
 * <pre>
 *     author : fupp-
 *     time   : 2024/04/20
 *     desc   :
 * </pre>
 */
public class TcConfig {
    public TcConfig() {
    }

    private String tc_name;
    private short valid_wave_count;
    private TcWaveConfig[] wave_config;
    private short repeat_loop = 0;
    private short record_interval = 10;

    public String getTc_name() {
        return tc_name;
    }

    public void setTc_name(String tc_name) {
        this.tc_name = tc_name;
    }

    public int getValid_wave_count() {
        return valid_wave_count;
    }

    public void setValid_wave_count(short valid_wave_count) {
        this.valid_wave_count = valid_wave_count;
    }

    public TcWaveConfig[] getWave_config() {
        return wave_config;
    }

    public void setWave_config(TcWaveConfig[] wave_config) {
        this.wave_config = wave_config;
    }

    public int getRepeat_loop() {
        return repeat_loop;
    }

    public void setRepeat_loop(short repeat_loop) {
        this.repeat_loop = repeat_loop;
    }

    public int getRecord_interval() {
        return record_interval;
    }

    public void setRecord_interval(short record_interval) {
        this.record_interval = record_interval;
    }

    public TcConfig(String tc_name, short valid_wave_count, TcWaveConfig[] wave_config, short repeat_loop, short record_interval) {
        this.tc_name = tc_name;
        this.valid_wave_count = valid_wave_count;
        this.wave_config = wave_config;
        this.repeat_loop = repeat_loop;
        this.record_interval = record_interval;
    }
}
