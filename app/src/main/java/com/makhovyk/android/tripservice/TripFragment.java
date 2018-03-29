package com.makhovyk.android.tripservice;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.makhovyk.android.tripservice.Model.Helper;
import com.makhovyk.android.tripservice.Model.HelperFactory;
import com.makhovyk.android.tripservice.Model.Trip;
import com.makhovyk.android.tripservice.Utils.SettingsManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


public class TripFragment extends Fragment {

    private static final String ARG_TRIP = "trip";

    private String DBMS;

    private long id;
    private Trip trip;
    private Helper dbHelper;
    @BindView(R.id.trip_id_text_view)
    TextView tripIdTextView;
    @BindView(R.id.from_city_id_text_view)
    TextView fromCityIdTextView;
    @BindView(R.id.from_city_hl_text_view)
    TextView fromCityHlTextView;
    @BindView(R.id.from_city_name_text_view)
    TextView fromCityNameTextView;
    @BindView(R.id.to_city_id_text_view)
    TextView toCityIdTextView;
    @BindView(R.id.to_city_hl_text_view)
    TextView toCityHlTextView;
    @BindView(R.id.to_city_name_text_view)
    TextView toCityNameTextView;
    @BindView(R.id.from_date_time_text_view)
    TextView fromDateTimeTextView;
    @BindView(R.id.from_info_text_view)
    TextView fromInfoTextView;
    @BindView(R.id.to_date_time_text_view)
    TextView toDateTimeTextView;
    @BindView(R.id.to_info_text_view)
    TextView toInfoTextView;
    @BindView(R.id.info_text_view)
    TextView infoTextView;
    @BindView(R.id.price_text_view)
    TextView priceTextView;
    @BindView(R.id.bus_id_text_view)
    TextView busIdTextView;
    @BindView(R.id.reservation_count_text_view)
    TextView reservationCountTextView;

    public static TripFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(ARG_TRIP, id);
        TripFragment fragment = new TripFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        Fabric.with(getActivity(), new Crashlytics());

        super.onCreate(savedInstanceState);
        SettingsManager settings = new SettingsManager(getActivity());
        DBMS = settings.getDBMS();
        id = (long) getArguments().getLong(ARG_TRIP);
        TripServiceApp app = TripServiceApp.getInstance();
        dbHelper = app.getHelper(DBMS);
        trip = dbHelper.getTripById(id);
        //Log.e("EE", trip.toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trip, container, false);
        ButterKnife.bind(this, v);
        Resources resources = getResources();

        tripIdTextView.setText(String.format(resources.getString(R.string.trip_id), id));
        fromCityIdTextView.setText(String.format(resources.getString(R.string.city_id), trip.getFromCity().getCityId()));
        fromCityHlTextView.setText(String.format(resources.getString(R.string.city_highlight), trip.getFromCity().getHighlight()));
        fromCityNameTextView.setText(String.format(resources.getString(R.string.city_name), trip.getFromCity().getName()));
        toCityIdTextView.setText(String.format(resources.getString(R.string.city_id), trip.getToCity().getCityId()));
        toCityHlTextView.setText(String.format(resources.getString(R.string.city_highlight), trip.getToCity().getHighlight()));
        toCityNameTextView.setText(String.format(resources.getString(R.string.city_name), trip.getToCity().getName()));
        fromDateTimeTextView.setText(String.format(resources.getString(R.string.from_date_time), trip.getFromDate(), trip.getFromTime()));
        fromInfoTextView.setText(String.format(resources.getString(R.string.from_info), trip.getFromInfo()));
        toDateTimeTextView.setText(String.format(resources.getString(R.string.to_date_time), trip.getToDate(), trip.getToTime()));
        toInfoTextView.setText(String.format(resources.getString(R.string.to_info), trip.getToInfo()));
        infoTextView.setText(String.format(resources.getString(R.string.info), trip.getInfo()));
        priceTextView.setText(String.format(resources.getString(R.string.price), trip.getPrice()));
        busIdTextView.setText(String.format(resources.getString(R.string.bus_id), trip.getBusId()));
        reservationCountTextView.setText(String.format(resources.getString(R.string.reservation_count), trip.getReservationCount()));

        return v;
    }


}
