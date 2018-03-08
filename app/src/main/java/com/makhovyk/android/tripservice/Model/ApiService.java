package com.makhovyk.android.tripservice.Model;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiService {
    @Headers("Accept: application/json")
    @GET("api/trips")
    Observable<ApiResponse> getTrips(@Query("from_date") String fromDate,
                                     @Query("to_date") String toDate);
}
