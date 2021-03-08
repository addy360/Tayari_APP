package com.lockminds.tayari.datasource.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lockminds.tayari.responses.LoginResponse

@Entity(tableName = "users")
data class Users(
    @PrimaryKey(autoGenerate = false) var id: String,
    var name: String,
    var photo_url: String,
    var phone_number: String,
    var email: String? = null
){
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
    )

    constructor(response: LoginResponse) : this() {
        var name: String = response.name
        var photo_url: String = response.photo_url
        var phone_number: String = response.phone_number
        var email: String? = response.email
    }

}