package com.pjas.tripplan.Classes.Database.Model

import java.util.HashMap

class SharedWith {
    var id: String? = null
    var email: String? = null
    var name: String? = null

    constructor() {}

    constructor(email: String, name: String) {
        this.email = email
        this.name = name
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("email", email!!)
        result.put("name", name!!)

        return result
    }
}