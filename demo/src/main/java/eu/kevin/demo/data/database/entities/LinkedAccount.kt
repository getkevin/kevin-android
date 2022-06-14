package eu.kevin.demo.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class LinkedAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bankName: String,
    val logoUrl: String,
    val linkToken: String,
    val bankId: String
) : Parcelable