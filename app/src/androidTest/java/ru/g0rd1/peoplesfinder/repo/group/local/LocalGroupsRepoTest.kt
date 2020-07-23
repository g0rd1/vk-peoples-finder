package ru.g0rd1.peoplesfinder.repo.group.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.g0rd1.peoplesfinder.InjectableTest
import ru.g0rd1.peoplesfinder.db.Database
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.di.TestAppComponent
import ru.g0rd1.peoplesfinder.repo.group.GroupTestUtil.getTestGroupEntity
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@RunWith(AndroidJUnit4ClassRunner::class)
class LocalGroupsRepoTest : InjectableTest() {

    @Inject
    @Named("TEST")
    lateinit var localGroupsRepo: LocalGroupsRepo

    @Inject
    @Named("TEST")
    lateinit var db: Database

    @Before
    fun beforeTests() {
        db.clearAllTables()
    }

    @Test
    fun update() {
        val id = 1
        val group1 = getTestGroupEntity(id)
        localGroupsRepo.insert(group1).blockingAwait()
        val newLoadedMembersCount = 25000
        val newAllMembersLoadedDate = Date()
        localGroupsRepo.update(group1.id, newLoadedMembersCount, newAllMembersLoadedDate)
            .blockingAwait()
        val actualGroup = localGroupsRepo.get().blockingGet().first()
        val expectedGroup = getTestGroupEntity(
            id = id,
            allMembersLoadedDate = newAllMembersLoadedDate,
            loadedMembersCount = newLoadedMembersCount
        )
        Assert.assertEquals(expectedGroup.loadedMembersCount, actualGroup.loadedMembersCount)
        Assert.assertEquals(expectedGroup.allMembersLoadedDate, actualGroup.allMembersLoadedDate)
    }

    @Test
    fun getGroupWithNotExistId() {
        val actualGroup: GroupEntity? = localGroupsRepo.get(0).blockingGet().value
        val expectedGroup: GroupEntity? = null
        Assert.assertEquals(expectedGroup, actualGroup)
    }

    override fun inject(testAppComponent: TestAppComponent) {
        testAppComponent.inject(this)
    }

}