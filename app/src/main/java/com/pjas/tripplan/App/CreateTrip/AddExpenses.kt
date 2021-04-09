package com.pjas.tripplan.App.CreateTrip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
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
import com.pjas.tripplan.Classes.Database.Adapter.TripExpenseRecyclerViewAdapter
import com.pjas.tripplan.Classes.Database.Adapter.TripPlaceRecyclerViewAdapter
import com.pjas.tripplan.Classes.Database.Model.Expense
import com.pjas.tripplan.Classes.Database.Model.SharedWith
import com.pjas.tripplan.Classes.Database.Model.TripPlace
import com.pjas.tripplan.Classes.NavigationDrawer.ClickListener
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationItemModel
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationRVAdapter
import com.pjas.tripplan.Classes.NavigationDrawer.RecyclerTouchListener
import com.pjas.tripplan.Classes.Variable.GlobalVariables
import com.pjas.tripplan.R
import kotlinx.android.synthetic.main.add_expenses_trip_layout.*
import java.util.*


class AddExpenses : AppCompatActivity()
{
    private lateinit var etmDescription: EditText
    private lateinit var etCost: EditText
    private lateinit var sType: Spinner
    private lateinit var rbYes: RadioButton
    private lateinit var rbNo: RadioButton
    private lateinit var bAdd: Button
    private lateinit var bCreate: Button

    //var docId = ""
    var tripName = ""
    var tripType = ""
    var created = ""
    var tripBegining = ""
    var tripEnd = ""
    var id = 0

    val placesList = GlobalVariables.placeList
    var sharedWith = GlobalVariables.sharedWithList

    private var firestoreDB: FirebaseFirestore? = null

    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter

    //lateinit var placesClient: PlacesClient

    private var mAdapter: TripExpenseRecyclerViewAdapter? = null


    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "My Trips"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Create Trip"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Signout")
    )

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_expenses_trip_layout)

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
                Handler().postDelayed(
                    {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }, 200
                )
            }
        }))

        // Update Adapter with item data and highlight the default menu item ('Home' Fragment)
        updateAdapter(1)

        // Set 'Home' as the default fragment when the app starts
        //val myTrips = MyTrips_()
        //supportFragmentManager.beginTransaction().replace(R.id.activity_main_content_id, myTrips).commit()

        // Close the soft keyboard when you open or close the Drawer
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            activity_main_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
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

        bAdd!!.setOnClickListener{
            Add()
            Load()
        }

        bCreate!!.setOnClickListener{
            Create()
        }
    }

    fun init()
    {
        etmDescription = findViewById<View>(R.id.etm_DescriptionAE) as EditText
        etCost = findViewById<View>(R.id.et_CostAE) as EditText
        sType = findViewById<View>(R.id.s_ExpenseTypeAE) as Spinner
        rbYes = findViewById<View>(R.id.rb_SharedYesAE) as RadioButton
        rbNo = findViewById<View>(R.id.rb_SharedNoAE) as RadioButton
        bAdd = findViewById<View>(R.id.b_AddExpenseAE) as Button
        bCreate = findViewById<View>(R.id.b_CreateTripAE) as Button

        rbNo.isSelected
        etmDescription.setMovementMethod(ScrollingMovementMethod())
    }

    fun Add()
    {
        val description = etmDescription.text.toString()
        val cost = etCost.text.toString()
        val type = sType.selectedItem.toString()
        var shared = false

        if(!TextUtils.isEmpty(description) && !TextUtils.isEmpty(cost))
        {
            if (rbYes.isChecked)
                shared=true

            val sharedWithList: ArrayList<SharedWith> = ArrayList()

            val expense = Expense (description, cost.toDouble(), type, shared, false, sharedWithList )

            GlobalVariables.expensesList.add(expense)

            etmDescription.setText("")
        }
        /*val name = etName.text.toString()
        val begining = etdBegining.text.toString()
        val end = etdEnd.text.toString()

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(begining) && !TextUtils.isEmpty(end))
        {
            val place = TripPlace(name, begining, end)
            GlobalVariables.placeList.add(place)
            etName.setText("")
            etdBegining.setText("")
            etdEnd.setText("")
        }*/
    }

    fun Load()
    {
        mAdapter = TripExpenseRecyclerViewAdapter(GlobalVariables.expensesList, applicationContext)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        rv_ExpensesAE.layoutManager = mLayoutManager
        rv_ExpensesAE.itemAnimator = DefaultItemAnimator()
        rv_ExpensesAE.adapter = mAdapter
    }

    fun Create(){
        /*firestoreDB = FirebaseFirestore.getInstance()

        val trip = Trip(tripName, GlobalVariables.placeList, sharedWith, tripBegining, tripEnd, tripType, created)

        firestoreDB!!.collection("Trips").add(trip).addOnSuccessListener{ documentReference ->
            Toast.makeText(applicationContext, "Trip created", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
        }*/
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