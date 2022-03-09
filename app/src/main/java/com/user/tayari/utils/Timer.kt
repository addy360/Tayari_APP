package com.user.tayari.utils


import kotlin.math.abs

class Timer {

    fun differMinutes(time1: Long, time2: Long): String{

        // Parsing the Time Period
        // Calculating the difference in milliseconds
        val differenceInMilliSeconds = abs(time1 - time2)

        // Calculating the difference in Hours
        val differenceInHours = (differenceInMilliSeconds / (60 * 60 * 1000) % 24)

        // Calculating the difference in Minutes
        val differenceInMinutes = differenceInMilliSeconds / (60 * 1000) % 60

        // Calculating the difference in Seconds
        val differenceInSeconds = differenceInMilliSeconds / 1000 % 60

        return "$differenceInHours:$differenceInMinutes:$differenceInSeconds"
    }
}