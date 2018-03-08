package com.makhovyk.android.tripservice.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class City implements Serializable{

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
                ((this.name == null) ? 0 : this.name.hashCode()); return result;
    }
}
