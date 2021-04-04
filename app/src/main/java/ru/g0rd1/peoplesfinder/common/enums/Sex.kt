package ru.g0rd1.peoplesfinder.common.enums

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import ru.g0rd1.peoplesfinder.R

enum class Sex {

    @SerializedName("0")
    NOT_SPECIFIED,

    @SerializedName("1")
    FEMALE,

    @SerializedName("2")
    MALE;

    @StringRes
    fun getStringResource(): Int {
        return when (this) {
            NOT_SPECIFIED -> R.string.user_sex_not_specified
            FEMALE -> R.string.user_sex_female
            MALE -> R.string.user_sex_male
        }
    }
}