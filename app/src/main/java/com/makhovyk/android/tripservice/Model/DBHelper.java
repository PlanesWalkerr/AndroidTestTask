package com.makhovyk.android.tripservice.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TripsDB";
    public static final String TABLE_TRIPS = "Trips";
    public static final String TABLE_CITIES = "Cities";

    public static final String CITY_ID = "_id";
    public static final String CITY_HIGHLIGHT = "highlight";
    public static final String CITY_NAME = "name";

    public static final String TRIP_ID = "_trip_id";
    public static final String FROM_CITY = "from_city";
    public static final String TO_CITY = "to_city";
    public static final String FROM_DATE = "from_date";
    public static final String FROM_TIME = "from_time";
    public static final String FROM_INFO = "from_info";
    public static final String TO_DATE = "to_date";
    public static final String TO_TIME = "to_time";
    public static final String TO_INFO = "to_info";
    public static final String INFO = "info";
    public static final String PRICE = "price";
    public static final String BUS_ID = "bus_id";
    public static final String RESERVATION_COUNT = "reservation_count";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CITIES_TABLE_CREATE = "create table " + TABLE_CITIES + " (" + CITY_ID
                + " integer primary key," + CITY_HIGHLIGHT + " integer," + CITY_NAME + " text)";
        final String TRIPS_TABLE_CREATE = "create table " + TABLE_TRIPS + " (" + TRIP_ID
                + " integer primary key," + FROM_CITY + " integer," + FROM_DATE + " text,"
                + FROM_TIME + " text," + FROM_INFO + " text," + TO_CITY + " integer," + TO_DATE
                + " text," + TO_TIME + " text," + TO_INFO + " text," + INFO + " text,"
                + PRICE + " real," + BUS_ID + " integer," + RESERVATION_COUNT + " integer,"
                + " FOREIGN KEY (" + FROM_CITY + ") REFERENCES " + TABLE_CITIES + "(" + CITY_ID + "),"
                + " FOREIGN KEY (" + TO_CITY + ") REFERENCES " + TABLE_CITIES + "(" + CITY_ID + "))";

        sqLiteDatabase.execSQL(CITIES_TABLE_CREATE);
        sqLiteDatabase.execSQL(TRIPS_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+ TABLE_CITIES);
        sqLiteDatabase.execSQL("drop table if exists "+ TABLE_TRIPS);
        onCreate(sqLiteDatabase);
    }

    public List<Trip> getAllTrips(){
        String sqlQuery = "select t._trip_id, c._id as from_city_id, " +
                "c.name as from_city_name, c.highlight as from_city_hl, t.from_date, " +
                "t.from_time, t.from_info, ct._id as to_city_id, " +
                "ct.highlight as to_city_hl, ct.name as to_city_name, t.to_date, t.to_time, t.to_info, " +
                "t.info, t.price, t.bus_id, t.reservation_count "
                + "from trips as t "
                + "inner join cities as c "
                + "on t.from_city = c._id "
                + "inner join cities as ct "
                + "on t.to_city = ct._id ";
        Cursor tripCursor = this.getReadableDatabase().rawQuery(sqlQuery,null);
        List<Trip> trips = new ArrayList<Trip>();
        if (tripCursor.moveToFirst()) {
            do {
                int idIndex = tripCursor.getColumnIndex(TRIP_ID);
                int fromDateIndex = tripCursor.getColumnIndex(FROM_DATE);
                int fromTimeIndex = tripCursor.getColumnIndex(FROM_TIME);
                int fromInfoIndex = tripCursor.getColumnIndex(FROM_INFO);
                int toDateIndex = tripCursor.getColumnIndex(TO_DATE);
                int toTimeIndex = tripCursor.getColumnIndex(TO_TIME);
                int toInfoIndex = tripCursor.getColumnIndex(TO_INFO);
                int infoIndex = tripCursor.getColumnIndex(INFO);
                int priceIndex = tripCursor.getColumnIndex(PRICE);
                int busIdIndex = tripCursor.getColumnIndex(BUS_ID);
                int reservationCountIndex = tripCursor.getColumnIndex(RESERVATION_COUNT);
                int fromCityIdIndex = tripCursor.getColumnIndex("from_city_id");
                int fromCityHlIndex = tripCursor.getColumnIndex("from_city_hl");
                int fromCityNameIndex = tripCursor.getColumnIndex("from_city_name");
                int toCityIdIndex = tripCursor.getColumnIndex("to_city_id");
                int toCityHlIndex = tripCursor.getColumnIndex("to_city_hl");
                int toCityNameIndex = tripCursor.getColumnIndex("to_city_name");
                Trip tr = new Trip();
                City c = new City();
                City c1 = new City();
                tr.setTripId(tripCursor.getInt(idIndex));
                c.setCityId(tripCursor.getInt(fromCityIdIndex));
                c.setHighlight(tripCursor.getInt(fromCityHlIndex));
                c.setName(tripCursor.getString(fromCityNameIndex));
                tr.setFromCity(c);
                c1.setCityId(tripCursor.getInt(toCityIdIndex));
                c1.setHighlight(tripCursor.getInt(toCityHlIndex));
                c1.setName(tripCursor.getString(toCityNameIndex));
                tr.setToCity(c1);
                tr.setFromDate(tripCursor.getString(fromDateIndex));
                tr.setFromTime(tripCursor.getString(fromTimeIndex));
                tr.setFromInfo(tripCursor.getString(fromInfoIndex));
                tr.setToDate(tripCursor.getString(toDateIndex));
                tr.setToTime(tripCursor.getString(toTimeIndex));
                tr.setToInfo(tripCursor.getString(toInfoIndex));
                tr.setInfo(tripCursor.getString(infoIndex));
                tr.setPrice(tripCursor.getDouble(priceIndex));
                tr.setBusId(tripCursor.getInt(busIdIndex));
                tr.setReservationCount(tripCursor.getInt(reservationCountIndex));
                trips.add(tr);
            } while (tripCursor.moveToNext());
        }
        tripCursor.close();
        return trips;
    }

    public boolean isEmpty(){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_TRIPS, null);
        boolean res = true;
        if (cursor.moveToFirst())
        {
            res = false;
        }

        cursor.close();
        return res;
    }

    public void dropTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CITIES,null,null);
        db.delete(TABLE_TRIPS,null,null);
    }
}
