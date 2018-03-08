package com.makhovyk.android.tripservice;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.makhovyk.android.tripservice.Model.ApiClient;
import com.makhovyk.android.tripservice.Model.ApiResponse;
import com.makhovyk.android.tripservice.Model.City;
import com.makhovyk.android.tripservice.Model.DBHelper;
import com.makhovyk.android.tripservice.Model.Trip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


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
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private String message = "";
    private String errorMessage = "";

    @Override
    public void onCreate() {
        dbHelper = new DBHelper(getApplicationContext());
        database = dbHelper.getWritableDatabase();
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
        intent.putExtra(RESULT,message);
        if (message.equals(RESULT_ERROR)){
            intent.putExtra(ERROR,errorMessage);
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
                        dbHelper.dropTables();
                        if (trips.isEmpty()){
                            message = RESULT_EMPTY;
                        }else {

                            // writing to the db
                            ContentValues cv = new ContentValues();
                            for (City c: citySet) {
                                cv.put(DBHelper.CITY_ID,c.getCityId());
                                cv.put(DBHelper.CITY_HIGHLIGHT,c.getHighlight());
                                cv.put(DBHelper.CITY_NAME,c.getName());
                                database.insert(DBHelper.TABLE_CITIES,null, cv);
                            }

                            cv = new ContentValues();
                            for (Trip tr: trips) {
                                cv.put(DBHelper.TRIP_ID,tr.getTripId());
                                cv.put(DBHelper.FROM_CITY,tr.getFromCity().getCityId());
                                cv.put(DBHelper.FROM_DATE,tr.getFromDate());
                                cv.put(DBHelper.FROM_TIME,tr.getFromTime());
                                cv.put(DBHelper.FROM_INFO,tr.getFromInfo());
                                cv.put(DBHelper.TO_CITY,tr.getToCity().getCityId());
                                cv.put(DBHelper.TO_DATE,tr.getToDate());
                                cv.put(DBHelper.TO_TIME,tr.getToTime());
                                cv.put(DBHelper.TO_INFO,tr.getToInfo());
                                cv.put(DBHelper.INFO,tr.getInfo());
                                cv.put(DBHelper.PRICE,tr.getPrice());
                                cv.put(DBHelper.BUS_ID,tr.getBusId());
                                cv.put(DBHelper.RESERVATION_COUNT,tr.getReservationCount());
                                database.insert(DBHelper.TABLE_TRIPS, null, cv);
                            }

                            message = RESULT_OK;
                        }

                        stopService();
                    }
                });
    }

    void stopService(){
        this.stopSelf();
    }
}
