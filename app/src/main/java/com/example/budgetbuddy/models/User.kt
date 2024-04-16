package com.example.budgetbuddy.models

class User() {
    var userUID: String = ""
    var email: String = ""
    var name: String = ""

    constructor(pName: String, pUserUID: String, pEmail: String): this() {
        this.userUID = pUserUID
        this.email = pEmail
        this.name = pName

    }
}