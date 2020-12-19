package ru.g0rd1.peoplesfinder.base.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.auhorization.VkAuthorizationContract
import ru.g0rd1.peoplesfinder.base.navigator.Navigator
import ru.g0rd1.peoplesfinder.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), HasAndroidInjector {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var vkAuthorizationManager: VkAuthorizationContract.Manager

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

        vkAuthorizationManager.initiateHelper(this)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        val navController = findNavController(R.id.nav_host_fragment)
        navigator.setNavigator(navController) {
            binding.navView.visibility = if (it) View.VISIBLE else View.GONE
        }
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.groups_fragment,
                R.id.settings_fragment,
                R.id.results_fragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        if (savedInstanceState == null) {
            navController.setGraph(R.navigation.mobile_navigation, intent.extras)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

}
