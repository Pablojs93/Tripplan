package com.pjas.tripplan.Classes.Database.Model

import java.util.HashMap

class User {
    var id: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var userID: String? = null

    constructor() {}

    constructor(firstName: String, lastName: String, email: String, userID: String) {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.userID = userID
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("firstName", firstName!!)
        result.put("lastName", lastName!!)
        result.put("email", email!!)
        result.put("userID", userID!!)
        return result
    }
}