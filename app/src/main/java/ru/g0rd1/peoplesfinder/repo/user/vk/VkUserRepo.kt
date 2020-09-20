package ru.g0rd1.peoplesfinder.repo.user.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.User

interface VkUserRepo {

    fun getUser(): Single<User>

}