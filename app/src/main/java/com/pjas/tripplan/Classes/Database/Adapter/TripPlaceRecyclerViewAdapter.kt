package com.pjas.tripplan.Classes.Database.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pjas.tripplan.Classes.Database.Model.TripPlace
import com.pjas.tripplan.Classes.Variable.GlobalVariables
import com.pjas.tripplan.R

class TripPlaceRecyclerViewAdapter (private val tripsPlaceList: MutableList<TripPlace>, private val context: Context) : RecyclerView.Adapter<TripPlaceRecyclerViewAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripPlaceRecyclerViewAdapter.ViewHolder
    {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.tripplace_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return tripsPlaceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val tripPlace = tripsPlaceList[position]

        holder!!.place.text = tripPlace.place
        holder!!.begining.text = tripPlace.begining
        holder!!.end.text = tripPlace.end

        holder.edit.setOnClickListener {
            //editPlace(tripPlace, position)
        }

        holder.delete.setOnClickListener {
            deletePlace(tripPlace)
        }
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view)
    {
        internal var place: TextView
        internal var begining: TextView
        internal var end: TextView

        internal var delete: Button
        internal var edit: Button

        init
        {
            place = view.findViewById(R.id.tv_PlaceNameMP)
            begining = view.findViewById(R.id.tv_BeginingDateMP)
            end = view.findViewById(R.id.tv_EndDateMP)

            delete = view.findViewById(R.id.b_DeletePlace)
            edit = view.findViewById(R.id.b_EditPlace)
        }
    }

   /* private fun editPlace(place: TripPlace, position: Int)
    {
        val intent = Intent(context, UpdatePlace::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        var place = TripPlace(place.place.toString(), place.begining.toString(), place.end.toString())
        GlobalVariables.actualPlace = place
        GlobalVariables.placePosition = position
        context.startActivity(intent)
    }*/

    private fun deletePlace(place: TripPlace)
    {
        GlobalVariables.placeList.remove(place)
        notifyDataSetChanged()
    }
}