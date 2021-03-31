package com.pjas.tripplan.Classes.Database.Model

import java.util.HashMap

class TripPlace {
    var place: String? = null
    var begining: String? = null
    var end: String? = null

    constructor() {}

    constructor(place: String, begining: String, end: String) {
        this.place = place
        this.begining = begining
        this.end = end
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("place", place!!)
        result.put("begining", begining!!)
        result.put("end", end!!)

        return result
    }
}