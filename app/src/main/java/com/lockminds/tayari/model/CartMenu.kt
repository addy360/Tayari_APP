package com.lockminds.tayari.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "cart_menu")
data class CartMenu(
        @PrimaryKey(autoGenerate = false) var id: Long,
        var team_id: String? = "",
        var type: String? = "",
        var cousin_id: String? = "",
        var name: String? = "",
        var price: String? = "",
        var sale: String? = "",
        var sale_start: String? = "",
        var sale_end: String? = "",
        var image: String? = "",
        var sale_image: String? = "",
        var description: String? = "",
        var currency: String? = "",
        var restaurant_name: String? = "",
        var restaurant_banner: String? = "",
        var restaurant_logo: String? = "",
        var cousin_name: String? = "",
        var qty: String? = "",
        var total: String? = "",
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
            "",
            "",
            ""
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(type)
        parcel.writeString(team_id)
        parcel.writeString(cousin_id)
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeString(sale)
        parcel.writeString(sale_start)
        parcel.writeString(sale_end)
        parcel.writeString(image)
        parcel.writeString(sale_image)
        parcel.writeString(description)
        parcel.writeString(currency)
        parcel.writeString(restaurant_name)
        parcel.writeString(restaurant_banner)
        parcel.writeString(restaurant_logo)
        parcel.writeString(cousin_name)
        parcel.writeString(qty)
        parcel.writeString(total)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartMenu> {
        override fun createFromParcel(parcel: Parcel): CartMenu {
            return CartMenu(parcel)
        }

        override fun newArray(size: Int): Array<CartMenu?> {
            return arrayOfNulls(size)
        }
    }
}
