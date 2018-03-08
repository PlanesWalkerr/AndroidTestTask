package com.makhovyk.android.tripservice;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makhovyk.android.tripservice.Model.Trip;


public class TripFragment extends Fragment {

    private static final String ARG_TRIP = "trip";

    private long id;
    private Trip trip;
    private TextView tripIdTextView;
    private TextView fromCityIdTextView;
    private TextView fromCityHlTextView;
    private TextView fromCityNameTextView;
    private TextView toCityIdTextView;
    private TextView toCityHlTextView;
    private TextView toCityNameTextView;
    private TextView fromDateTimeTextView;
    private TextView fromInfoTextView;
    private TextView toDateTimeTextView;
    private TextView toInfoTextView;
    private TextView infoTextView;
    private TextView priceTextView;
    private TextView busIdTextView;
    private TextView reservationCountTextView;

    public static TripFragment newInstance(Trip t){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, t);
        TripFragment fragment = new TripFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trip = (Trip) getArguments().getSerializable(ARG_TRIP);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trip,container,false);

        Resources resources = getResources();
        tripIdTextView = v.findViewById(R.id.trip_id_text_view);
        tripIdTextView.setText(String.format(resources.getString(R.string.trip_id),trip.getTripId()));
        fromCityIdTextView =v.findViewById(R.id.from_city_id_text_view);
        fromCityIdTextView.setText(String.format(resources.getString(R.string.city_id),trip.getFromCity().getCityId()));
        fromCityHlTextView =v.findViewById(R.id.from_city_hl_text_view);
        fromCityHlTextView.setText(String.format(resources.getString(R.string.city_highlight),trip.getFromCity().getHighlight()));
        fromCityNameTextView =v.findViewById(R.id.from_city_name_text_view);
        fromCityNameTextView.setText(String.format(resources.getString(R.string.city_name),trip.getFromCity().getName()));
        toCityIdTextView =v.findViewById(R.id.to_city_id_text_view);
        toCityIdTextView.setText(String.format(resources.getString(R.string.city_id),trip.getToCity().getCityId()));
        toCityHlTextView =v.findViewById(R.id.to_city_hl_text_view);
        toCityHlTextView.setText(String.format(resources.getString(R.string.city_highlight),trip.getToCity().getHighlight()));
        toCityNameTextView =v.findViewById(R.id.to_city_name_text_view);
        toCityNameTextView.setText(String.format(resources.getString(R.string.city_name),trip.getToCity().getName()));
        fromDateTimeTextView =v.findViewById(R.id.from_date_time_text_view);
        fromDateTimeTextView.setText(String.format(resources.getString(R.string.from_date_time),trip.getFromDate(),trip.getFromTime()));
        fromInfoTextView =v.findViewById(R.id.from_info_text_view);
        fromInfoTextView.setText(String.format(resources.getString(R.string.from_info),trip.getFromInfo()));
        toDateTimeTextView =v.findViewById(R.id.to_date_time_text_view);
        toDateTimeTextView.setText(String.format(resources.getString(R.string.to_date_time),trip.getToDate(),trip.getToTime()));
        toInfoTextView =v.findViewById(R.id.to_info_text_view);
        toInfoTextView.setText(String.format(resources.getString(R.string.to_info),trip.getToInfo()));
        infoTextView =v.findViewById(R.id.info_text_view);
        infoTextView.setText(String.format(resources.getString(R.string.info),trip.getInfo()));
        priceTextView =v.findViewById(R.id.price_text_view);
        priceTextView.setText(String.format(resources.getString(R.string.price),trip.getPrice()));
        busIdTextView =v.findViewById(R.id.bus_id_text_view);
        busIdTextView.setText(String.format(resources.getString(R.string.bus_id),trip.getBusId()));
        reservationCountTextView =v.findViewById(R.id.reservation_count_text_view);
        reservationCountTextView.setText(String.format(resources.getString(R.string.reservation_count),trip.getReservationCount()));

        return v;
    }
}
