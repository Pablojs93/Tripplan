package com.pjas.tripplan.Login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pjas.tripplan.Classes.Database.DatabaseModelUser
import com.pjas.tripplan.Classes.Database.Model.Trip
import com.pjas.tripplan.Classes.Database.Model.User
import com.pjas.tripplan.R
import java.util.regex.Pattern

class Register : AppCompatActivity() {
    //private lateinit var auth: FirebaseAuth

    //UI elements
    private var etName: EditText? = null
    private var etSurname: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var etConfirm: EditText? = null
    private var bRegister: Button? = null
    private var pbCreate: ProgressDialog? = null

    //Firebase references
    //private var mDatabaseReference: DatabaseReference? = null
    //private var mDatabase: FirebaseDatabase? = null
    private var firestoreDB: FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null

    //global variables
    private var name: String? = null
    private var surname: String? = null
    private var email: String? = null
    private var password: String? = null
    private var confirmPassword: String? = null
    private var userID: String? = null


    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun isValidEmail(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initialise()
    }

    private fun initialise() {
        etName = findViewById<View>(R.id.et_NameR) as EditText
        etSurname = findViewById<View>(R.id.et_SurnameR) as EditText
        etEmail = findViewById<View>(R.id.et_EmailR) as EditText
        etPassword = findViewById<View>(R.id.et_PasswordR) as EditText
        etConfirm = findViewById<View>(R.id.et_ConfirmPasswordR) as EditText
        bRegister = findViewById<View>(R.id.b_RegisterR) as Button
        pbCreate = ProgressDialog(this)

        //mDatabase = FirebaseDatabase.getInstance()
        //mDatabaseReference = mDatabase!!.reference!!.child("Users")
        firestoreDB = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        bRegister!!.setOnClickListener{
            RegisterUser()
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    fun RegisterUser() {
        name = etName?.text.toString()
        surname = etSurname?.text.toString()
        email = etEmail?.text.toString().toLowerCase().trim()
        password = etPassword?.text.toString()
        confirmPassword = etConfirm?.text.toString()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword))
        {
            if(isValidEmail(email.toString()))
            {
                if(password.equals(confirmPassword)){
                    mAuth!!
                        .createUserWithEmailAndPassword(email!!, password!!)
                        .addOnCompleteListener(this) { task ->
                            pbCreate!!.hide()
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "createUserWithEmail:success")
                                userID = mAuth!!.currentUser!!.uid

                                //var model=DatabaseModelUser(name.toString(), surname.toString(), email.toString())

                                //mDatabaseReference!!.child(userId).setValue(model)

                                val user = User(name!!, surname!!, email!!, userID!!)

                                firestoreDB!!.collection("Users")
                                    .add(user)
                                    .addOnSuccessListener { documentReference ->
                                        Toast.makeText(
                                            applicationContext, "User created",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            applicationContext, "Error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                //Verify Email
                                verifyEmail()
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)
                                //update user profile information
                            } else {
                                // If sign in fails, display a message to the user.

                                Log.w("Register", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(applicationContext, task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                else
                    etConfirm?.error = "Confirm password should be equal to password."
            }
            else
                etEmail?.error = "Email isn't in the right format. Email should be in the format xxx@xxx.xxx"
        }
        else
        {
            val toast = Toast.makeText(applicationContext, "Email, password and confirm password shouldn't be empty", Toast.LENGTH_LONG)
            toast.show()
        }
    }

    @IgnoreExtraProperties
    data class User(val name: String? = null, val surname: String? = null, val email: String? = null) {
        // Null default values create a no-argument default constructor, which is needed
        // for deserialization from a DataSnapshot.
    }

    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser;
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext,
                        "Verification email sent to " + mUser.getEmail(),
                        Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Register", "sendEmailVerification", task.exception)
                    Toast.makeText(applicationContext,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}