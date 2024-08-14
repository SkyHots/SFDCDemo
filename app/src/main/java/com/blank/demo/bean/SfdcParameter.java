package com.blank.demo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *     author : fupp-
 *     time   : 2024/04/20
 *     desc   :
 * </pre>
 */
public class SfdcParameter implements Parcelable {

    private String wave_name;
    private int result;
    private float lra_f0;
    private float wave_fc;
    private float avg_slope;
    private float bemf_offset;
    private int sfdc_support;

    public int getResult() {
        return result;
    }

    public SfdcParameter(int result, float lra_f0, float wave_fc, float avg_slope, float bemf_offset, int sfdc_support) {
        this.result = result;
        this.lra_f0 = lra_f0;
        this.wave_fc = wave_fc;
        this.avg_slope = avg_slope;
        this.bemf_offset = bemf_offset;
        this.sfdc_support = sfdc_support;
    }

    public String getWave_name() {
        return wave_name;
    }

    public void setWave_name(String wave_name) {
        this.wave_name = wave_name;
    }

    public float getLra_f0() {
        return lra_f0;
    }

    public void setLra_f0(float lra_f0) {
        this.lra_f0 = lra_f0;
    }

    public float getWave_fc() {
        return wave_fc;
    }

    public void setWave_fc(float wave_fc) {
        this.wave_fc = wave_fc;
    }

    public float getAvg_slope() {
        return avg_slope;
    }

    public void setAvg_slope(float avg_slope) {
        this.avg_slope = avg_slope;
    }

    public float getBemf_offset() {
        return bemf_offset;
    }

    public void setBemf_offset(float bemf_offset) {
        this.bemf_offset = bemf_offset;
    }

    public int isSfdc_support() {
        return sfdc_support;
    }

    public void setSfdc_support(int sfdc_support) {
        this.sfdc_support = sfdc_support;
    }

    public SfdcParameter(String wave_name, float lra_f0, float wave_fc, float avg_slope, float bemf_offset, int sfdc_support) {
        this.wave_name = wave_name;
        this.lra_f0 = lra_f0;
        this.wave_fc = wave_fc;
        this.avg_slope = avg_slope;
        this.bemf_offset = bemf_offset;
        this.sfdc_support = sfdc_support;
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.wave_name);
        dest.writeInt(this.result);
        dest.writeFloat(this.lra_f0);
        dest.writeFloat(this.wave_fc);
        dest.writeFloat(this.avg_slope);
        dest.writeFloat(this.bemf_offset);
        dest.writeInt(this.sfdc_support);
        dest.writeInt(this.result);
    }

    public void readFromParcel(Parcel source) {
        this.wave_name = source.readString();
        this.result = source.readInt();
        this.lra_f0 = source.readFloat();
        this.wave_fc = source.readFloat();
        this.avg_slope = source.readFloat();
        this.bemf_offset = source.readFloat();
        this.sfdc_support = source.readInt();
        this.result = source.readInt();
    }

    protected SfdcParameter(Parcel in) {
        this.wave_name = in.readString();
        this.result = in.readInt();
        this.lra_f0 = in.readFloat();
        this.wave_fc = in.readFloat();
        this.avg_slope = in.readFloat();
        this.bemf_offset = in.readFloat();
        this.sfdc_support = in.readInt();
        this.result = in.readInt();
    }

    public static final Parcelable.Creator<SfdcParameter> CREATOR = new Parcelable.Creator<SfdcParameter>() {
        @Override
        public SfdcParameter createFromParcel(Parcel source) {return new SfdcParameter(source);}

        @Override
        public SfdcParameter[] newArray(int size) {return new SfdcParameter[size];}
    };

    @Override
    public String toString() {
        return "SfdcParameter{" +
                "wave_name='" + wave_name + '\'' +
                ", result=" + result +
                ", lra_f0=" + lra_f0 +
                ", wave_fc=" + wave_fc +
                ", avg_slope=" + avg_slope +
                ", bemf_offset=" + bemf_offset +
                ", sfdc_support=" + sfdc_support +
                '}';
    }
}
