package com.pjas.tripplan.App.CreateTrip

import `in`.madapps.placesautocomplete.PlaceAPI
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.snapshot.BooleanNode
import com.google.firebase.firestore.FirebaseFirestore
import com.pjas.tripplan.App.MyTrips.MyTrips
import com.pjas.tripplan.Classes.Database.Model.Trip
import com.pjas.tripplan.Classes.NavigationDrawer.ClickListener
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationItemModel
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationRVAdapter
import com.pjas.tripplan.Classes.NavigationDrawer.RecyclerTouchListener
import com.pjas.tripplan.R
import kotlinx.android.synthetic.main.createtrip_home_layout.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern


class CreateTrip : AppCompatActivity() {

    val placesApi = PlaceAPI.Builder()
        .apiKey("AIzaSyDvz6yilc-vfRRlTgMUJMRRwYdl394fuzM")
        .build(this@CreateTrip)

    var street = ""
    var city = ""
    var state = ""
    var country = ""
    var zipCode = ""

    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter
    private lateinit var  autocompleteFragment: AutocompleteSupportFragment

    var apiKey = ""

    lateinit var placesClient: PlacesClient

    private lateinit var bCreate: Button
    private lateinit var etBegining: EditText
    private lateinit var etEnd: EditText

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String = ""

    var cal = Calendar.getInstance()

    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "My Trips"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Create Trip"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Signout")
    )


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createtrip_home_layout)

        apiKey = getString(R.string.api_key)

        if(!Places.isInitialized())
            Places.initialize(applicationContext,apiKey)

        placesClient = Places.createClient(this)

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
        navigation_layout.setBackgroundColor(ContextCompat.getColor(this,
            R.color.colorPrimary
        ))

        val dateSetListenerBegining = object: DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                updateDateBegining()
            }
        }

        val dateSetListenerEnd = object: DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                updateDateEnd()
            }
        }

        etBegining = findViewById<View>(R.id.et_BeginingCT) as EditText
        etBegining.showSoftInputOnFocus = false

        etBegining.setOnClickListener{

        }

        etBegining.setOnTouchListener { v, event ->
            DatePickerDialog(
                this@CreateTrip,
                dateSetListenerEnd,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
            true
        }

        etEnd = findViewById<View>(R.id.et_EndCT) as EditText
        etEnd.showSoftInputOnFocus = false

        etEnd.setOnTouchListener { v, event ->
            DatePickerDialog(
                this@CreateTrip,
                dateSetListenerEnd,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
            true
        }

        bCreate = findViewById<View>(R.id.b_Create) as Button
        bCreate!!.setOnClickListener{
            Create()
        }
    }

    //private fun showDatePick

    private fun updateDateBegining() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etBegining.setText(sdf.format(cal.getTime()))
    }

    private fun updateDateEnd() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etEnd.setText(sdf.format(cal.getTime()))
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

        if(yearSecond > yearFirst || yearFirst == yearSecond) {
            if (monthSecond > monthFirst || monthFirst == monthSecond) {
                if (daySecond > dayFirst)
                    return true
                else
                    return false
            } else
                return false
        }
        else
            return false
    }


    fun Create(){
        val tripName = et_NameTripCT.text.toString()
        val tripPlace = et_PlaceTripCT.text.toString()
        val tripBegining = et_BeginingCT.text.toString()
        val tripEnd = et_EndCT.text.toString()
        val created = FirebaseAuth.getInstance().currentUser.uid
        val sharedWith = ""


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

        val current = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val now = current.format(formatter)

        var dateVerification: Int = 0
        if(verifyDates(now, tripBegining))
            if(verifyDates(now, tripEnd))
                if(verifyDates(tripBegining, tripEnd))
                    dateVerification = 1

        if(!TextUtils.isEmpty(tripName) && !TextUtils.isEmpty(tripPlace) && !TextUtils.isEmpty(tripBegining) && !TextUtils.isEmpty(tripEnd)){
            if(dateVerification == 1){
                firestoreDB = FirebaseFirestore.getInstance()
                val trip = Trip (tripName, tripPlace, tripBegining, tripEnd, created, sharedWith).toMap()

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
                goMyTrips()
            }
            else
                Toast.makeText(
                    applicationContext, "Error",
                    Toast.LENGTH_SHORT)
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