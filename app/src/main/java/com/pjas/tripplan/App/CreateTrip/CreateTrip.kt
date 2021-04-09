package com.pjas.tripplan.App.CreateTrip

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.pjas.tripplan.Classes.Database.Adapter.TripSharedRecyclerViewAdapter
import com.pjas.tripplan.Classes.Database.Model.SharedWith
import com.pjas.tripplan.Classes.Database.Model.User
import com.pjas.tripplan.Classes.NavigationDrawer.ClickListener
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationItemModel
import com.pjas.tripplan.Classes.NavigationDrawer.NavigationRVAdapter
import com.pjas.tripplan.Classes.NavigationDrawer.RecyclerTouchListener
import com.pjas.tripplan.Classes.Variable.GlobalVariables
import com.pjas.tripplan.R
import kotlinx.android.synthetic.main.createtrip_home_layout.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class CreateTrip : AppCompatActivity()
{
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000

    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter

    private lateinit var bNext: Button
    private lateinit var bAdd: Button
    private lateinit var etName: EditText
    private lateinit var etdBegining: EditText
    private lateinit var etdEnd: EditText
    private lateinit var sType: Spinner
    private lateinit var etEmail: EditText
    var picker: DatePickerDialog? = null

    val sharedWith = ArrayList<SharedWith>()

    private var mAdapter: TripSharedRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String = ""
    internal var shared: String = ""

    var docId: String = ""
    var cal = Calendar.getInstance()

    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "My Trips"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Create Trip"),
        NavigationItemModel(R.drawable.ic_baseline_airplanemode_active_24, "Signout")
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createtrip_home_layout)

        firestoreDB = FirebaseFirestore.getInstance()

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
                Handler().postDelayed({ drawerLayout.closeDrawer(GravityCompat.START) }, 200)
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
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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

        init()

        showBeginingDate()
        showEndDate()

        bAdd.setOnClickListener{
            etEmail.clearFocus()
            bAdd.requestFocus()
            val email = etEmail.text.toString().toLowerCase().trim()
            if(email.isNotEmpty()){
                Add(email)
                Load()
            }
        }

        bNext.setOnClickListener{
            Create()
        }
    }

    override fun onResume()
    {
        handler.postDelayed(Runnable
        {
            handler.postDelayed(runnable!!, delay.toLong())
            getUsers()
        }.also
        {
            runnable = it
        }, delay.toLong()
        )
        super.onResume()
    }

    override fun onPause()
    {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

    fun getUsers ()
    {
        GlobalVariables.usersList = ArrayList()
        firestoreDB!!.collection("Users").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                for (doc in task.result) {
                    val user = doc.toObject<User>(User::class.java)
                    user.id = doc.id
                    GlobalVariables.usersList.add(user)
                }
            }
        }
    }

    fun showBeginingDate()
    {
        // DatePicker

        val c = Calendar.getInstance().timeInMillis
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

            val dialog = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.minDate = c
            if(etdEnd.text.isNotEmpty())
            {
                val d = getMilliFromDate(etdEnd.text.toString())
                dialog.datePicker.maxDate = d
            }
            dialog.show()
        }
    }

    fun showEndDate()
    {
        // DatePicker

        val c = Calendar.getInstance().timeInMillis
        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            etdEnd.setText(sdf.format(cal.time))
        }

        etdEnd.setOnClickListener{

            Log.d("Clicked", "Interview Date Clicked")

            val dialog = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.minDate = c
            if(etdBegining.text.isNotEmpty())
            {
                val d = getMilliFromDate(etdBegining.text.toString())
                dialog.datePicker.minDate = d
            }
            dialog.show()
        }
    }

    fun getMilliFromDate(dateFormat: String?): Long
    {
        var date = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        try
        {
            date = formatter.parse(dateFormat)
        }
        catch (e: ParseException)
        {
            e.printStackTrace()
        }
        return date.time
    }

    fun verifyDates(f: String, s: String) : Boolean
    {
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

    fun init()
    {
        bNext = findViewById<View>(R.id.b_Next) as Button
        bAdd = findViewById<View>(R.id.b_AddPersonCT) as Button
        etName = findViewById<View>(R.id.et_NameTripCT) as EditText
        etdBegining = findViewById<View>(R.id.etd_TripBeginingCT) as EditText
        etdEnd = findViewById<View>(R.id.etd_TripEndCT) as EditText
        sType = findViewById<View>(R.id.s_TripTypeCT) as Spinner
        etEmail = findViewById<View>(R.id.et_EmailCT) as EditText
    }

    fun Add(email: String)
    {
        //firestoreDB = FirebaseFirestore.getInstance()
        var person: String = ""
        for(user: User in GlobalVariables.usersList)
        {
            if(user.email.equals(email)){
                person = user.firstName.toString() + " " + user.lastName.toString()
                val shared = SharedWith(user.email.toString(), person)
                GlobalVariables.sharedWithList.add(shared)
            }
        }
    }

    fun Load()
    {
        mAdapter = TripSharedRecyclerViewAdapter(GlobalVariables.sharedWithList, applicationContext)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        rv_sharedWithCT.layoutManager = mLayoutManager
        rv_sharedWithCT.itemAnimator = DefaultItemAnimator()
        rv_sharedWithCT.adapter = mAdapter
    }

    fun Create()
    {
        val tripName = etName.text.toString()
        val tripType = sType.selectedItem.toString()
        val created = FirebaseAuth.getInstance().currentUser.uid
        val tripBegining = etdBegining.text.toString()
        val tripEnd = etdEnd.text.toString()

        if(!TextUtils.isEmpty(tripName) && !TextUtils.isEmpty(tripName) && !TextUtils.isEmpty(
                tripName
            ))
        {
            val email = FirebaseAuth.getInstance().currentUser.email.toString().toLowerCase().trim()
            Add(email)

            lateinit var intent: Intent

            intent = Intent(this, AddPlaces::class.java)

            GlobalVariables.newTripName = tripName
            GlobalVariables.newTripBegining = tripBegining
            GlobalVariables.newTripEnd = tripEnd
            GlobalVariables.newTripType = tripType
            GlobalVariables.newTripCreated = created

            startActivity(intent)
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