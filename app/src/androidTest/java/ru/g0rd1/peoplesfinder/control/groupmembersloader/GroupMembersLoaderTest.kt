package ru.g0rd1.peoplesfinder.control.groupmembersloader

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.g0rd1.peoplesfinder.InjectableTest
import ru.g0rd1.peoplesfinder.db.Database
import ru.g0rd1.peoplesfinder.di.TestAppComponent
import ru.g0rd1.peoplesfinder.repo.group.GroupTestUtil.getTestGroupEntity
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Named
import kotlin.concurrent.schedule
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4ClassRunner::class)
class GroupMembersLoaderTest : InjectableTest() {

    @Inject
    @Named("TEST")
    lateinit var groupMembersLoaderManager: GroupMembersLoader.Manager

    @Inject
    @Named("TEST")
    lateinit var localGroupsRepo: LocalGroupsRepo

    @Inject
    @Named("TEST")
    lateinit var db: Database

    override fun inject(testAppComponent: TestAppComponent) {
        testAppComponent.inject(this)
    }

    @Before
    fun beforeTests() {
        groupMembersLoaderManager.clear()
        db.clearAllTables()
    }

    @Test
    fun testRapidlyStartStopPause() {
        Timber.d("testRapidlyStartStopPauseClear() starts")
        val id = 1
        val groupMembersCount = 25000
        localGroupsRepo.insert(getTestGroupEntity(id = id, membersCount = groupMembersCount))
            .blockingAwait()
        val groupsLoader = groupMembersLoaderManager.getLoader(id).blockingGet()
        Timber.d("groupsLoader: $groupsLoader")
        for (i in 0..200) {
            when (Math.random().roundToInt() % 4) {
                0 -> groupsLoader.start()
                1 -> groupsLoader.stop()
                2 -> groupsLoader.pause()
            }
        }
    }

    @Test
    fun testSlowStartStopPause() {
        Timber.d("testRapidlyStartStopPauseClear() starts")
        val id = 1
        val groupMembersCount = 25000
        localGroupsRepo.insert(getTestGroupEntity(id = id, membersCount = groupMembersCount))
            .blockingAwait()
        val groupsLoader = groupMembersLoaderManager.getLoader(id).blockingGet()
        Timber.d("groupsLoader: $groupsLoader")
        for (i in 0..10) {
            Thread.sleep(2000)
            when (Math.random().roundToInt() % 4) {
                0 -> groupsLoader.start()
                1 -> groupsLoader.stop()
                2 -> groupsLoader.pause()
            }
        }
    }

    @Test
    fun testFullLoad() {
        val lock = Object()
        var status = GroupMembersLoader.Status.STOPPED
        val actualCount = 50000
        localGroupsRepo.insert(getTestGroupEntity(id = 0, membersCount = actualCount))
            .blockingAwait()
        var expectedCount = 0
        val groupsLoader = groupMembersLoaderManager.getLoader(0).blockingGet()
        groupsLoader.addOnCountChangeListener {
            Timber.d("onCountChanged: oldCount: $expectedCount, newCount: $it")
            expectedCount = it
        }
        groupsLoader.addOnStatusChangeListener { newStatus ->
            Timber.d("newStatus changed to: $newStatus")
            if (newStatus == GroupMembersLoader.Status.FINISH) {
                Assert.assertEquals(expectedCount, actualCount)
            }
            synchronized(lock) {
                status = newStatus
                lock.notifyAll()
            }
        }
        Timber.d("before start")
        groupsLoader.start()
        Timber.d("after start")
        Timer().schedule(25000) {
            throw TimeoutException()
        }
        synchronized(lock) {
            while (status != GroupMembersLoader.Status.FINISH) {
                lock.wait()
            }
        }
    }
}