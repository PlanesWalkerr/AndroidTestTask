package com.makhovyk.android.tripservice.Model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.realm.Realm;

/**
 * Created by misha on 3/26/18.
 */

public class RealmDBHelper implements Helper {

    private Realm realm;

    public RealmDBHelper(Context context) {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public boolean isEmpty() {

        return realm.isEmpty();
    }

    @Override
    public void dropTables() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(City.class);
                realm.delete(Trip.class);
            }
        });
    }

    @Override
    public List<Trip> getAllTrips() {
        Log.e("EE", "using realm");
        return realm.where(Trip.class).findAll();

    }


    public void writeCities(Set<City> cities) {
        for (City c : cities) {
            realm.beginTransaction();
            realm.copyToRealm(c);
            realm.commitTransaction();
        }
    }

    @Override
    public void writeTrips(List<Trip> trips, Set<City> cities) {
        for (Trip t : trips) {
            realm.beginTransaction();
            realm.copyToRealm(t);
            realm.commitTransaction();
        }
    }

    @Override
    public void closeConnection() {
        realm.close();
    }

    @Override
    public Trip getTripById(long id) {
        Log.e("EE", "using realm");
        realm.beginTransaction();
        Trip t = realm.where(Trip.class).equalTo("tripId",id).findFirst();
        realm.commitTransaction();
        return t;
    }
}
