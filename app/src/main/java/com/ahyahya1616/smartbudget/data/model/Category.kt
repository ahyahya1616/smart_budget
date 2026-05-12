package com.ahyahya1616.smartbudget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // String representation of the icon
    val color: String, // Hex string
    val isActive: Boolean = true
)
