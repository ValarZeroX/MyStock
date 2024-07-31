package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val themeIndex: Int
)

