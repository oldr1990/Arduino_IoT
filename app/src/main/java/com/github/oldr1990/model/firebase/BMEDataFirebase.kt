package com.github.oldr1990.model.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BMEDataFirebase(
    val humidity: String = "0.0",
    val temperature: String= "0.0",
    val pressure: Int = 0,
    val date: Long = 0,
):Parcelable
