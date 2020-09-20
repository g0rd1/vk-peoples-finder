package ru.g0rd1.peoplesfinder.repo.user.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.model.User
import javax.inject.Inject

class HttpVkUserRepo @Inject constructor(
    private val apiClient: ApiClient
) : VkUserRepo {

    private var userCache: User? = null

    override fun getUser(): Single<User> {
        if (userCache != null) return Single.just(userCache)
        return apiClient.getUser().map {
            val user = it.response.first()
            userCache = user
            user
        }
    }

}