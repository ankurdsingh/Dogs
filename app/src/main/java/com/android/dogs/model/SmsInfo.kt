package com.android.dogs.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SmsInfo (
    var to: String,
    var text: String,
    var imageUrl: String? = null
    ):Parcelable