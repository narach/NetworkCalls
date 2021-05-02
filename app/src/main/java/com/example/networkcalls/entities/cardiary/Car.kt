package com.example.networkcalls.entities.cardiary

data class Car(
    val brand: Brand,
    val id: Int,
    val mileage: Int,
    val model: Model,
    val photoUrl: String,
    val vin: String,
    val year: Int
)