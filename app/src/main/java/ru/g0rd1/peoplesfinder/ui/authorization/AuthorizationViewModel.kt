package ru.g0rd1.peoplesfinder.ui.authorization

import android.net.Uri
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import javax.inject.Inject


@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val vkAccessRepo: VKAccessRepo,
    private val appNavigator: AppNavigator,
    private val resourceManager: ResourceManager,
) : BaseViewModel() {

    val reload = SingleLiveEvent<Unit>()

    val url = ObservableField<String>()

    val errorVisible = ObservableBoolean()
    val startPageVisible = ObservableBoolean(true)

    val errorText = resourceManager.getString(R.string.fragment_authorization_error_text)

    val retryFunction: () -> Unit = {
        errorVisible.set(false)
        reload.call()
    }

    val webViewClient: WebViewClient = object : WebViewClient() {

        override fun onReceivedError(
            view: WebView?, request: WebResourceRequest?,
            error: WebResourceError?,
        ) {
            super.onReceivedError(view, request, error)
            errorVisible.set(true)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            errorVisible.set(false)
            if (url.isNullOrBlank()) {
                goToAuthorizePage()
                return
            }
            if (!url.startsWith("https://oauth.vk.com/blank.html")) return
            val uri = Uri.parse(url.replace("#", "?"))
            val token: String? = uri.getQueryParameter("access_token")
            val id: String? = uri.getQueryParameter("user_id")
            if (token == null || id == null) {
                errorVisible.set(true)
                return
            }
            vkAccessRepo.setUserId(id.toInt())
            vkAccessRepo.setUserToken(token)
            appNavigator.synchronization()
        }

    }

    override fun onStart() {
        if (vkAccessRepo.isHasToken()) {
            appNavigator.synchronization()
        } else {
            startPageVisible.set(true)
        }
    }

    fun toGetPermission() {
        goToAuthorizePage()
        startPageVisible.set(false)
    }

    private fun goToAuthorizePage() {
        val appId = resourceManager.getInteger(R.integer.com_vk_sdk_AppId)
        val scope = OFFLINE_SCOPE + GROUPS_SCOPE
        val authorizeUrl = "https://oauth.vk.com/authorize?client_id=$appId&redirect_uri=$REDIRECT_URI&scope=$scope&response_type=token&v=${ApiClient.API_VERSION}"
        url.set(authorizeUrl)
    }

    companion object {
        private const val REDIRECT_URI = "https://oauth.vk.com/blank.html"
        private const val OFFLINE_SCOPE = 65536
        private const val GROUPS_SCOPE = 262144
    }

}