package ru.g0rd1.peoplesfinder.db.helper

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserHistoryEntity
import ru.g0rd1.peoplesfinder.db.entity.UserUserTypeEntity
import ru.g0rd1.peoplesfinder.db.query.UserIdWithSameGroupsCount
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.model.UserType
import java.time.LocalDate

object UserQueryBuilder {

    fun getUsersQuery(
        filterParameters: FilterParameters,
        count: Int,
        notInUserTypes: List<UserType>,
    ): SupportSQLiteQuery {
        val queryBuilder = StringBuilder()
        queryBuilder.append("SELECT *, COUNT(${UserGroupEntity.Column.GROUP_ID}) ")
        queryBuilder.append("as ${UserIdWithSameGroupsCount.SAME_GROUPS_COUNT_COLUMN_NAME} FROM (\n")
        queryBuilder.append("(\n")
        queryBuilder.append("SELECT ${UserEntity.Column.ID} as ${UserIdWithSameGroupsCount.USER_ID}\n")
        queryBuilder.append("FROM ${UserEntity.TABLE_NAME}\n")
        getUsersWhereConditionOrNull(filterParameters, notInUserTypes)?.let { queryBuilder.append("$it\n") }
        queryBuilder.append(") as u\n")
        queryBuilder.append("JOIN\n")
        queryBuilder.append("(\n")
        queryBuilder.append("SELECT * FROM ${UserGroupEntity.TABLE_NAME}\n")
        queryBuilder.append(") as ug\n")
        queryBuilder.append("ON\n")
        queryBuilder.append("u.${UserIdWithSameGroupsCount.USER_ID} = ug.user_id\n")
        queryBuilder.append(")\n")
        queryBuilder.append("GROUP BY u.${UserIdWithSameGroupsCount.USER_ID}\n")
        queryBuilder.append("ORDER BY COUNT(group_id)\n")
        queryBuilder.append("DESC\n")
        queryBuilder.append("LIMIT $count")
        return SimpleSQLiteQuery(queryBuilder.toString())
    }

    private fun getUsersWhereConditionOrNull(
        filterParameters: FilterParameters,
        notInUserTypes: List<UserType>,
    ): String? {
        val whereConditions = getUserWhereConditions(filterParameters, notInUserTypes)
        if (whereConditions.isEmpty()) return null
        val builder = StringBuilder()
        whereConditions.forEachIndexed { index, whereCondition ->
            if (index > 0) {
                builder.append(" AND ")
            } else {
                builder.append("WHERE ")
            }
            builder.append(whereCondition)
        }
        return builder.toString()
    }

    private fun getUserWhereConditions(
        filterParameters: FilterParameters,
        notInUserTypes: List<UserType>,
    ): List<String> {
        val whereConditions: MutableList<String> = mutableListOf()
        whereConditions.apply {
            getAgeFromWhereConditionOrNull(filterParameters.ageFrom)?.let { add(it) }
            getAgeToWhereConditionOrNull(filterParameters.ageTo)?.let { add(it) }
            getSexWhereConditionOrNull(filterParameters.sex)?.let { add(it) }
            getRelationWhereConditionOrNull(filterParameters.relation)?.let { add(it) }
            getCityWhereConditionOrNull(filterParameters.city)?.let { add(it) }
            getCountryWhereConditionOrNull(filterParameters.country)?.let { add(it) }
            getHasPhotoWhereConditionOrNull(filterParameters.hasPhoto)?.let { add(it) }
            getNotClosedWhereConditionOrNull(filterParameters.notClosed)?.let { add(it) }
            getUserWithRequiredGroupsWhereConditionOrNull(filterParameters.requiredGroupIds)?.let { add(it) }
            getUserNotInUserTypesWhereConditionOrNull(notInUserTypes)?.let {
                add(it)
            }
            add(getUsersNotInViewedWhereCondition())
        }
        return whereConditions
    }

