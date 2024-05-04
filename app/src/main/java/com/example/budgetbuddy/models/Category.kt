package com.example.budgetbuddy.models

class Category() {
    var id : String = ""
    var bgcolor : String = ""
    var icon : String = ""
    var name : String = ""

    constructor(id: String, name: String, icon : String, bgc: String): this(){
        this.id = id
        this.name = name
        this.icon = icon
        this.bgcolor = bgc
    }
}