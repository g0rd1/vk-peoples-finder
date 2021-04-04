package ru.g0rd1.peoplesfinder.db.helper

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import ru.g0rd1.peoplesfinder.common.PriorityQueueManager
import ru.g0rd1.peoplesfinder.common.enums.Relation
import ru.g0rd1.peoplesfinder.common.enums.Sex
import ru.g0rd1.peoplesfinder.model.City
import ru.g0rd1.peoplesfinder.model.Country
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.model.UserType
import kotlin.random.Random


class UserQueryBuilderTest {

    @Test
    fun getUsersQuery() {
        val filterParameters = FilterParameters(
            ageFrom = FilterParameters.Age.Specific(16),
            ageTo = FilterParameters.Age.Specific(20),
            sex = FilterParameters.Sex.Specific(Sex.FEMALE),
            relation = FilterParameters.Relation.Specific(Relation.ACTIVELY_SEARCHING),
            country = FilterParameters.Country.Specific(Country(1, "1")),
            city = FilterParameters.City.Specific(City(1, "1", false)),
            hasPhoto = true,
            notClosed = true,
            requiredGroupIds = listOf(62816733)
        )
        val usersQuery = UserQueryBuilder.getUsersQuery(
            filterParameters,
            3,
            listOf(UserType.BLOCKED, UserType.FAVORITE)
        )
        println("TEST:\n${usersQuery.sql}")
    }

    @Test
    fun rxTest() {
        val singles = (0L..11L).map {
            val single = Single.fromCallable {
                val delay = 990L + Random.nextLong(10, 20)
                Thread.sleep(delay)
                it to delay
            }.subscribeOn(Schedulers.computation())
            single
        }
        Flowable.fromIterable(singles).flatMap({ it.toFlowable() }, 3).blockingSubscribe { (number, delay) ->
            println("TEST: $number $delay")
        }
    }
}