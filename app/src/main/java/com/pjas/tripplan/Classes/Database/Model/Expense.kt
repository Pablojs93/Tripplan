package com.pjas.tripplan.Classes.Database.Model

import java.util.HashMap

class Expense {
    var id: String? = null
    var description: String? = null
    var cost: Double? = 0.0
    var type: String? = null
    var shared: Boolean? = false
    var paid: Boolean? = false
    var sharedWith: List<SharedWith>? = null


    constructor() {}

    constructor(description: String, cost: Double, type: String, shared: Boolean, paid: Boolean, sharedWith: ArrayList<SharedWith>)
    {
        this.description = description
        this.cost = cost
        this.type = type
        this.shared = shared
        this.paid = paid
        this.sharedWith = sharedWith
    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("description", description!!)
        result.put("cost", cost!!)
        result.put("type", type!!)
        result.put("shared", shared!!)
        result.put("paid", paid!!)
        result.put("sharedWith", sharedWith!!)

        return result
    }
}