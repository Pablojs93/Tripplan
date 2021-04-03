package com.pjas.tripplan.App.MyTrips

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.pjas.tripplan.App.CreateTrip.CreateTrip
import com.pjas.tripplan.Classes.Database.Adapter.TripRecyclerViewAdapter
import com.pjas.tripplan.Classes.Database.Model.SharedWith
import com.pjas.tripplan.Classes.Database.Model.Trip
import com.pjas.tripplan.Classes.NavigationDrawer.ClickListener
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationItemModel
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationRVAdapter
import com.pjas.tripplan.Classes.NavigationDrawer.RecyclerTouchListener
import com.pjas.tripplan.Login.Login
import com.pjas.tripplan.R
import kotlinx.android.synthetic.main.mytrips_home_layout.*
import kotlinx.android.synthetic.main.trips_layout.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

class MyTrips : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter

    private var mAdapter: TripRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null

    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "My Trips"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Create Trip"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Signout")
    )


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mytrips_home_layout)

        drawerLayout = findViewById(R.id.drawer_layout)

        // Set the toolbar
        setSupportActionBar(activity_main_toolbar)

        // Setup Recyclerview's Layout
        navigation_rv.layoutManager = LinearLayoutManager(this)
        navigation_rv.setHasFixedSize(true)

        // Add Item Touch Listener
        navigation_rv.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener
        {
            override fun onClick(view: View, position: Int)
            {
                when (position)
                {
                    0 ->
                    {
                        // # My trips Fragment
                        goMyTrips()
                    }
                    1 ->
                    {
                        // Create trip Fragment
                        goCreateTrip()
                    }
                    2 ->
                    {
                        // Signout
                        goLogin()
                    }
                }
                // Don't highlight the 'Profile' and 'Like us on Facebook' item row
                if (position != 6 && position != 4)
                {
                    updateAdapter(position)
                }
                Handler().postDelayed({
                    drawerLayout.closeDrawer(GravityCompat.START)
                }, 200)
            }
        }))

        // Update Adapter with item data and highlight the default menu item ('Home' Fragment)
        updateAdapter(0)

        // Set 'Home' as the default fragment when the app starts
        //val myTrips = MyTrips_()
        //supportFragmentManager.beginTransaction().replace(R.id.activity_main_content_id, myTrips).commit()

        // Close the soft keyboard when you open or close the Drawer
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, activity_main_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            override fun onDrawerClosed(drawerView: View)
            {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try
                {
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }
                catch (e: Exception)
                {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View)
            {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try
                {
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                }
                catch (e: Exception)
                {
                    e.stackTrace
                }
            }
        }
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        // Set Header Image
        navigation_header_img.setImageResource(R.drawable.ic_baseline_airplanemode_active_24)

        // Set background of Drawer
        navigation_layout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

        val current = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val now = current.format(formatter)

        firestoreDB = FirebaseFirestore.getInstance()

        //loadMyTripsList()
        loadSharedTripsList()

        val email = FirebaseAuth.getInstance().currentUser.email

        /*firestoreListener = firestoreDB!!
            .collection("Trips")
            .whereEqualTo("created", id)
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    //Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                val tripsList = mutableListOf<Trip>()

                if (documentSnapshots != null) {
                    for (doc in documentSnapshots) {
                        val trip = doc.toObject(Trip::class.java)
                        trip.id = doc.id
                        tripsList.add(trip)
                    }
                }

                mAdapter = TripRecyclerViewAdapter(tripsList, applicationContext, firestoreDB!!)
                rv_FutureTrips.adapter = mAdapter
            })*/

        firestoreDB!!.collection("Trips").get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val tripsList = mutableListOf<Trip>()
                val myTripsList = mutableListOf<Trip>()
                var shared = mutableListOf<SharedWith>()

                for (doc in task.result)
                {
                    val trip = doc.toObject<Trip>(Trip::class.java)
                    trip.id = doc.id
                    tripsList.add(trip)
                }

                for (trip: Trip in tripsList)
                {
                    var dateVerification: Int = 0
                    if(verifyDates(now, trip.tripBegining.toString()))
                        dateVerification = 1

                    if(dateVerification == 1)
                    {
                        shared = trip.sharedWith as MutableList<SharedWith>
                        for (s: SharedWith in shared)
                        {
                            if (s.email.equals(email))
                                myTripsList.add(trip)
                        }
                    }
                }

                mAdapter = TripRecyclerViewAdapter(myTripsList, applicationContext, firestoreDB!!)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                rv_FutureTrips.layoutManager = mLayoutManager
                rv_FutureTrips.itemAnimator = DefaultItemAnimator()
                rv_FutureTrips.adapter = mAdapter
            }
            else
            {
                Log.d("TAG", "Error getting documents: ", task.exception)
            }
        }
    }

    fun verifyDates(f: String, s: String) : Boolean{
        val delim = "/"

        val first = Pattern.compile(delim).split(f.toString())
        val second = Pattern.compile(delim).split(s.toString())

        val dayFirst: Int = first.get(0).toInt()
        val monthFirst: Int = first.get(1).toInt()
        val yearFirst: Int = first.get(2).toInt()

        val daySecond: Int = second.get(0).toInt()
        val monthSecond: Int = second.get(1).toInt()
        val yearSecond: Int = second.get(2).toInt()

        if(yearSecond > yearFirst)
            return true
        if(yearSecond == yearFirst && monthSecond > monthFirst)
            return true
        if(yearSecond == yearFirst && monthSecond == monthFirst && daySecond > dayFirst)
            return true

        return false
    }

    private fun loadSharedTripsList()
    {
        val email = FirebaseAuth.getInstance().currentUser.email

        val current = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val now = current.format(formatter)

        firestoreDB!!.collection("Trips").get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val tripsList = mutableListOf<Trip>()
                val myTripsList = mutableListOf<Trip>()
                var shared = mutableListOf<SharedWith>()
                for (doc in task.result)
                {
                    val trip = doc.toObject<Trip>(Trip::class.java)
                    trip.id = doc.id
                    tripsList.add(trip)
                }

                for (trip: Trip in tripsList)
                {

                    var dateVerification: Int = 0
                    if (verifyDates(now, trip.tripBegining.toString()))
                        dateVerification = 1

                    if (dateVerification == 1)
                    {
                        shared = trip.sharedWith as MutableList<SharedWith>
                        for (s: SharedWith in shared)
                        {
                            if (s.email.equals(email))
                                myTripsList.add(trip)
                        }
                    }


                    mAdapter = TripRecyclerViewAdapter(myTripsList, applicationContext, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    rv_FutureTrips.layoutManager = mLayoutManager
                    rv_FutureTrips.itemAnimator = DefaultItemAnimator()
                    rv_FutureTrips.adapter = mAdapter
                }
            }
            else
            {
                Log.d("TAG", "Error getting documents: ", task.exception)
            }
        }
    }

    /*private fun loadMyTripsList() {
        val id = FirebaseAuth.getInstance().currentUser.uid
        firestoreDB!!
            .collection("Trips")
            .whereEqualTo("created", id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tripsList = mutableListOf<Trip>()

                    for (doc in task.result) {
                        val trip = doc.toObject<Trip>(Trip::class.java)
                        trip.id = doc.id
                        tripsList.add(trip)
                    }

                    mAdapter = TripRecyclerViewAdapter(tripsList, applicationContext, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    rv_FutureTrips.layoutManager = mLayoutManager
                    rv_FutureTrips.itemAnimator = DefaultItemAnimator()
                    rv_FutureTrips.adapter = mAdapter
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }
    }*/

    // // View Holder Class

    fun goLogin()
    {
        FirebaseAuth.getInstance().signOut()
        finish()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun goMyTrips()
    {
        val intent = Intent(this, MyTrips::class.java)
        startActivity(intent)
    }

    fun goCreateTrip()
    {
        val intent = Intent(this, CreateTrip::class.java)
        startActivity(intent)
    }

    private fun updateAdapter(highlightItemPos: Int)
    {
        adapter = NavigationRVAdapter(items, highlightItemPos)
        navigation_rv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else
        {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0)
            {
                // Go to the previous fragment
                supportFragmentManager.popBackStack()
            }
            else
            {
                // Exit the app
                super.onBackPressed()
            }
        }
    }
}