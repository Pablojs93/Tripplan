package com.pjas.tripplan.App.MyTrips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.pjas.tripplan.Classes.Database.Model.Trip
import com.pjas.tripplan.R
import kotlinx.android.synthetic.main.trips_layout.view.*

class FutureTrips : Fragment(){

    lateinit var mRecyclerView : RecyclerView

    lateinit var mDatabaseReference: DatabaseReference

    //lateinit var firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Trip, TripsViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val x = inflater.inflate(R.layout.oldtrips_layout,null)

        //mRecyclerView = x.findViewById<View>(R.id.rv_FutureTrips) as RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        //mRecyclerView.adapter = firebaseRecyclerAdapter
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Trips")

        //getData()
        return x

    }

    /*private fun getData(){
        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Trip, TripsViewHolder>(
            Trip::class.java,
            R.layout.trips_layout,
            TripsViewHolder::class.java,
            mDatabaseReference
        ){
            override fun populateViewHolder(viewHolder: TripsViewHolder?, model: Trip?, position: Int) {
                viewHolder?.mview?.tv_TripNameT?.setText(model?.tripName)
                viewHolder?.mview?.tv_TripPlaceT?.setText(model?.tripPlaces)
                viewHolder?.mview?.tv_TripFromT?.setText(model?.tripBegining)
                viewHolder?.mview?.tv_TripToT?.setText(model?.tripEnd)
            }
        }*/
}

    /*class TripsViewHolder (var mview : View) : RecyclerView.ViewHolder(mview){

    }*/
