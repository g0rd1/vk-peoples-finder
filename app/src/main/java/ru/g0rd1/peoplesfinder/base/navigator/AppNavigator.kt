package ru.g0rd1.peoplesfinder.base.navigator

import androidx.navigation.NavController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationObserver
import ru.g0rd1.peoplesfinder.ui.synchronization.SynchronizationObserver
import ru.g0rd1.peoplesfinder.util.observeOnUI
import timber.log.Timber
import javax.inject.Inject

class AppNavigator @Inject constructor(
    private val authorizationObserver: AuthorizationObserver,
    private val synchronizationObserver: SynchronizationObserver
) : Navigator {

    private lateinit var navigator: NavController

    private lateinit var showNavigationMenu: (Boolean) -> Unit

    private val disposables = CompositeDisposable()

    init {
        observe()
    }

    private fun observe() {
        authorizationObserver.observe()
            .observeOnUI()
            .subscribe(
                { synchronization() },
                Timber::e
            )
            .addTo(disposables)
        synchronizationObserver.observe()
            .observeOnUI()
            .subscribe(
                { groups() },
                Timber::e
            )
            .addTo(disposables)
        observerDestinationChanged()
    }

    private fun observerDestinationChanged() {
        navigator.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.groups_fragment,
                R.id.settings_fragment,
                R.id.results_fragment -> {
                    showNavigationMenu(true)
                }
                else -> {
                    showNavigationMenu(false)
                }
            }
        }
    }

    override fun setNavigator(
        navController: NavController,
        showNavigationMenu: (Boolean) -> Unit
    ) {
        navigator = navController
        this.showNavigationMenu = showNavigationMenu
    }

    override fun authorization() {
        navigator.navigate(R.id.authorization_fragment)
    }

    override fun synchronization() {
        navigator.navigate(R.id.synchronize_fragment)
    }

    override fun groups() {
        navigator.navigate(R.id.groups_fragment)
    }

}