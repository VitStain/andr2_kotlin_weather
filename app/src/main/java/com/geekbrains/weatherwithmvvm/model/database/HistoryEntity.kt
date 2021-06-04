package com.geekbrains.weatherwithmvvm.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val city: String = "",
    val temperature: Int = 0,
    val condition: String = ""
)

const val ID = "id"
const val CITY = "city"
const val TEMPERATURE = "temperature"
