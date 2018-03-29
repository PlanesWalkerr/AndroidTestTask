package com.makhovyk.android.tripservice.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ApiResponse {
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private ArrayList<Trip> trips;

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }
}
