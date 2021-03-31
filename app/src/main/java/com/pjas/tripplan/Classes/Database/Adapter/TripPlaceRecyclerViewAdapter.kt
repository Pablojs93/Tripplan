package com.pjas.tripplan.Classes.Database.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.pjas.tripplan.App.MyTrips.TripDetails
import com.pjas.tripplan.Classes.Database.Model.Trip
import com.pjas.tripplan.Classes.Database.Model.TripPlace
import com.pjas.tripplan.R

class TripPlaceRecyclerViewAdapter (
    private val tripsPlaceList: MutableList<TripPlace>,
    private val context: Context
    )
    : RecyclerView.Adapter<TripPlaceRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripPlaceRecyclerViewAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent!!.context).inflate(R.layout.tripplace_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tripsPlaceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tripPlace = tripsPlaceList[position]

        holder!!.place.text = tripPlace.place
        holder!!.begining.text = tripPlace.begining
        holder!!.end.text = tripPlace.end

        /*holder.details.setOnClickListener {
            getDetails(trip)
        }*/
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var place: TextView
        internal var begining: TextView
        internal var end: TextView
        internal var delete: ImageButton

        init {
            place = view.findViewById(R.id.tv_Place)
            begining = view.findViewById(R.id.tv_Begining)
            end = view.findViewById(R.id.tv_End)

            delete = view.findViewById(R.id.b_Delete)
        }
    }
}