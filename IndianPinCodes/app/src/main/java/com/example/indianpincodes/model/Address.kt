package com.example.indianpincodes.model

data class Address(
    val city: String? = null,
    val district: String? = null,
    val pincode: String? = null,
    val postOfficeName: String? = null,
    val state: String? = null,
    var isLike:Boolean=false
){
    override fun toString(): String {
        return city!!
    }
}