    private fun getAgeFromWhereConditionOrNull(
        ageFrom: FilterParameters.Age,
    ) = when (ageFrom) {
        FilterParameters.Age.Any -> null
        is FilterParameters.Age.Specific -> {
            val ageFromDays = LocalDate.now().minusYears(ageFrom.age.toLong() + 1)
            "${UserEntity.Column.BIRTHDAY_EPOCH_DAYS} < ${ageFromDays.toEpochDay()}"
        }
    }

    private fun getAgeToWhereConditionOrNull(
        ageTo: FilterParameters.Age,
    ) = when (ageTo) {
        FilterParameters.Age.Any -> null
        is FilterParameters.Age.Specific -> {
            val ageToDays = LocalDate.now().minusYears(ageTo.age.toLong())
            "${UserEntity.Column.BIRTHDAY_EPOCH_DAYS} >= ${ageToDays.toEpochDay()}"
        }
    }

    private fun getSexWhereConditionOrNull(
        sex: FilterParameters.Sex,
    ) = when (sex) {
        FilterParameters.Sex.Any -> null
        is FilterParameters.Sex.Specific -> "${UserEntity.Column.SEX} == '${sex.sex.name}'"
    }

    private fun getRelationWhereConditionOrNull(
        relation: FilterParameters.Relation,
    ) = when (relation) {
        FilterParameters.Relation.Any -> null
        is FilterParameters.Relation.Specific -> "${UserEntity.Column.RELATION} == '${relation.relation.name}'"
    }

    private fun getCityWhereConditionOrNull(
        city: FilterParameters.City,
    ) = when (city) {
        FilterParameters.City.Any -> null
        is FilterParameters.City.Specific -> "${UserEntity.Column.CITY_PREFIX}id == ${city.city.id}"
    }

    private fun getCountryWhereConditionOrNull(
        country: FilterParameters.Country,
    ) = when (country) {
        FilterParameters.Country.Any -> null
        is FilterParameters.Country.Specific -> "${UserEntity.Column.COUNTRY_PREFIX}id == ${country.country.id}"
    }

    private fun getHasPhotoWhereConditionOrNull(hasPhoto: Boolean) =
        if (hasPhoto) "${UserEntity.Column.HAS_PHOTO} == 1" else null

    private fun getNotClosedWhereConditionOrNull(notClosed: Boolean) =
        if (notClosed) "${UserEntity.Column.IS_CLOSED} == 0" else null

    private fun getUserWithRequiredGroupsWhereConditionOrNull(requiredGroupIds: List<Int>): String? {
        if (requiredGroupIds.isEmpty()) return null
        val builder = StringBuilder()
        builder.append("${UserEntity.Column.ID} IN (SELECT ${UserGroupEntity.Column.USER_ID}")
        builder.append(" FROM ${UserGroupEntity.TABLE_NAME}")
        builder.append(" WHERE ${UserGroupEntity.Column.GROUP_ID} IN (")
        requiredGroupIds.forEachIndexed { index, id ->
            if (index > 0) {
                builder.append(", ")
            }
            builder.append(id)
        }
        builder.append("))")
        return builder.toString()
    }

    private fun getUserNotInUserTypesWhereConditionOrNull(notInUserTypes: List<UserType>): String? {
        if (notInUserTypes.isEmpty()) return null
        val builder = StringBuilder()
        builder.append("${UserEntity.Column.ID} NOT IN (")
        builder.append("SELECT ${UserUserTypeEntity.Column.USER_ID}")
        builder.append(" FROM ${UserUserTypeEntity.TABLE_NAME}")
        builder.append(" WHERE ${UserUserTypeEntity.Column.USER_TYPE_ID} IN (")
        notInUserTypes.forEachIndexed { index, userType ->
            if (index > 0) {
                builder.append(", ")
            }
            builder.append(userType.id)
        }
        builder.append("))")
        return builder.toString()
    }

    private fun getUsersNotInViewedWhereCondition(): String {
        val builder = StringBuilder()
        builder.append("${UserEntity.Column.ID} NOT IN (")
        builder.append("SELECT ${UserHistoryEntity.Column.USER_ID}")
        builder.append(" FROM ${UserHistoryEntity.TABLE_NAME})")
        return builder.toString()
    }

}