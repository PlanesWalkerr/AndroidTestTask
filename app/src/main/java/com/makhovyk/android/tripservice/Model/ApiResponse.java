package com.makhovyk.android.tripservice.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private List<Trip> trips;

    public boolean isSuccess() {
        return success;
    }

    public List<Trip> getTrips() {
        return trips;
    }
}
