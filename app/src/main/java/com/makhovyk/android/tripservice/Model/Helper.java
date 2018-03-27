package com.makhovyk.android.tripservice.Model;

import java.util.List;
import java.util.Set;

/**
 * Created by misha on 3/26/18.
 */

public interface Helper {
    public boolean isEmpty();

    public void dropTables();

    public List<Trip> getAllTrips();

    public void writeTrips(List<Trip> trips, Set<City> cities);

    public void closeConnection();

    public Trip getTripById(long id);
}
