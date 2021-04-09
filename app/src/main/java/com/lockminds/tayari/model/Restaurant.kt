package com.lockminds.tayari.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
        @PrimaryKey(autoGenerate = false) var id: Long,
        var name: String? = "",
        var latitude: String? = "",
        var longitude: String? = "",
        var logo: String? = "",
        var banner: String? = "",
        var phone: String? = "",
        var email: String? = "",
        var address: String? = "",
        var currency_code: String? = "",
        var description: String? = "",
 ) : Parcelable{

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    constructor(): this(
        1,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(logo)
        parcel.writeString(banner)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(address)
        parcel.writeString(currency_code)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun newArray(size: Int): Array<Restaurant?> {
            return arrayOfNulls(size)
        }
    }
}
