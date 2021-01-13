package ru.g0rd1.peoplesfinder.repo.group.vk

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.apiservice.response.GetGroupMembersResponse
import ru.g0rd1.peoplesfinder.mapper.GroupMapper
import ru.g0rd1.peoplesfinder.mapper.UserMapper
import ru.g0rd1.peoplesfinder.mapper.VkErrorMapper
import ru.g0rd1.peoplesfinder.model.GetGroupMembersResult
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import javax.inject.Inject

class HttpVkGroupsRepo @Inject constructor(
    private val apiClient: ApiClient,
    private val vkAccessRepo: VKAccessRepo,
    private val userMapper: UserMapper,
    private val groupMapper: GroupMapper,
    private val vkErrorMapper: VkErrorMapper
) : VkGroupsRepo {

    private var groupsCache: MutableMap<String, List<Group>> = mutableMapOf()

    override fun getGroups(): Single<List<Group>> {
        return getGroups(vkAccessRepo.getUserId().toString()).subscribeOnIo()
    }

    private fun getGroups(userId: String): Single<List<Group>> {
        if (groupsCache[userId] != null) return Single.just(groupsCache[userId])
        return apiClient.getGroups(
            userId = userId,
            accessToken = vkAccessRepo.getUserToken(),
            count = DEFAULT_GROUP_COUNT
        ).flatMap {
            when {
                it.error != null -> {
                    Single.error(it.error)
                }
                it.response != null -> {
                    val groups = it.response.items.mapIndexed { index, apiGroup ->
                        groupMapper.transform(apiGroup, index)
                    }
                    groupsCache[userId] = groups
                    Single.just(groups)
                }
                else -> {
                    throw IllegalArgumentException("Response and error can't be both null")
                }
            }
        }.subscribeOnIo()
    }

    override fun getGroupMembers(
        groupId: String,
        count: Int,
        offset: Int
    ): Single<GetGroupMembersResult> {
        if (count % DEFAULT_STEPS_COUNT != 0) throw IllegalArgumentException("the number should be divisible by $DEFAULT_STEPS_COUNT")
        val step = count / DEFAULT_STEPS_COUNT
        val code = ApiClient.getGroupMembersCode(
            groupId = groupId,
            offset = offset,
            step = step,
            stepsCount = DEFAULT_STEPS_COUNT
        )
        return apiClient.getGroupMembers(
            code,
            accessToken = vkAccessRepo.getUserToken()
        )
            .map { response ->
                when {
                    response.error != null -> {
                        GetGroupMembersResult.Error.Vk(vkErrorMapper.transform(response.error))
                    }
                    response.executeErrors?.isNotEmpty() == true -> {
                        GetGroupMembersResult.Error.Vk(vkErrorMapper.transform(response.executeErrors.first()))
                    }
                    response.rawResponse != null -> {
                        GetGroupMembersResult.Success(
                            getGetGroupMembersResponseList(response.rawResponse).flatMap {
                                it.items.map { apiUser ->
                                    userMapper.transform(apiUser)
                                }
                            }
                        )
                    }
                    else -> {
                        GetGroupMembersResult.Error.Generic(IllegalArgumentException("Response and error can't be both null"))
                    }
                }
            }
            .onErrorReturn {
                GetGroupMembersResult.Error.Generic(it)
            }
            .subscribeOnIo()
    }

    private fun getGetGroupMembersResponseList(json: JsonArray): List<GetGroupMembersResponse.Response> {
        val type = object : TypeToken<ArrayList<GetGroupMembersResponse.Response>>() {}.type
        return Gson().fromJson(json, type)
    }

    companion object {
        const val DEFAULT_GROUP_COUNT = 1000
        const val DEFAULT_STEPS_COUNT = 10
    }
}