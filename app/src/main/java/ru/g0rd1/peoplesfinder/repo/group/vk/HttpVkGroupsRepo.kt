package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
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
    private val vkResultMapper: VkResultMapper
) : VkGroupsRepo {

    private var groupsCache: MutableMap<Int, List<Group>> = mutableMapOf()

    override fun getGroups(): Single<VkResult<List<Group>>> {
        val userId = vkAccessRepo.getUserId()
        if (groupsCache[userId] != null) return Single.just(VkResult.Success(groupsCache[userId] ?: listOf()))
        return vkRepo.getGroups(
            userId = userId,
            count = DEFAULT_GROUP_COUNT
        ).map {
            val vkResult = vkResultMapper.transform(it) { apiGroups ->
                apiGroups.mapIndexed { index, apiGroup ->
                    groupMapper.transform(apiGroup, index)
                }
            }
            if (vkResult is VkResult.Success) {
                groupsCache[userId] = vkResult.data
            }
            vkResult
        }.subscribeOnIo()
    }

    override fun getGroupMembers(
        groupId: String,
        count: Int,
        offset: Int
    ): Single<VkResult<List<User>>> {
        if (count % DEFAULT_STEPS_COUNT != 0) throw IllegalArgumentException("the number should be divisible by $DEFAULT_STEPS_COUNT")
        val step = count / DEFAULT_STEPS_COUNT
        val code = ApiClient.getGroupMembersCode(
            groupId = groupId,
            offset = offset,
            step = step,
            stepsCount = DEFAULT_STEPS_COUNT
        )
        return vkRepo.getGroupMembers(code).map { apiVkResult ->
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