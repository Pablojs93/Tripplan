package com.pjas.tripplan.Classes.Database.Model

import java.util.HashMap

class Trip {
    var id: String? = null
    var name: String? = null
    var multiple: Boolean? = false
    var multiplePlaces: List<TripPlace>? = null
    var shared: Boolean? = false
    var sharedWith: List<String>? = null
    var tripBegining: String? = null
    var tripEnd: String? = null
    var type: String? = null
    var created: String? = null

    constructor() {}

    constructor(name: String, multiple: Boolean?, multiplePlaces: List<TripPlace>?, shared: Boolean?, sharedWith: List<String>?, tripBegining: String, tripEnd: String, type: String, created: String) {
        this.name = name
        this.multiple = multiple
        this.multiplePlaces = multiplePlaces
        this.shared = shared
        this.sharedWith = sharedWith
        this.tripBegining = tripBegining
        this.tripEnd = tripEnd
        this.type = type
        this.created = created
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("name", name!!)
        result.put("multiple", multiple!!)
        result.put("multiplePlace", multiplePlaces!!)
        result.put("shared", shared!!)
        result.put("sharedWith", sharedWith!!)
        result.put("tripBegining", tripBegining!!)
        result.put("tripEnd", tripEnd!!)
        result.put("type", type!!)
        result.put("created", created!!)

        return result
    }
}