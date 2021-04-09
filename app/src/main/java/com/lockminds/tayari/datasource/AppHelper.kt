package com.lockminds.tayari.datasource

import com.lockminds.tayari.model.Cousin
import com.lockminds.tayari.model.Offer
import com.lockminds.tayari.model.Restaurant
import java.lang.Exception
import java.text.NumberFormat


fun restaurantsListEmpty(): List<Restaurant> {
    return arrayListOf()
}


fun offersList(): List<Offer> {
    return arrayListOf()
}

fun cousinsList(): List<Cousin>{
    return arrayListOf()
}

fun myMoney(money: String): String{
    return try{
        NumberFormat.getInstance().format(money)
    }catch (ex: Exception){
        money
    }

}