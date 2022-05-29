package com.oneconnect.demoapp.OneConnectModel;


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */


import android.os.Parcel;
import android.os.Parcelable;

public class Countries implements Parcelable {
    private String country;
    private String flagUrl;
    private String ovpn;
    private String ovpnUserName;
    private String ovpnUserPassword;


    public Countries() {
    }

    public Countries(String country, String flagUrl, String ovpn) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.ovpn = ovpn;
    }

    public Countries(String country, String flagUrl, String ovpn, String ovpnUserName, String ovpnUserPassword) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.ovpn = ovpn;
        this.ovpnUserName = ovpnUserName;
        this.ovpnUserPassword = ovpnUserPassword;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    public String getOvpn() {
        return ovpn;
    }

    public void setOvpn(String ovpn) {
        this.ovpn = ovpn;
    }

    public String getOvpnUserName() {
        return ovpnUserName;
    }

    public void setOvpnUserName(String ovpnUserName) {
        this.ovpnUserName = ovpnUserName;
    }

    public String getOvpnUserPassword() {
        return ovpnUserPassword;
    }

    public void setOvpnUserPassword(String ovpnUserPassword) {
        this.ovpnUserPassword = ovpnUserPassword;
    }

    public static final Creator<Countries> CREATOR
            = new Creator<Countries>() {
        public Countries createFromParcel(Parcel in) {
            return new Countries(in);
        }

        public Countries[] newArray(int size) {
            return new Countries[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(flagUrl);
        dest.writeString(ovpn);
        dest.writeString(ovpnUserName);
        dest.writeString(ovpnUserPassword);
    }

    private Countries(Parcel in ) {
        country = in.readString();
        flagUrl = in.readString();
        ovpn = in.readString();
        ovpnUserName = in.readString();
        ovpnUserPassword = in.readString();
    }
}

