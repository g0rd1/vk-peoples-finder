package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.mapper.GroupMapper
import ru.g0rd1.peoplesfinder.mapper.UserMapper
import ru.g0rd1.peoplesfinder.mapper.VkErrorMapper
import ru.g0rd1.peoplesfinder.mapper.VkResultMapper
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import ru.g0rd1.peoplesfinder.repo.vk.VkRepo
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import javax.inject.Inject

class HttpVkGroupsRepo @Inject constructor(
    private val vkRepo: VkRepo,
    private val vkAccessRepo: VKAccessRepo,
    private val userMapper: UserMapper,
    private val groupMapper: GroupMapper,
    private val vkErrorMapper: VkErrorMapper,
    private val vkResultMapper: VkResultMapper,
) : VkGroupsRepo {

    // private var usersGroupsCache: MutableMap<Int, List<Group>> = mutableMapOf()

    override fun getGroups(): Single<VkResult<List<Group>>> {
        val userId = vkAccessRepo.getUserId()
        // if (usersGroupsCache[userId] != null) return Single.just(VkResult.Success(usersGroupsCache[userId] ?: listOf()))
        return vkRepo.getGroups(
            userId = userId,
            count = DEFAULT_GROUP_COUNT
        ).map {
            try {
                val vkResult = vkResultMapper.transform(it) { apiGroups ->
                    apiGroups.mapIndexed { index, apiGroup ->
                        groupMapper.transform(apiGroup, index, true)
                    }
                }
                // if (vkResult is VkResult.Success) {
                //     usersGroupsCache[userId] = vkResult.data
                // }
                vkResult
            } catch (t: Throwable) {
                VkResult.Error.Generic(t)
            }
        }.subscribeOnIo()
    }

    override fun searchGroups(searchText: String): Single<VkResult<List<Group>>> {
        return vkRepo.searchGroups(searchText).map {
            try {
                vkResultMapper.transform(it) { apiGroups ->
                    apiGroups.map { apiGroup ->
                        groupMapper.transform(apiGroup, null, false)
                    }
                }
            } catch (t: Throwable) {
                VkResult.Error.Generic(t)
            }
        }.subscribeOnIo()
    }

    override fun getGroupMembers(
        groupId: String,
        count: Int,
        offset: Int,
    ): Single<VkResult<List<User>>> {
        return vkRepo.getGroupMembers(groupId, offset, count).map { apiVkResult ->
            vkResultMapper.transform(apiVkResult) { apiUsers ->
                apiUsers.map { userMapper.transform(it) }
            }
        }.subscribeOnIo()
    }

    companion object {
        const val DEFAULT_GROUP_COUNT = 1000
        const val DEFAULT_STEPS_COUNT = 10
    }
}