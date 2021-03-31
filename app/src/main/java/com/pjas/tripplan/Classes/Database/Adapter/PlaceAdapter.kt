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
import com.pjas.tripplan.Classes.Database.Model.TripPlace
import com.pjas.tripplan.R

class PlaceAdapter (
    private val placesList: List<TripPlace>?,
    private val context: Context
)
    : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceAdapter.ViewHolder{
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.places_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return placesList!!.size
    }

    override fun onBindViewHolder(holder: PlaceAdapter.ViewHolder, position: Int) {
        val place = placesList?.get(position)

        holder!!.place.text = place?.place
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var place: TextView

        init {
            place = view.findViewById(R.id.tv_PlaceP)
        }
    }


    /*private fun updateNote(note: Note) {
        val intent = Intent(context, NoteActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateNoteId", note.id)
        intent.putExtra("UpdateNoteTitle", note.title)
        intent.putExtra("UpdateNoteContent", note.content)
        context.startActivity(intent)
    }*/

    /*private fun deleteNote(id: String, position: Int) {
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