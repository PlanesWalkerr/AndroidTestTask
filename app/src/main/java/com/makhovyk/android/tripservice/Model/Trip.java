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
public class Trip implements Serializable, Parcelable, RealmModel {

    @SerializedName("id")
    @Expose

    long tripId;
    @SerializedName("from_city")
    @Expose
    City fromCity;
    @SerializedName("from_date")
    @Expose
    String fromDate;
    @SerializedName("from_time")
    @Expose
    String fromTime;
    @SerializedName("from_info")
    @Expose
    String fromInfo;
    @SerializedName("to_city")
    @Expose
    City toCity;
    @SerializedName("to_date")
    @Expose
    String toDate;
    @SerializedName("to_time")
    @Expose
    String toTime;
    @SerializedName("to_info")
    @Expose
    String toInfo;
    @SerializedName("info")
    @Expose
    String info;
    @SerializedName("price")
    @Expose
    double price;
    @SerializedName("bus_id")
    @Expose
    int busId;
    @SerializedName("reservation_count")
    @Expose
    int reservationCount;

    public Trip() {
    }

    public Trip(long id, City fromCity, City toCity, String info, double price, int busId, int reservationCount) {
        this.tripId = id;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.info = info;
        this.price = price;
        this.busId = busId;
        this.reservationCount = reservationCount;
    }

    protected Trip(Parcel in) {
        tripId = in.readLong();
        fromCity = in.readParcelable(City.class.getClassLoader());
        fromDate = in.readString();
        fromTime = in.readString();
        fromInfo = in.readString();
        toCity = in.readParcelable(City.class.getClassLoader());
        toDate = in.readString();
        toTime = in.readString();
        toInfo = in.readString();
        info = in.readString();
        price = in.readDouble();
        busId = in.readInt();
        reservationCount = in.readInt();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public long getTripId() {
        return tripId;
    }

    public City getFromCity() {
        return fromCity;
    }

    public City getToCity() {
        return toCity;
    }

    public String getInfo() {
        return info;
    }

    public double getPrice() {
        return price;
    }

    public int getBusId() {
        return busId;
    }

    public int getReservationCount() {
        return reservationCount;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public void setFromCity(City fromCity) {
        this.fromCity = fromCity;
    }

    public void setToCity(City toCity) {
        this.toCity = toCity;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public void setReservationCount(int reservationCount) {
        this.reservationCount = reservationCount;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getFromInfo() {
        return fromInfo;
    }

    public void setFromInfo(String fromInfo) {
        this.fromInfo = fromInfo;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getToInfo() {
        return toInfo;
    }

    public void setToInfo(String toInfo) {
        this.toInfo = toInfo;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", fromCity=" + fromCity +
                ", toCity=" + toCity +
                ", info='" + info + '\'' +
                ", price=" + price +
                ", busId=" + busId +
                ", reservationCount=" + reservationCount +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fromDate);
        parcel.writeString(fromTime);
        parcel.writeString(fromInfo);
        parcel.writeString(toDate);
        parcel.writeString(toTime);
        parcel.writeString(toInfo);
        parcel.writeString(info);
        parcel.writeLong(tripId);
        parcel.writeInt(reservationCount);
        parcel.writeInt(busId);
        parcel.writeParcelable(fromCity, i);
        parcel.writeParcelable(toCity, i);

    }
}
