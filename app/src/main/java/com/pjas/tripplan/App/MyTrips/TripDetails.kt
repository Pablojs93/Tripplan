package com.pjas.tripplan.App.MyTrips

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.pjas.tripplan.Classes.Database.Model.Trip
import com.pjas.tripplan.R
import org.w3c.dom.Text

class TripDetails : AppCompatActivity() {

    private var firestoreDB: FirebaseFirestore? = null
    var tripID : String? = null

    private lateinit var name: TextView
    private lateinit var place: TextView
    private lateinit var begining: TextView
    private lateinit var end: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tripdetails_layout)

        firestoreDB = FirebaseFirestore.getInstance()
        tripID = intent.getStringExtra("tripID").toString()

        name = findViewById(R.id.tv_TripNameTD)
        place = findViewById(R.id.tv_TripPlaceTD)
        begining = findViewById(R.id.tv_TripBeginingTD)
        end = findViewById(R.id.tv_TripEndTD)

        //getData()
    }

    /*private fun getData(){
        val data = firestoreDB?.collection("Trips")?.document(tripID!!)
        data?.get()?.addOnSuccessListener { documentSnapshot ->
            val trip=documentSnapshot.toObject(Trip::class.java)

            name.setText(trip?.name)
            place.setText(trip?.places)
            begining.setText(trip?.begining)
            end.setText(trip?.end)
        }
    }*/
}