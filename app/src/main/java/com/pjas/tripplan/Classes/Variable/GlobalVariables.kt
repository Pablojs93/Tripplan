package com.pjas.tripplan.Classes.Variable

import android.app.Application
import com.pjas.tripplan.Classes.Database.Model.SharedWith
import java.util.*

class GlobalVariables : Application() {

    companion object{
        @JvmField
        var sharedWithList: ArrayList<SharedWith> = ArrayList()
    }

}