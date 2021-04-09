package com.pjas.tripplan.Classes.Variable

import android.app.Application
import com.pjas.tripplan.Classes.Database.Model.Expense
import com.pjas.tripplan.Classes.Database.Model.SharedWith
import com.pjas.tripplan.Classes.Database.Model.TripPlace
import com.pjas.tripplan.Classes.Database.Model.User
import java.util.*
import kotlin.collections.ArrayList

class GlobalVariables : Application() {

    companion object{
        @JvmField
        var usersList: ArrayList<User> = ArrayList()
        var sharedWithList: ArrayList<SharedWith> = ArrayList()
        var placeList: ArrayList<TripPlace> = ArrayList()
        var expensesList: ArrayList<Expense> = ArrayList()
        var actualPlace: TripPlace = TripPlace()
        var placePosition: Int = 0
        var newTripName: String = ""
        var newTripBegining: String = ""
        var newTripEnd: String = ""
        var newTripType: String = ""
        var newTripCreated: String = ""


    }
}