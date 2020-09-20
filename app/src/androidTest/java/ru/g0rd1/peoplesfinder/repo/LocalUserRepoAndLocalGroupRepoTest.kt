package ru.g0rd1.peoplesfinder.repo

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.g0rd1.peoplesfinder.InjectableTest
import ru.g0rd1.peoplesfinder.db.Database
import ru.g0rd1.peoplesfinder.di.TestAppComponent
import ru.g0rd1.peoplesfinder.repo.group.GroupTestUtil.getTestGroupEntity
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.UserTestUtil.getTestUserEntity
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import javax.inject.Inject
import javax.inject.Named

class LocalUserRepoAndLocalGroupRepoTest : InjectableTest() {

    @Inject
    @Named("TEST")
    lateinit var localGroupsRepo: LocalGroupsRepo

    @Inject
    @Named("TEST")
    lateinit var localUsersRepo: LocalUsersRepo

    @Inject
    @Named("TEST")
    lateinit var db: Database

    @Before
    fun beforeTests() {
        db.clearAllTables()
    }

    @Test
    fun testCascadeInsertAndDeleteGroups() {
        val user1 = getTestUserEntity(1)
        val user2 = getTestUserEntity(2)
        val user3 = getTestUserEntity(3)
        val group1 = getTestGroupEntity(4)
        val group2 = getTestGroupEntity(5)
        val group3 = getTestGroupEntity(6)
        val user1GroupIds = listOf(group1, group2).map { it.id }
        val user2GroupIds = listOf(group1, group3).map { it.id }
        val user3GroupIds = listOf(group2, group3).map { it.id }
        localUsersRepo.insertWithGroups(user1, user1GroupIds).blockingAwait()
        localUsersRepo.insertWithGroups(user2, user2GroupIds).blockingAwait()
        localUsersRepo.insertWithGroups(user3, user3GroupIds).blockingAwait()
        localGroupsRepo.insert(listOf(group1, group2, group3)).blockingAwait()
        val groups = localGroupsRepo.getWithUsers().blockingGet()
        val actualInsertedGroup1Users =
            groups.first { it.id == group1.id }.users!!.sortedBy { it.id }
        val actualInsertedGroup2Users =
            groups.first { it.id == group2.id }.users!!.sortedBy { it.id }
        val actualInsertedGroup3Users =
            groups.first { it.id == group3.id }.users!!.sortedBy { it.id }
        val expectedInsertedGroup1Users = arrayOf(user1, user2)
        val expectedInsertedGroup2Users = arrayOf(user1, user3)
        val expectedInsertedGroup3Users = arrayOf(user2, user3)
        Assert.assertArrayEquals(
            expectedInsertedGroup1Users,
            actualInsertedGroup1Users.toTypedArray()
        )
        Assert.assertArrayEquals(
            expectedInsertedGroup2Users,
            actualInsertedGroup2Users.toTypedArray()
        )
        Assert.assertArrayEquals(
            expectedInsertedGroup3Users,
            actualInsertedGroup3Users.toTypedArray()
        )
        localGroupsRepo.delete(group1).blockingAwait()
        val users = localUsersRepo.getWithGroups().blockingGet()
        val actualUser1GroupsAfterDelete =
            users.first { it.id == user1.id }.groups!!.sortedBy { it.id }
        val actualUser2GroupsAfterDelete =
            users.first { it.id == user2.id }.groups!!.sortedBy { it.id }
        val actualUser3GroupsAfterDelete =
            users.first { it.id == user3.id }.groups!!.sortedBy { it.id }
        val expectedUser1GroupsAfterDelete = arrayOf(group2)
        val expectedUser2GroupsAfterDelete = arrayOf(group3)
        val expectedUser3GroupsAfterDelete = arrayOf(group2, group3)
        Assert.assertArrayEquals(
            expectedUser1GroupsAfterDelete,
            actualUser1GroupsAfterDelete.toTypedArray()
        )
        Assert.assertArrayEquals(
            expectedUser2GroupsAfterDelete,
            actualUser2GroupsAfterDelete.toTypedArray()
        )
        Assert.assertArrayEquals(
            expectedUser3GroupsAfterDelete,
            actualUser3GroupsAfterDelete.toTypedArray()
        )
    }

    @Test
    fun testCascadeInsertAndDeleteUsers() {
        val user1 = getTestUserEntity(1)
        val user2 = getTestUserEntity(2)
        val user3 = getTestUserEntity(3)
        val group1 = getTestGroupEntity(4)
        val group2 = getTestGroupEntity(5)
        val group3 = getTestGroupEntity(6)
        val group1UserIds = listOf(user1, user2).map { it.id }
        val group2UserIds = listOf(user1, user3).map { it.id }
        val group3UserIds = listOf(user2, user3).map { it.id }
        localGroupsRepo.insertWithUsers(group1, group1UserIds).blockingAwait()
        localGroupsRepo.insertWithUsers(group2, group2UserIds).blockingAwait()
        localGroupsRepo.insertWithUsers(group3, group3UserIds).blockingAwait()
        localUsersRepo.insert(listOf(user1, user2, user3)).blockingAwait()
        val users = localUsersRepo.getWithGroups().blockingGet()
        val actualInsertedUser1Groups =
            users.first { it.id == user1.id }.groups!!.sortedBy { it.id }
        val actualInsertedUser2Groups =
            users.first { it.id == user2.id }.groups!!.sortedBy { it.id }
        val actualInsertedUser3Groups =
            users.first { it.id == user3.id }.groups!!.sortedBy { it.id }
        val expectedInsertedUser1Groups = arrayOf(group1, group2)
        val expectedInsertedUser2Groups = arrayOf(group1, group3)
        val expectedInsertedUser3Groups = arrayOf(group2, group3)
        Assert.assertArrayEquals(
            expectedInsertedUser1Groups,
            actualInsertedUser1Groups.toTypedArray()
        )
        Assert.assertArrayEquals(
            expectedInsertedUser2Groups,
            actualInsertedUser2Groups.toTypedArray()
        )
        Assert.assertArrayEquals(
            expectedInsertedUser3Groups,
            actualInsertedUser3Groups.toTypedArray()
        )
        localUsersRepo.delete(user1).blockingAwait()
        val groups = localGroupsRepo.getWithUsers().blockingGet()
        val actualGroup1UsersAfterDelete =
            groups.first { it.id == group1.id }.users!!.sortedBy { it.id }
        val actualGroup2UsersAfterDelete =
            groups.first { it.id == group2.id }.users!!.sortedBy { it.id }
        val actualGroup3UsersAfterDelete =
            groups.first { it.id == group3.id }.users!!.sortedBy { it.id }
        val expectedGroup1UsersAfterDelete = arrayOf(user2)
        val expectedGroup2UsersAfterDelete = arrayOf(user3)
        val expectedGroup3UsersAfterDelete = arrayOf(user2, user3)
        Assert.assertArrayEquals(
            expectedGroup1UsersAfterDelete,
            actualGroup1UsersAfterDelete.toTypedArray()
        )
        Assert.assertArrayEquals(
            expectedGroup2UsersAfterDelete,
            actualGroup2UsersAfterDelete.toTypedArray()
        )
        Assert.assertArrayEquals(
            expectedGroup3UsersAfterDelete,
            actualGroup3UsersAfterDelete.toTypedArray()
        )
    }

    override fun inject(testAppComponent: TestAppComponent) {
        testAppComponent.inject(this)
    }

}