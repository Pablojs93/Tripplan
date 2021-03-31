package com.pjas.tripplan.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.pjas.tripplan.App.CreateTrip.CreateTrip
import com.pjas.tripplan.App.MyTrips.MyTrips
import com.pjas.tripplan.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val intent = Intent(this, MyTrips::class.java)
            startActivity(intent)
        }
    }

    fun openLogin(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun openRegister(view: View) {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
}