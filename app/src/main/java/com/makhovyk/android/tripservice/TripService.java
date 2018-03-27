package com.makhovyk.android.tripservice;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.makhovyk.android.tripservice.Model.ApiClient;
import com.makhovyk.android.tripservice.Model.ApiResponse;
import com.makhovyk.android.tripservice.Model.City;
import com.makhovyk.android.tripservice.Model.DBHelper;
import com.makhovyk.android.tripservice.Model.Helper;
import com.makhovyk.android.tripservice.Model.HelperFactory;
import com.makhovyk.android.tripservice.Model.Trip;
import com.makhovyk.android.tripservice.Utils.SettingsManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class TripService extends Service {

    public static final String NOTIFICATION = "com.makhovyk.android.tripservice.receiver";
    public static final String RESULT = "result";
    public static final String RESULT_OK = "0";
    public static final String RESULT_ERROR = "1";
    public static final String RESULT_EMPTY = "2";
    public static final String ERROR = "error";

    private final String BASE_URL = "http://projects.gmoby.org/web/index.php/";
    private List<Trip> trips = new ArrayList<>();
    private Set<City> citySet = new HashSet<>();
    private Helper dbHelper;
    private SQLiteDatabase database;
    private String message = "";
    private String errorMessage = "";
    private String DBMS;

    @Override
    public void onCreate() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);

        DBMS = new SettingsManager(getApplicationContext()).getDBMS();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getTrips();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //sending the result message
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, message);
        if (message.equals(RESULT_ERROR)) {
            intent.putExtra(ERROR, errorMessage);
        }

        sendBroadcast(intent);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getTrips() {
        ApiClient apiClient = new ApiClient(BASE_URL);
        apiClient.getTrips("2016-01-01", "2018-03-01")
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<ApiResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ApiResponse apiResponse) {
                        trips = apiResponse.getTrips();

                        //collection of unique cities
                        for (Trip t : trips) {
                            citySet.add(t.getFromCity());
                            citySet.add(t.getToCity());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        message = RESULT_ERROR;
                        errorMessage = e.getMessage();
                        stopService();
                    }

                    @Override
                    public void onComplete() {
                        dbHelper = HelperFactory.geHelper(getApplicationContext(), DBMS);
                        if (!dbHelper.isEmpty()) {
                            dbHelper.dropTables();
                        }

                        if (trips.isEmpty()) {
                            message = RESULT_EMPTY;
                        } else {

                            // writing to the db
                            //dbHelper.writeCities(citySet);

                            dbHelper.writeTrips(trips, citySet);

                            message = RESULT_OK;
                        }
                        dbHelper.closeConnection();
                        stopService();
                    }
                });
    }

    void stopService() {
        this.stopSelf();
    }
}
