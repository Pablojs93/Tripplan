package com.pjas.tripplan.App.CreateTrip

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pjas.tripplan.App.MyTrips.MyTrips
import com.pjas.tripplan.Classes.Database.Adapter.TripPlaceRecyclerViewAdapter
import com.pjas.tripplan.Classes.Database.Model.Trip
import com.pjas.tripplan.Classes.Database.Model.TripPlace
import com.pjas.tripplan.Classes.NavigationDrawer.ClickListener
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationItemModel
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationRVAdapter
import com.pjas.tripplan.Classes.NavigationDrawer.RecyclerTouchListener
import com.pjas.tripplan.Classes.Variable.GlobalVariables
import com.pjas.tripplan.R
import kotlinx.android.synthetic.main.createtrip_home_layout.activity_main_toolbar
import kotlinx.android.synthetic.main.createtrip_home_layout.navigation_header_img
import kotlinx.android.synthetic.main.createtrip_home_layout.navigation_layout
import kotlinx.android.synthetic.main.createtrip_home_layout.navigation_rv
import kotlinx.android.synthetic.main.add_edit_places_trip_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddPlaces : AppCompatActivity()
{

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
    var id = 0

    val placesList = ArrayList<TripPlace>()
    var sharedWith = GlobalVariables.sharedWithList

    private var firestoreDB: FirebaseFirestore? = null

    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter

    //lateinit var placesClient: PlacesClient

    private var mAdapter: TripPlaceRecyclerViewAdapter? = null


    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "My Trips"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Create Trip"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Signout")
    )

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_places_trip_layout)

        tripName = GlobalVariables.newTripName
        tripType = GlobalVariables.newTripType
        created = GlobalVariables.newTripCreated
        tripBegining = GlobalVariables.newTripBegining
        tripEnd = GlobalVariables.newTripEnd
        //placesClient = Places.createClient(this)

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
                        //val createTrip = CreateTrip_()
                        //supportFragmentManager.beginTransaction().replace(R.id.activity_main_content_id, createTrip).commit()
                        goCreateTrip()
                    }
                    2 ->
                    {
                        // Signout
                        FirebaseAuth.getInstance().signOut()
                        finish()
                    }
                }
                // Don't highlight the 'Profile' and 'Like us on Facebook' item row
                if (position != 6 && position != 4)
                {
                    updateAdapter(position)
                }
                Handler().postDelayed(
                    {
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

        firestoreDB = FirebaseFirestore.getInstance()

        init()
        showBeginingDate()
        showEndDate()

        bAdd!!.setOnClickListener{
            Add()
            Load()
        }

        bNext!!.setOnClickListener{
            Create()
        }

        Load()
    }

    fun showBeginingDate()
    {
        // DatePicker

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            etdBegining.setText(sdf.format(cal.time))
        }

        etdBegining.setOnClickListener{

            Log.d("Clicked", "Interview Date Clicked")

            val dialog = DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
            //dialog.datePicker.minDate = CalendarHelper.getCurrentDateInMills()
            dialog.show()
        }
    }

    fun showEndDate()
    {
        // DatePicker

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            etdEnd.setText(sdf.format(cal.time))
        }

        etdEnd.setOnClickListener {

            Log.d("Clicked", "Interview Date Clicked")

            val dialog = DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
            //dialog.datePicker.minDate = CalendarHelper.getCurrentDateInMills()
            dialog.show()
        }
    }

    fun init()
    {
        val buttonName: String = getString(R.string.bAddPlace)
        bNext = findViewById<View>(R.id.b_NextMP) as Button
        etName = findViewById<View>(R.id.et_TripPlaceMP) as EditText
        etdBegining = findViewById<View>(R.id.et_EmailCT) as EditText
        etdEnd = findViewById<View>(R.id.etd_TripEndMP) as EditText
        bAdd = findViewById<View>(R.id.b_AddPersonCT) as Button
        bAdd.setText(buttonName)

        if(!GlobalVariables.actualPlace.place.isNullOrBlank())
        {
            GlobalVariables.actualPlace = TripPlace()
            GlobalVariables.placePosition = 0
            etName.setText("")
            etdBegining.setText("")
            etdEnd.setText("")
        }
    }

    fun Add()
    {
        val name = etName.text.toString()
        val begining = etdBegining.text.toString()
        val end = etdEnd.text.toString()

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(begining) && !TextUtils.isEmpty(end))
        {
            val place = TripPlace(name, begining, end)
            GlobalVariables.placeList.add(place)
        }
    }

    fun Load()
    {
        mAdapter = TripPlaceRecyclerViewAdapter(GlobalVariables.placeList, applicationContext)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        rv_PlacesMP.layoutManager = mLayoutManager
        rv_PlacesMP.itemAnimator = DefaultItemAnimator()
        rv_PlacesMP.adapter = mAdapter
    }

    fun Create(){
        firestoreDB = FirebaseFirestore.getInstance()

        val trip = Trip(tripName, placesList, sharedWith, tripBegining, tripEnd, tripType, created)

        firestoreDB!!.collection("Trips").add(trip).addOnSuccessListener{ documentReference ->
                Toast.makeText(applicationContext, "Trip created", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
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