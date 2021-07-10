package com.lockminds.tayari.model

import android.os.Parcel
import android.os.Parcelable


data class Parameters(

        var param_1: String? = "",
        var param_2: String? = "",
        var param_3: String? = "",

 ): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),) {
    }

    constructor(): this(
        "",
            "",
            "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(param_1)
        parcel.writeString(param_2)
        parcel.writeString(param_3)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Parameters> {
        override fun createFromParcel(parcel: Parcel): Parameters {
            return Parameters(parcel)
        }

        override fun newArray(size: Int): Array<Parameters?> {
            return arrayOfNulls(size)
        }
    }
}
