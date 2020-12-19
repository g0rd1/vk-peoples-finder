package ru.g0rd1.peoplesfinder.ui.authorization

import androidx.lifecycle.ViewModel
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import ru.g0rd1.peoplesfinder.base.auhorization.VkAuthorizationContract
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import javax.inject.Inject

class AuthorizationViewModel @Inject constructor(
    private val vkAccessRepo: VKAccessRepo,
    private val vkAuthorizationHelper: VkAuthorizationContract.Helper,
    private val errorHandler: Error.Handler,
    private val authorizationObserver: AuthorizationObserver
) : ViewModel() {

    init {
        if (!VK.isLoggedIn()) {
            authorize()
        } else {
            authorizationObserver.authorized()
        }
    }

    private fun authorize() {
        vkAuthorizationHelper.authorize(object : VKAuthCallback {

            override fun onLogin(token: VKAccessToken) {
                vkAccessRepo.setUserId(token.userId.toString())
                vkAccessRepo.setUserToken(token.accessToken)
                authorizationObserver.authorized()
            }

            override fun onLoginFailed(errorCode: Int) {
                errorHandler.handle(Exception("Ошибка авторизации")) { authorize() }
            }

        })
    }

}