package ru.g0rd1.peoplesfinder.base.auhorization

import android.app.Activity
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import javax.inject.Inject

class VkAuthorizationManager @Inject constructor() : VkAuthorizationContract.Manager {

    private lateinit var activity: Activity
    private lateinit var callback: VKAuthCallback

    private val helper: VkAuthorizationContract.Helper = object : VkAuthorizationContract.Helper {

        override fun authorize(callback: VKAuthCallback) {
            this@VkAuthorizationManager.callback = callback
            VK.login(activity)
        }

    }

    override fun initiateHelper(activity: Activity) {
        this.activity = activity
    }

    override fun getHelper(): VkAuthorizationContract.Helper = helper

    override fun onLogin(token: VKAccessToken) {
        callback.onLogin(token)
    }

    override fun onLoginFailed(errorCode: Int) {
        callback.onLoginFailed(errorCode)
    }

}