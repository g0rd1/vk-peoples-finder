package ru.g0rd1.peoplesfinder.model

import androidx.annotation.StringRes
import ru.g0rd1.peoplesfinder.R

data class FilterParameters(
    val ageFrom: Age,
    val ageTo: Age,
    val sex: Sex,
    val relation: Relation,
    val country: Country,
    val city: City,
    val hasPhoto: Boolean,
    val requiredGroupIds: List<Int>
) {

    sealed class Age {
        object Any : Age()
        data class Specific(val age: Int) : Age()
    }

    enum class Sex {
        ANY, MALE, FEMALE;

        @StringRes
        fun getStringResource(): Int {
            return when (this) {
                ANY -> R.string.user_sex_any
                MALE -> R.string.user_sex_male
                FEMALE -> R.string.user_sex_female
            }
        }

    }

    enum class Relation {
        ANY,
        SINGLE,
        IN_A_RELATIONSHIP,
        ENGAGED,
        MARRIED,
        IT_IS_COMPLICATED,
        ACTIVELY_SEARCHING,
        IN_LOVE,
        IN_A_CIVIL_UNION;

        @StringRes
        fun getStringResource(sex: Sex = Sex.ANY): Int {
            return when (this) {
                ANY -> R.string.user_relation_any
                SINGLE -> when (sex) {
                    Sex.ANY -> R.string.user_relation_single_sex_not_specified
                    Sex.FEMALE -> R.string.user_relation_single_sex_female
                    Sex.MALE -> R.string.user_relation_single_sex_male
                }
                IN_A_RELATIONSHIP -> when (sex) {
                    Sex.ANY -> R.string.user_relation_in_a_relationship_sex_not_specified
                    Sex.FEMALE -> R.string.user_relation_in_a_relationship_sex_female
                    Sex.MALE -> R.string.user_relation_in_a_relationship_sex_male
                }
                ENGAGED -> when (sex) {
                    Sex.ANY -> R.string.user_relation_engaged_sex_not_specified
                    Sex.FEMALE -> R.string.user_relation_engaged_sex_female
                    Sex.MALE -> R.string.user_relation_engaged_sex_male
                }
                MARRIED -> when (sex) {
                    Sex.ANY -> R.string.user_relation_married_sex_not_specified
                    Sex.FEMALE -> R.string.user_relation_married_sex_female
                    Sex.MALE -> R.string.user_relation_married_sex_male
                }
                IT_IS_COMPLICATED -> R.string.user_relation_it_is_complicated
                ACTIVELY_SEARCHING -> R.string.user_relation_actively_searching
                IN_LOVE -> when (sex) {
                    Sex.ANY -> R.string.user_relation_in_love_sex_not_specified
                    Sex.FEMALE -> R.string.user_relation_in_love_sex_female
                    Sex.MALE -> R.string.user_relation_in_love_sex_male
                }
                IN_A_CIVIL_UNION -> R.string.user_relation_in_a_civil_union
            }
        }
    }

    sealed class Country(val name: String) {
        object Any : Country("")
        data class Specific(val data: ru.g0rd1.peoplesfinder.model.Country) : Country(data.title)
    }

    sealed class City(val name: String) {
        object Any : City("")
        data class Specific(val data: ru.g0rd1.peoplesfinder.model.City) : City(data.title)
    }

    companion object {
        fun getDefault(): FilterParameters {
            return FilterParameters(
                ageFrom = Age.Any,
                ageTo = Age.Any,
                sex = Sex.ANY,
                relation = Relation.ANY,
                country = Country.Any,
                city = City.Any,
                hasPhoto = false,
                requiredGroupIds = listOf()
            )
        }
    }

}




