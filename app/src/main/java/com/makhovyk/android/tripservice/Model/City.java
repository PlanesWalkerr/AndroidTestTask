package com.makhovyk.android.tripservice.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class City implements Serializable, Parcelable, RealmModel {

    @SerializedName("id")
    @Expose
    private long cityId;
    @SerializedName("highlight")
    @Expose
    private long highlight;
    @SerializedName("name")
    @Expose
    private String name;

    public City() {
    }

    public City(long id, long highlight, String name) {
        this.cityId = id;
        this.highlight = highlight;
        this.name = name;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityId=" + cityId +
                ", highlight=" + highlight +
                ", name='" + name + '\'' +
                '}';
    }

    public long getCityId() {
        return cityId;
    }

    public long getHighlight() {
        return highlight;
    }

    public String getName() {
        return name;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public void setHighlight(long highlight) {
        this.highlight = highlight;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        City city = (City) obj;
        return city.getCityId() == this.cityId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (int) (prime * result + this.cityId);
        result = prime * result +
                ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeLong(highlight);
        parcel.writeLong(cityId);

    }

    public City(Parcel in) {
        this.cityId = in.readLong();
        this.highlight = in.readLong();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
