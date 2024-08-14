package com.blank.demo.utils;


import android.annotation.SuppressLint;

import com.blank.demo.bean.PlayRecord;
import com.blank.demo.bean.ScanConfig;
import com.blank.demo.bean.SfdcParameter;
import com.blank.demo.bean.TcConfig;
import com.blank.demo.bean.TcRecord;

/**
 * @Created by    blank
 */
@SuppressLint("UnsafeDynamicallyLoadedCode")
public class F0Class {

    static {
        System.loadLibrary("rtdemo");
    }
    public native int sfdc_wave_clear();
    public native int sfdc_wave_add(String waveName, short waveLength, byte[] waveData);
    public native SfdcParameter[] sfdc_param_cali_run(String lraType, ScanConfig config, SfdcParameter[] paramList);
    public native int sfdc_param_cali_apply();
    public native int sfdc_drift_apply(short f0Drift);
    public native int sfdc_enable();
    public native int sfdc_disable();
    public native int sfdc_long_waveform_playback(String waveform);
    public native PlayRecord sfdc_wave_play(PlayRecord record);
    public native int sfdc_tc_add(TcConfig config);
    public native int sfdc_tc_edit(TcConfig config);
    public native int sfdc_tc_delete(String tcName);
    public native TcRecord sfdc_tc_execute(String record_path, String tcName, TcRecord tcRecord, int waveSize);
    public native float sfdc_get_continuous_f0();
    private static volatile F0Class instance;
    private F0Class() {
    }

    public static F0Class getInstance() {
        if (instance == null) {
            synchronized (F0Class.class) {
                if (instance == null) {
                    instance = new F0Class();
                }
            }
        }
        return instance;
    }
}