package com.pjas.tripplan.App.CreateTrip.MultiplePlaces

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.places.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pjas.tripplan.App.CreateTrip.CreateTrip
import com.pjas.tripplan.App.MyTrips.MyTrips
import com.pjas.tripplan.Classes.Database.Adapter.TripPlaceRecyclerViewAdapter
import com.pjas.tripplan.Classes.Database.Adapter.TripRecyclerViewAdapter
import com.pjas.tripplan.Classes.Database.Model.Trip
import com.pjas.tripplan.Classes.Database.Model.TripPlace
import com.pjas.tripplan.Classes.NavigationDrawer.ClickListener
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationItemModel
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationRVAdapter
import com.pjas.tripplan.Classes.NavigationDrawer.RecyclerTouchListener
import com.pjas.tripplan.R
import kotlinx.android.synthetic.main.createtrip_home_layout.*
import kotlinx.android.synthetic.main.createtrip_home_layout.activity_main_toolbar
import kotlinx.android.synthetic.main.createtrip_home_layout.navigation_header_img
import kotlinx.android.synthetic.main.createtrip_home_layout.navigation_layout
import kotlinx.android.synthetic.main.createtrip_home_layout.navigation_rv
import kotlinx.android.synthetic.main.multiple_places_trip_layout.*
import kotlinx.android.synthetic.main.mytrips_home_layout.*

class MultiplePlacesTrip : AppCompatActivity() {

    private lateinit var bNext: Button
    private lateinit var bAdd: Button
    private lateinit var etName: EditText
    private lateinit var etdBegining: EditText
    private lateinit var etdEnd: EditText

    //var docId = ""
    var tripName = ""
    var tripType = ""
    var created = ""
    var tripBegining = ""
    var tripEnd = ""

    val placesList = ArrayList<TripPlace>()
    val sharedWith = ArrayList<String>()

    private var firestoreDB: FirebaseFirestore? = null

    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter

    //lateinit var placesClient: PlacesClient

    private var sharedTrip: Boolean? = false
    private var multiplePlaces: Boolean? = false

