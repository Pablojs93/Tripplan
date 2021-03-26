package com.pjas.tripplan.App.CreateTrip.SharedTrip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pjas.tripplan.R

class NoSharedTrip : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.no_shared_trip_layout)
    }
}