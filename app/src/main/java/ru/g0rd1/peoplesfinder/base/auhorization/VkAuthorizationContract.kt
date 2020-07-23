package ru.g0rd1.peoplesfinder.base.auhorization

import android.app.Activity
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback

interface VkAuthorizationContract {

    interface Manager {
        fun initiateHelper(activity: Activity)
        fun getHelper(): Helper
        fun onLogin(token: VKAccessToken)
        fun onLoginFailed(errorCode: Int)
    }

    interface Helper {
        fun authorize(callback: VKAuthCallback)
    }

}