    private var mAdapter: TripPlaceRecyclerViewAdapter? = null


    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "My Trips"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Create Trip"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Signout")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multiple_places_trip_layout)

        var bundle: Bundle? = intent.extras
        //docId = bundle!!.getString("id").toString()
        tripName = bundle!!.getString("name").toString()
        tripType = bundle!!.getString("type").toString()
        created = bundle!!.getString("created").toString()
        tripBegining = bundle!!.getString("begining").toString()
        tripEnd = bundle!!.getString("end").toString()

        //placesClient = Places.createClient(this)

        drawerLayout = findViewById(R.id.drawer_layout)

        // Set the toolbar
        setSupportActionBar(activity_main_toolbar)

        // Setup Recyclerview's Layout
        navigation_rv.layoutManager = LinearLayoutManager(this)
        navigation_rv.setHasFixedSize(true)

        // Add Item Touch Listener
        navigation_rv.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        // # My trips Fragment
                        goMyTrips()
                    }
                    1 -> {
                        // Create trip Fragment
                        //val createTrip = CreateTrip_()
                        //supportFragmentManager.beginTransaction().replace(R.id.activity_main_content_id, createTrip).commit()
                        goCreateTrip()
                    }
                    2 -> {
                        // Signout
                        FirebaseAuth.getInstance().signOut()
                        finish()
                    }
                }
                // Don't highlight the 'Profile' and 'Like us on Facebook' item row
                if (position != 6 && position != 4) {
                    updateAdapter(position)
                }
                Handler().postDelayed({
                    drawerLayout.closeDrawer(GravityCompat.START)
                }, 200)
            }
        }))

        // Update Adapter with item data and highlight the default menu item ('Home' Fragment)
        updateAdapter(1)

        // Set 'Home' as the default fragment when the app starts
        //val myTrips = MyTrips_()
        //supportFragmentManager.beginTransaction().replace(R.id.activity_main_content_id, myTrips).commit()

        // Close the soft keyboard when you open or close the Drawer
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, activity_main_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        // Set Header Image
        navigation_header_img.setImageResource(R.drawable.ic_baseline_airplanemode_active_24)

        // Set background of Drawer
        navigation_layout.setBackgroundColor(
            ContextCompat.getColor(this,
            R.color.colorPrimary
        ))

        firestoreDB = FirebaseFirestore.getInstance()

        init()

        bAdd!!.setOnClickListener{
            Add()
            Load()
        }

        bNext!!.setOnClickListener{
            Create()
        }
    }

    fun init(){
        bNext = findViewById<View>(R.id.b_NextMP) as Button
        etName = findViewById<View>(R.id.et_TripPlaceMP) as EditText
        etdBegining = findViewById<View>(R.id.etd_TripBeginingMP) as EditText
        etdEnd = findViewById<View>(R.id.etd_TripEndMP) as EditText
        bAdd = findViewById<View>(R.id.b_AddPlaceMP) as Button
    }

    fun Add(){
        val tripName = etName.text.toString()
        val tripBegining = etdBegining.text.toString()
        val tripEnd = etdEnd.text.toString()

        if(!TextUtils.isEmpty(tripName) && !TextUtils.isEmpty(tripName) && !TextUtils.isEmpty(tripName)){

            firestoreDB = FirebaseFirestore.getInstance()
            val place = TripPlace(tripName, tripBegining, tripEnd)

            firestoreDB!!.collection("TripPlaces")
                .add(place)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(applicationContext, "Trip place created",
                        Toast.LENGTH_SHORT).show()
                    placesList.add(place)
                }.addOnFailureListener {
                    Toast.makeText(
                        applicationContext, "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        /*val simpleDateFormat = DateTimeFormatter.ISO_DATE
        val current = LocalDate.parse(Date().toString(), simpleDateFormat)
        val beginingDate = LocalDate.parse(tripBegining, simpleDateFormat)
        val endDate = LocalDate.parse(tripEnd, simpleDateFormat)

        if(beginingDate.isAfter(current) && !beginingDate.isEqual(current)){
            if(endDate.isAfter(current) && !endDate.isEqual(current)){
                if(endDate.isAfter(beginingDate) && !endDate.isEqual(beginingDate)){
                    Toast.makeText(applicationContext, "Tudo ok!",
                        Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(applicationContext, "Data de fim anterior à data de inicio!",
                        Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(applicationContext, "Data de fim anterior à data atual!",
                    Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(applicationContext, "Data de inicio anterior à data atual!",
                Toast.LENGTH_SHORT).show()*/

        /*val current = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val now = current.format(formatter)

        var dateVerification: Int = 0
        if(verifyDates(now, tripBegining))
            if(verifyDates(now, tripEnd))
                if(verifyDates(tripBegining, tripEnd))
                    dateVerification = 1

        if(!TextUtils.isEmpty(tripName) && !TextUtils.isEmpty(tripPlace) && !TextUtils.isEmpty(tripBegining) && !TextUtils.isEmpty(tripEnd)){
            if(dateVerification == 1){

                goMyTrips()
            }
            else
                Toast.makeText(
                    applicationContext, "Error",
                    Toast.LENGTH_SHORT)
        }*/
    }

    fun Load(){
        firestoreDB!!
            .collection("TripPlaces")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val placesList = mutableListOf<TripPlace>()

                    for (doc in task.result) {
                        val place = doc.toObject<TripPlace>(TripPlace::class.java)
                        place.id = doc.id
                        placesList.add(place)
                    }

                    mAdapter = TripPlaceRecyclerViewAdapter(placesList, applicationContext, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    rv_PlacesMP.layoutManager = mLayoutManager
                    rv_PlacesMP.itemAnimator = DefaultItemAnimator()
                    rv_PlacesMP.adapter = mAdapter
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }
    }

    fun Create(){
        firestoreDB = FirebaseFirestore.getInstance()

        val trip = Trip(tripName, placesList, sharedWith, tripBegining, tripEnd, tripType, created)

        firestoreDB!!.collection("Trips")
            .add(trip)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(applicationContext, "Trip created",
                    Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(
                    applicationContext, "Error",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun goMyTrips(){
        val intent = Intent(this, MyTrips::class.java)
        startActivity(intent)
    }

    fun goCreateTrip(){
        val intent = Intent(this, CreateTrip::class.java)
        startActivity(intent)
    }

    private fun updateAdapter(highlightItemPos: Int) {
        adapter = NavigationRVAdapter(items, highlightItemPos)
        navigation_rv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Go to the previous fragment
                supportFragmentManager.popBackStack()
            } else {
                // Exit the app
                super.onBackPressed()
            }
        }
    }
}