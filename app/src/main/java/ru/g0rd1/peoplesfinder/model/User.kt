package ru.g0rd1.peoplesfinder.model

import ru.g0rd1.peoplesfinder.common.enums.Relation
import ru.g0rd1.peoplesfinder.common.enums.Sex
import java.time.LocalDate

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val deactivated: String?,
    val isClosed: Boolean,
    val birthday: LocalDate?,
    val city: City?,
    val country: Country?,
    val sex: Sex?,
    val relation: Relation?,
    val hasPhoto: Boolean?,
    val photo100: String?,
    val photoMax: String?,
) {

    val age: Int?
        get() {
            val birthday = this.birthday ?: return null
            val now = LocalDate.now()
            val yearsDiff = now.year - birthday.year
            val additionalYears = if (now.minusYears(yearsDiff.toLong()) >= birthday) 0 else -1
            return yearsDiff + additionalYears
        }

}