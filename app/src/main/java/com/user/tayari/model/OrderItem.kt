package com.user.tayari.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItem(
        @PrimaryKey(autoGenerate = false) var id: Long,
        var created_at: String? = "",
        var updated_at: String? = "",
        var deleted_at: String? = "",
        var qty: String? = "",
        var cost: String? = "",
        var total_cost: String? = "",
        var menu_id: String? = "",
        var order_id: String? = "",
        var team_id: String? = "",
        val menu_name: String? = "",
        val menu_image: String? = "",
        var currency: String? = ""
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
            ""

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeString(deleted_at)
        parcel.writeString(qty)
        parcel.writeString(cost)
        parcel.writeString(total_cost)
        parcel.writeString(menu_id)
        parcel.writeString(order_id)
        parcel.writeString(team_id)
        parcel.writeString(menu_name)
        parcel.writeString(menu_image)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderItem> {
        override fun createFromParcel(parcel: Parcel): OrderItem {
            return OrderItem(parcel)
        }

        override fun newArray(size: Int): Array<OrderItem?> {
            return arrayOfNulls(size)
        }
    }
}
