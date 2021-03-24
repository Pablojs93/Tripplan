package com.pjas.tripplan.Classes.Database.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.pjas.tripplan.App.MyTrips.TripDetails
import com.pjas.tripplan.Classes.Database.Model.Trip
import com.pjas.tripplan.R

class TripRecyclerViewAdapter (
    private val tripsList: MutableList<Trip>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore)
    : RecyclerView.Adapter<TripRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripRecyclerViewAdapter.ViewHolder{
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.trip_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tripsList.size
    }

    override fun onBindViewHolder(holder: TripRecyclerViewAdapter.ViewHolder, position: Int) {
        val trip = tripsList[position]

        holder!!.name.text = trip.name
        holder!!.places.text = trip.places
        holder!!.begining.text = trip.begining
        holder!!.end.text = trip.end

        holder.details.setOnClickListener{
            getDetails(trip)
        }
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var name: TextView
        internal var places: TextView
        internal var begining: TextView
        internal var end: TextView
        internal var details: ImageButton

        init {
            name = view.findViewById(R.id.tv_TripNameT)
            places = view.findViewById(R.id.tv_TripPlaceT)

            begining = view.findViewById(R.id.tv_TripBeginingT)
            end = view.findViewById(R.id.tv_TripEndT)

            details = view.findViewById(R.id.b_TripDetailsT)
        }
    }

    private fun getDetails(trip: Trip){
        val intent = Intent(context, TripDetails::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("tripID", trip.id)
        context.startActivity(intent)
    }

    /*private fun updateNote(note: Note) {
        val intent = Intent(context, NoteActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateNoteId", note.id)
        intent.putExtra("UpdateNoteTitle", note.title)
        intent.putExtra("UpdateNoteContent", note.content)
        context.startActivity(intent)
    }

    private fun deleteNote(id: String, position: Int) {
        firestoreDB.collection("notes")
            .document(id)
            .delete()
            .addOnCompleteListener {
                notesList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, notesList.size)
                Toast.makeText(context, "Note has been deleted!", Toast.LENGTH_SHORT).show()
            }
    }*/

}