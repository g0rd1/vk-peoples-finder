package ru.g0rd1.peoplesfinder.base.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.MainActivityProvider
import ru.g0rd1.peoplesfinder.base.auhorization.VkAuthorizationContract
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.databinding.ActivityMainBinding
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationFragment
import ru.g0rd1.peoplesfinder.ui.groups.GroupsFragment
import ru.g0rd1.peoplesfinder.ui.results.ResultsFragment
import ru.g0rd1.peoplesfinder.ui.settings.SettingsFragment
import ru.g0rd1.peoplesfinder.ui.synchronization.SynchronizationFragment
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), HasAndroidInjector {

    private var binding: ActivityMainBinding? = null

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var mainActivityProvider: MainActivityProvider

    @Inject
    lateinit var vkAuthorizationManager: VkAuthorizationContract.Manager

    private val onFragmentChangeListener: (Fragment) -> Unit = { fragment ->
        when (fragment) {
            is AuthorizationFragment -> {
                binding?.navView?.visibility = View.GONE
            }
            is SynchronizationFragment -> {
                binding?.navView?.visibility = View.GONE
            }
            is GroupsFragment -> {
                binding?.navView?.visibility = View.VISIBLE
                binding?.navView?.selectedItemId = R.id.groups_fragment
            }
            is SettingsFragment -> {
                binding?.navView?.visibility = View.VISIBLE
                binding?.navView?.selectedItemId = R.id.settings_fragment
            }
            is ResultsFragment -> {
                binding?.navView?.visibility = View.VISIBLE
                binding?.navView?.selectedItemId = R.id.results_fragment
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                vkAuthorizationManager.onLogin(token)
            }

            override fun onLoginFailed(errorCode: Int) {
                vkAuthorizationManager.onLoginFailed(errorCode)
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityProvider.setMainActivity(this)

        vkAuthorizationManager.initiateHelper(this)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        appNavigator.addOnFragmentChangeListener(onFragmentChangeListener)

        binding?.navView?.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.groups_fragment -> {
                    appNavigator.groups()
                    true
                }
                R.id.settings_fragment -> {
                    appNavigator.settings()
                    true
                }
                R.id.results_fragment -> {
                    appNavigator.results()
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            appNavigator.authorization()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        appNavigator.removeOnFragmentChangeListener(onFragmentChangeListener)
    }

    override fun onBackPressed() {
        appNavigator.back()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

}
