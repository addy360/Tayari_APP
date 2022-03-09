package com.user.tayari.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "cart_items")
data class CartItem(
        @PrimaryKey(autoGenerate = false) var id: Long,
        var price: String? = "",
        var qty: String? = "",
        var total: String? = "",
        var main: String? = "",
        var team_id: String? = "",
        var product_id: String? = "",
        var menu_id: String? = "",
        var menu_name: String? = "",
        var menu_image: String? = "",
        var product_name: String? = "",
        var product_image: String? = "",
        var currency: String? = "",
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
        parcel.readString(),) {
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

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(price)
        parcel.writeString(qty)
        parcel.writeString(total)
        parcel.writeString(main)
        parcel.writeString(team_id)
        parcel.writeString(product_id)
        parcel.writeString(menu_id)
        parcel.writeString(menu_name)
        parcel.writeString(product_name)
        parcel.writeString(product_image)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}
