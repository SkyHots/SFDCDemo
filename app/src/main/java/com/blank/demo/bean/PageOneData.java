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
public class PageOneData implements Parcelable {
    private String tvStep;
    private String tvWindow;
    private String lraType;

    public String getTvStep() {
        return tvStep;
    }

    public String getTvWindow() {
        return tvWindow;
    }

    public String getLraType() {
        return lraType;
    }

    public PageOneData(String tvStep, String tvWindow, String lraType) {
        this.tvStep = tvStep;
        this.tvWindow = tvWindow;
        this.lraType = lraType;
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tvStep);
        dest.writeString(this.tvWindow);
        dest.writeString(this.lraType);
    }

    public void readFromParcel(Parcel source) {
        this.tvStep = source.readString();
        this.tvWindow = source.readString();
        this.lraType = source.readString();
    }

    public PageOneData() {}

    protected PageOneData(Parcel in) {
        this.tvStep = in.readString();
        this.tvWindow = in.readString();
        this.lraType = in.readString();
    }

    public static final Parcelable.Creator<PageOneData> CREATOR = new Parcelable.Creator<PageOneData>() {
        @Override
        public PageOneData createFromParcel(Parcel source) {return new PageOneData(source);}

        @Override
        public PageOneData[] newArray(int size) {return new PageOneData[size];}
    };
}
