package com.pjas.tripplan.Classes.Database.Model

import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.HashMap

class Trip {
    var id: String? = null
    var name: String? = null
    var places: String? = null
    var begining: String? = null
    var end: String? = null
    var created: String? = null
    var sharedWith: String? = null

    constructor() {}

    constructor(name: String, places: String, begining: String, end: String, created: String, sharedWith: String) {
        this.name = name
        this.places = places
        this.begining = begining
        this.end = end
        this.created = created
        this.sharedWith = sharedWith
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("name", name!!)
        result.put("places", places!!)
        result.put("begining", begining!!)
        result.put("end", end!!)
        result.put("created", created!!)
        result.put("shared", sharedWith!!)

        return result
    }
}