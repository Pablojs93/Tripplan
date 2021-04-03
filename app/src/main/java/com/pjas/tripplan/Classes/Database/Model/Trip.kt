package com.pjas.tripplan.Classes.Database.Model

import java.util.HashMap

class Trip {
    var id: String? = null
    var name: String? = null
    var multiplePlaces: List<TripPlace>? = null
    var sharedWith: List<SharedWith>? = null
    var tripBegining: String? = null
    var tripEnd: String? = null
    var type: String? = null
    var created: String? = null

    constructor() {}

    constructor(name: String, multiplePlaces: List<TripPlace>?, sharedWith: ArrayList<SharedWith>, tripBegining: String, tripEnd: String, type: String, created: String) {
        this.name = name
        this.multiplePlaces = multiplePlaces
        this.sharedWith = sharedWith
        this.tripBegining = tripBegining
        this.tripEnd = tripEnd
        this.type = type
        this.created = created
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("name", name!!)
        result.put("multiplePlace", multiplePlaces!!)
        result.put("sharedWith", sharedWith!!)
        result.put("tripBegining", tripBegining!!)
        result.put("tripEnd", tripEnd!!)
        result.put("type", type!!)
        result.put("created", created!!)

        return result
    }
}