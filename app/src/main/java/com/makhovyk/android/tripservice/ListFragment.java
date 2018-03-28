package com.makhovyk.android.tripservice;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makhovyk.android.tripservice.Model.DBHelper;
import com.makhovyk.android.tripservice.Model.Helper;
import com.makhovyk.android.tripservice.Model.HelperFactory;
import com.makhovyk.android.tripservice.Model.Trip;
import com.makhovyk.android.tripservice.Utils.FileLogger;
import com.makhovyk.android.tripservice.Utils.SettingsManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class ListFragment extends Fragment {

    private final String TAG = "TripLog";

    private BroadcastReceiver broadcastReceiver;
    private String resultMessage;
    private Helper dbHelper;
    private List<Trip> trips = new ArrayList<Trip>();
    private String DBMS;
    SettingsManager settings;
    private Unbinder unbinder;

    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @BindView(R.id.trips_recycler_view)
    RecyclerView tripsRecyclerView;
    @BindView(R.id.progress_message_text_view)
    TextView progressMessage;
    @BindView(R.id.message_empty_text_view)
    TextView messageEmpty;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Callbacks callbacks;
    private Tracker tracker;

    // callback to start activity with trip details
    public interface Callbacks {
        void onTripSelected(Trip trip);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        //dbHelper = new DBHelper(getActivity());
        //dbHelper = HelperFactory.geHelper(getActivity(), "sqlite");
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        Fabric.with(getActivity(), new Crashlytics());

        setHasOptionsMenu(true);
        checkPermissions();
        settings = new SettingsManager(getActivity());
        if (settings.isEmpty()) {
            settings.setDefault();
        }
        DBMS = settings.getDBMS();

        FileLogger.logInFile(TAG, "current db: " + DBMS, getActivity());
        Realm.init(getActivity());
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        dbHelper = HelperFactory.geHelper(getActivity(), DBMS);

        setRetainInstance(true);
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        //tripsRecyclerView = view.findViewById(R.id.trips_recycler_view);
        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //progressMessage = view.findViewById(R.id.progress_message_text_view);
        progressMessage.setText(getResources().getString(R.string.progress_message));
        // messageEmpty = view.findViewById(R.id.message_empty_text_view);
        //swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        //disabling UI and starting API request on swipe down
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                disableUI();
                dbHelper.closeConnection();
                getActivity().startService(new Intent(getActivity(), TripService.class));
            }

        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //receiving message from service
                dbHelper = HelperFactory.geHelper(getActivity(), DBMS);
                resultMessage = intent.getExtras().getString(TripService.RESULT);
                switch (resultMessage) {
                    //if OK, reading data from db and setting adapter
                    case TripService.RESULT_OK:
                        trips = dbHelper.getAllTrips();
                        setItems(trips);
                        tripsRecyclerView.getAdapter().notifyDataSetChanged();
                        messageEmpty.setVisibility(View.GONE);
                        break;
                    // if result is empty, clear list and show message
                    case TripService.RESULT_EMPTY:
                        messageEmpty.setVisibility(View.VISIBLE);
                        trips.clear();
                        tripsRecyclerView.getAdapter().notifyDataSetChanged();
                        break;
                    //if error, show dialog with error message and button "Try again"
                    case TripService.RESULT_ERROR:
                        String error = intent.getStringExtra(TripService.ERROR);
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity(), R.style.AppTheme);
                        builder.setTitle("Something went wrong");
                        builder.setMessage("Network error. Check yout Internet connection and try again");
                        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                disableUI();
                                getActivity().startService(new Intent(getActivity(), TripService.class));
                            }
                        });

                        builder.setCancelable(false);
                        AlertDialog dlg = builder.create();
                        dlg.getWindow().setLayout(800, 450);
                        dlg.show();
                        break;
                }
                enableUI();
                dbHelper = HelperFactory.geHelper(getActivity(), DBMS);

            }
        };

        //check, if db has stored data. If no, making API request
        if (dbHelper.isEmpty()) {
            FileLogger.logInFile(TAG, "db is empty, downloading data from server", getActivity());
            Log.e("EE", "Empty");
            disableUI();
            dbHelper.closeConnection();
            dbHelper = null;
            getActivity().startService(new Intent(getActivity(), TripService.class));
        } else {
            Log.e("EE", "not empty");
            FileLogger.logInFile(TAG, "db isn't empty, downloading data from db", getActivity());
            trips = dbHelper.getAllTrips();

        }
        Log.e("EE", String.valueOf(trips.size()));
        setItems(trips);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.change_bd_menu, menu);
        if (settings.getDBMS().equals(SettingsManager.SQLITE)) {
            menu.getItem(0).setTitle("current db: " + SettingsManager.SQLITE + "" +
                    ". Change to " + SettingsManager.REALM);
        } else {
            menu.getItem(0).setTitle("current db: " + SettingsManager.REALM + "" +
                    ". Change to " + SettingsManager.SQLITE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_change:
                if (settings.getDBMS().equals(SettingsManager.SQLITE)) {
                    item.setTitle("current db: " + SettingsManager.REALM + "" +
                            ". Change to " + SettingsManager.SQLITE);
                    DBMS = SettingsManager.REALM;
                    FileLogger.logInFile(TAG, "changing db to realm. Downloading data from server", getActivity());
                } else {
                    item.setTitle("current db: " + SettingsManager.SQLITE + "" +
                            ". Change to " + SettingsManager.REALM);
                    FileLogger.logInFile(TAG, "changing db to sqlite. Downloading data from server", getActivity());
                    DBMS = SettingsManager.SQLITE;
                }
                settings.setDBMS(DBMS);

                dbHelper = HelperFactory.geHelper(getActivity(), DBMS);
                disableUI();
                dbHelper.closeConnection();
                getActivity().startService(new Intent(getActivity(), TripService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tracker.setScreenName("Trip List");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());

        // registering receiver to get data from service
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(TripService.NOTIFICATION));
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        dbHelper.closeConnection();
        super.onDestroy();
        unbinder.unbind();
    }

    public void setItems(List<Trip> trips) {
        tripsRecyclerView.setAdapter(new TripAdapter(trips));
    }

    //disable UI while performing API request
    public void disableUI() {
        progressMessage.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUI() {
        progressMessage.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    class TripHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.trip_id_text_view)
        TextView tripIdTextView;
        @BindView(R.id.trip_from_text_view)
        TextView tripFromTextView;
        @BindView(R.id.trip_to_text_view)
        TextView tripToTextView;
        @BindView(R.id.trip_date_text_view)
        TextView tripDateTextView;
        @BindView(R.id.trip_time_text_view)
        TextView tripTimeTextView;

        private Trip trip;

        public TripHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindTrip(Trip trip) {
            tripIdTextView.setText(String.valueOf(trip.getTripId()));
            tripFromTextView.setText(trip.getFromCity().getName());
            tripToTextView.setText(trip.getToCity().getName());
            tripDateTextView.setText(trip.getFromDate());
            tripTimeTextView.setText(trip.getFromTime());
            this.trip = trip;
        }

        @Override
        public void onClick(View view) {
            //show detail info about selected trip
            Log.i(TAG, "Trip selected");
            callbacks.onTripSelected(trip);
        }
    }

    private class TripAdapter extends RecyclerView.Adapter<TripHolder> {

        private List<Trip> trips;

        public TripAdapter(List<Trip> trips) {
            this.trips = trips;
        }

        @Override
        public TripHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.list_item_trip, parent, false);
            return new TripHolder(v);
        }

        @Override
        public void onBindViewHolder(TripHolder holder, int position) {
            Trip trip = trips.get(position);
            holder.bindTrip(trip);
        }

        @Override
        public int getItemCount() {
            return trips.size();
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getActivity(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }

}
