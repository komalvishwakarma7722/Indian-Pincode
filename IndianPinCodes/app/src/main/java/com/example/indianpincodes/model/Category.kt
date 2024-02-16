package com.example.indianpincodes.model

data class Category(
    var state:String? = null,
    var districts:MutableList<String>? = null
){

    override fun toString(): String {
        return state.toString()
    }

}
