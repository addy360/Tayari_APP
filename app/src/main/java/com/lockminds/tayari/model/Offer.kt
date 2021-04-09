package com.lockminds.tayari.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "offer")
data class Offer(
        @PrimaryKey(autoGenerate = false) var id: Long,
        var name: String? = "",
        var team_id: String? = "",
        var sale_image: String? = "",
        var currency: String? = "",
        var cousin_id: String? = "",
        var price: String? = "",
        var sale: String? = "",
        var sale_start: String? = "",
        var sale_end: String? = "",
        var image: String? = "",
        var description: String? = "",
        var restaurant_name: String? = "",
        var restaurant_banner: String? = "",
        var restaurant_logo: String? = "",
        var cousin_name: String? = "",

        ): Parcelable {
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
                    "",
                    "",
                    "",
                    "",
                    "",
            )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(team_id)
        parcel.writeString(sale_image)
        parcel.writeString(currency)
        parcel.writeString(cousin_id)
        parcel.writeString(price)
        parcel.writeString(sale)
        parcel.writeString(sale_start)
        parcel.writeString(sale_end)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeString(restaurant_name)
        parcel.writeString(restaurant_banner)
        parcel.writeString(restaurant_logo)
        parcel.writeString(cousin_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Offer> {
        override fun createFromParcel(parcel: Parcel): Offer {
            return Offer(parcel)
        }

        override fun newArray(size: Int): Array<Offer?> {
            return arrayOfNulls(size)
        }
    }
}
