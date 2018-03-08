package com.makhovyk.android.tripservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.makhovyk.android.tripservice.Model.Trip;


public class TripActivity extends AppCompatActivity{

    private static final String EXTRA_TRIP =
            "com.makhovyk.android.tripservice.trip";

    public static Intent newIntent(Context packageContext, Trip trip) {
        Intent intent = new Intent(packageContext, TripActivity.class);
        intent.putExtra(EXTRA_TRIP, trip);
        return intent;
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);
            if (fragment == null) {
                Trip trip = (Trip) getIntent().getSerializableExtra(EXTRA_TRIP);
                fragment = TripFragment.newInstance(trip);
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
