package org.example

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Add the @Parcelize annotation to your data class
@Parcelize
data class UserData(
    val name: String,
    val age: Int
) : Parcelable
