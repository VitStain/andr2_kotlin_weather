package com.geekbrains.weatherwithmvvm.model.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    var city: String,
    val lat: Double,
    val lon: Double
) : Parcelable
