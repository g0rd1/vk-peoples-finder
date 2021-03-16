package ru.g0rd1.peoplesfinder.common.enums

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import ru.g0rd1.peoplesfinder.R

enum class Relation {
    @SerializedName("0")
    NOT_SPECIFIED,

    @SerializedName("1")
    SINGLE,

    @SerializedName("2")
    IN_A_RELATIONSHIP,

    @SerializedName("3")
    ENGAGED,

    @SerializedName("4")
    MARRIED,

    @SerializedName("5")
    IT_IS_COMPLICATED,

    @SerializedName("6")
    ACTIVELY_SEARCHING,

    @SerializedName("7")
    IN_LOVE,

    @SerializedName("8")
    IN_A_CIVIL_UNION;

    @StringRes
    fun getStringResource(sex: Sex = Sex.NOT_SPECIFIED): Int {
        return when (this) {
            NOT_SPECIFIED -> R.string.user_relation_not_specified
            SINGLE -> when (sex) {
                Sex.NOT_SPECIFIED -> R.string.user_relation_single_sex_not_specified
                Sex.FEMALE -> R.string.user_relation_single_sex_female
                Sex.MALE -> R.string.user_relation_single_sex_male
            }
            IN_A_RELATIONSHIP -> when (sex) {
                Sex.NOT_SPECIFIED -> R.string.user_relation_in_a_relationship_sex_not_specified
                Sex.FEMALE -> R.string.user_relation_in_a_relationship_sex_female
                Sex.MALE -> R.string.user_relation_in_a_relationship_sex_male
            }
            ENGAGED -> when (sex) {
                Sex.NOT_SPECIFIED -> R.string.user_relation_engaged_sex_not_specified
                Sex.FEMALE -> R.string.user_relation_engaged_sex_female
                Sex.MALE -> R.string.user_relation_engaged_sex_male
            }
            MARRIED -> when (sex) {
                Sex.NOT_SPECIFIED -> R.string.user_relation_married_sex_not_specified
                Sex.FEMALE -> R.string.user_relation_married_sex_female
                Sex.MALE -> R.string.user_relation_married_sex_male
            }
            IT_IS_COMPLICATED -> R.string.user_relation_it_is_complicated
            ACTIVELY_SEARCHING -> R.string.user_relation_actively_searching
            IN_LOVE -> when (sex) {
                Sex.NOT_SPECIFIED -> R.string.user_relation_in_love_sex_not_specified
                Sex.FEMALE -> R.string.user_relation_in_love_sex_female
                Sex.MALE -> R.string.user_relation_in_love_sex_male
            }
            IN_A_CIVIL_UNION -> R.string.user_relation_in_a_civil_union
        }
    }

}