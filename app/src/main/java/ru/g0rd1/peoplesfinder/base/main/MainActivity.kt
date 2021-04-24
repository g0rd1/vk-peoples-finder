package ru.g0rd1.peoplesfinder.base.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.MainActivityProvider
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.databinding.ActivityMainBinding
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationFragment
import ru.g0rd1.peoplesfinder.ui.groups.GroupsFragment
import ru.g0rd1.peoplesfinder.ui.settings.SettingsFragment
import ru.g0rd1.peoplesfinder.ui.synchronization.SynchronizationFragment
import ru.g0rd1.peoplesfinder.ui.userDetail.UserDetailDialog
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var mainActivityProvider: MainActivityProvider

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
            is UserDetailDialog -> {
                binding?.navView?.visibility = View.VISIBLE
                binding?.navView?.selectedItemId = R.id.user_detail_fragment
            }
            is ListFragment -> {
                binding?.navView?.visibility = View.VISIBLE
                binding?.navView?.selectedItemId = R.id.lists_fragment
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityProvider.setMainActivity(this)

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
                R.id.user_detail_fragment -> {
                    appNavigator.userDetail()
                    true
                }
                R.id.lists_fragment -> {
                    appNavigator.lists()
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

}
