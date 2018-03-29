package com.makhovyk.android.tripservice;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.makhovyk.android.tripservice.Model.DBHelper;
import com.makhovyk.android.tripservice.Model.Helper;
import com.makhovyk.android.tripservice.Model.RealmDBHelper;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class TripServiceApp extends Application {
    private static TripServiceApp tripServiceApp;
    private GoogleAnalytics sAnalytics;
    private Tracker sTracker;
    private DBHelper dbHelper;
    private RealmDBHelper realmDBHelper;

    public static TripServiceApp getInstance() {
        return tripServiceApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tripServiceApp = this;

        dbHelper = new DBHelper(getInstance());


        sAnalytics = GoogleAnalytics.getInstance(this);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

    public Helper getHelper(String option) {
        if (option.equals("sqlite")) {
            return dbHelper;
        } else if (option.equals("realm")) {

            Realm.init(getInstance());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            return new RealmDBHelper(getInstance());
        }
        return null;
    }
}