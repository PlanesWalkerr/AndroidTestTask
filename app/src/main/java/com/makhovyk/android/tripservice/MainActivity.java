package com.makhovyk.android.tripservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.makhovyk.android.tripservice.Model.Trip;

public class MainActivity extends AppCompatActivity implements ListFragment.Callbacks {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterdetail);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new ListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onTripSelected(Trip trip) {
        // start single trip activity if small screen layout is used
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = TripActivity.newIntent(this, trip.getTripId());
            startActivity(intent);
        } else {
            // show trip details in container to the right on tablet
            Fragment newDetail = TripFragment.newInstance(trip.getTripId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }
}
