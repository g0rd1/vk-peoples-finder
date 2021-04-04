package ru.g0rd1.peoplesfinder.model

import kotlinx.serialization.Serializable

@Serializable
data class FilterParameters(
    val ageFrom: Age,
    val ageTo: Age,
    val sex: Sex,
    val relation: Relation,
    val country: Country,
    val city: City,
    val hasPhoto: Boolean,
    val notClosed: Boolean,
    val requiredGroupIds: List<Int>
) {

    @Serializable
    sealed class Age {
        @Serializable
        object Any : Age()
        @Serializable
        data class Specific(val age: Int) : Age()
    }

    @Serializable
    sealed class Sex {
        @Serializable
        object Any : Sex()
        @Serializable
        data class Specific(val sex: ru.g0rd1.peoplesfinder.common.enums.Sex): Sex()
    }

    @Serializable
    sealed class Relation {
        @Serializable
        object Any : Relation()
        @Serializable
        data class Specific(val relation: ru.g0rd1.peoplesfinder.common.enums.Relation): Relation()
    }

    @Serializable
    sealed class Country {
        @Serializable
        object Any : Country()
        @Serializable
        data class Specific(val country: ru.g0rd1.peoplesfinder.model.Country) : Country()
    }

    @Serializable
    sealed class City {
        @Serializable
        object Any : City()
        @Serializable
        data class Specific(val city: ru.g0rd1.peoplesfinder.model.City) : City()
    }

    companion object {
        fun getDefault(): FilterParameters {
            return FilterParameters(
                ageFrom = Age.Any,
                ageTo = Age.Any,
                sex = Sex.Any,
                relation = Relation.Any,
                country = Country.Any,
                city = City.Any,
                hasPhoto = false,
                notClosed = false,
                requiredGroupIds = listOf()
            )
        }
    }

}




