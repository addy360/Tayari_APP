package com.user.tayari.datasource

import com.user.tayari.model.Cousin
import com.user.tayari.model.Offer
import com.user.tayari.model.Restaurant
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