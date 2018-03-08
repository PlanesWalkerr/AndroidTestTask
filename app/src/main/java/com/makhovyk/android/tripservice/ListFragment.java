package com.makhovyk.android.tripservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.makhovyk.android.tripservice.Model.DBHelper;
import com.makhovyk.android.tripservice.Model.Trip;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {

    private BroadcastReceiver broadcastReceiver;
    private String resultMessage;
    private DBHelper dbHelper;
    private List<Trip> trips = new ArrayList<Trip>();

    private RecyclerView tripsRecyclerView;

    private TextView progressMessage;
    private TextView messageEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Callbacks callbacks;

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

        dbHelper = new DBHelper(getActivity());

        setRetainInstance(true);
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list, container, false);

        tripsRecyclerView = view.findViewById(R.id.trips_recycler_view);
        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressMessage = view.findViewById(R.id.progress_message_text_view);
        progressMessage.setText(getResources().getString(R.string.progress_message));
        messageEmpty = view.findViewById(R.id.message_empty_text_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        //disabling UI and starting API request on swipe down
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                disableUI();
                getActivity().startService(new Intent(getActivity(), TripService.class));
            }

        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //receiving message from service
                resultMessage = intent.getExtras().getString(TripService.RESULT);
                switch (resultMessage){
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
                        dlg.getWindow().setLayout(800,450);
                        dlg.show();
                        break;
                }
                enableUI();

            }
        };

        //check, if db has stored data. If no, making API request
        if (dbHelper.isEmpty()) {
            disableUI();
            getActivity().startService(new Intent(getActivity(), TripService.class));
        }else {
            trips = dbHelper.getAllTrips();

        }
        setItems(trips);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // registering receiver to get data from service
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(TripService.NOTIFICATION));
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }


    public void setItems(List<Trip> trips){
        tripsRecyclerView.setAdapter(new TripAdapter(trips));
    }

    //disable UI while performing API request
    public void disableUI(){
        progressMessage.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUI(){
        progressMessage.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private class TripHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tripIdTextView;
        private TextView tripFromTextView;
        private TextView tripToTextView;
        private TextView tripDateTextView;
        private TextView tripTimeTextView;

        private Trip trip;

        public TripHolder(View itemView) {
            super(itemView);
            tripIdTextView = itemView.findViewById(R.id.trip_id_text_view);
            tripFromTextView = itemView.findViewById(R.id.trip_from_text_view);
            tripToTextView = itemView.findViewById(R.id.trip_to_text_view);
            tripDateTextView = itemView.findViewById(R.id.trip_date_text_view);
            tripTimeTextView = itemView.findViewById(R.id.trip_time_text_view);
            itemView.setOnClickListener(this);
        }

        void bindTrip( Trip trip){
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
            callbacks.onTripSelected(trip);
        }
    }

    private class TripAdapter extends RecyclerView.Adapter<TripHolder>{

        private List<Trip> trips;

        public TripAdapter(List<Trip> trips) {
            this.trips = trips;
        }

        @Override
        public TripHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.list_item_trip,parent,false);
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

}
