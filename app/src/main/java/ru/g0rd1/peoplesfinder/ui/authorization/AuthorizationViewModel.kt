package ru.g0rd1.peoplesfinder.ui.authorization

import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.auhorization.VkAuthorizationContract
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val vkAccessRepo: VKAccessRepo,
    private val vkAuthorizationHelper: VkAuthorizationContract.Helper,
    private val errorHandler: Error.Handler,
    private val appNavigator: AppNavigator,
) : BaseViewModel() {

    override fun onStart() {
        if (VK.isLoggedIn()) {
            appNavigator.synchronization()
        } else {
            authorize()
        }
    }

    private fun authorize() {
        vkAuthorizationHelper.authorize(object : VKAuthCallback {

            override fun onLogin(token: VKAccessToken) {
                vkAccessRepo.setUserId(token.userId)
                vkAccessRepo.setUserToken(token.accessToken)
                appNavigator.synchronization()
            }

            override fun onLoginFailed(errorCode: Int) {
                errorHandler.handle(Exception("Ошибка авторизации")) { authorize() }
            }

        })
    }

}