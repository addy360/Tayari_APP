package com.user.tayari.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "orders")
data class Order(
        @PrimaryKey(autoGenerate = false) var id: Long,
        var waiting_time: String? = "",
        var execution_time: String? = "",
        var delivery_cost: String? = "",
        var initial_payment: String? = "",
        var cost: String? = "",
        var total_cost: String? = "",
        var paid: String? = "",
        var balance: String? = "",
        var order_status: String? = "",
        var payment_status: String? = "",
        var deliverer: String? = "",
        var created_at: String? = "",
        var updated_at: String? = "",
        var restaurant_banner: String? = "",
        var restaurant_name: String? = "",
        var restaurant_logo: String? = "",
        var deleted_at: String? = "",
        var team_id: String? = "",
        var customer: String? = "",
        var discount_percent: String? = "",
        var discount_value: String? = "",
        var product_total: String? = "",
        var menu_total: String? = "",
        var table_name: String? = "",
        var currency: String? = "",
        var elapsed_time: String? = ""
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
        parcel.writeString(waiting_time)
        parcel.writeString(execution_time)
        parcel.writeString(delivery_cost)
        parcel.writeString(initial_payment)
        parcel.writeString(cost)
        parcel.writeString(total_cost)
        parcel.writeString(paid)
        parcel.writeString(balance)
        parcel.writeString(order_status)
        parcel.writeString(payment_status)
        parcel.writeString(deliverer)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeString(restaurant_banner)
        parcel.writeString(restaurant_name)
        parcel.writeString(restaurant_logo)
        parcel.writeString(deleted_at)
        parcel.writeString(team_id)
        parcel.writeString(customer)
        parcel.writeString(discount_percent)
        parcel.writeString(discount_value)
        parcel.writeString(product_total)
        parcel.writeString(menu_total)
        parcel.writeString(table_name)
        parcel.writeString(currency)
        parcel.writeString(elapsed_time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}
