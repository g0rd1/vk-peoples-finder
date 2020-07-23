package ru.g0rd1.peoplesfinder.ui.authorization

import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import ru.g0rd1.peoplesfinder.base.auhorization.VkAuthorizationContract
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.base.navigator.Navigator
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import javax.inject.Inject

class AuthorizationPresenter @Inject constructor(
    private val vkAccessRepo: VKAccessRepo,
    private val navigator: Navigator,
    private val vkAuthorizationHelper: VkAuthorizationContract.Helper,
    private val errorHandler: Error.Handler
) : AuthorizationContract.Presenter {

    private lateinit var view: AuthorizationContract.View

    override fun setView(view: AuthorizationContract.View) {
        this.view = view
    }

    override fun onStart() {
        if (!VK.isLoggedIn()) {
            authorize()
        } else {
            navigator.groups()
        }
    }

    private fun authorize() {
        vkAuthorizationHelper.authorize(object : VKAuthCallback {

            override fun onLogin(token: VKAccessToken) {
                vkAccessRepo.setUserId(token.userId.toString())
                vkAccessRepo.setUserToken(token.accessToken)
                navigator.groups()
            }

            override fun onLoginFailed(errorCode: Int) {
                errorHandler.handle(Exception("Ошибка авторизации")) { authorize() }
            }

        })
    }

    override fun onStop() {
        // DO NOTHING
    }

}