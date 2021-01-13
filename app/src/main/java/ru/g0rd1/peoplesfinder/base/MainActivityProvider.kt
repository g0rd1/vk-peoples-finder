package ru.g0rd1.peoplesfinder.base

import ru.g0rd1.peoplesfinder.base.main.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainActivityProvider @Inject constructor() {

    private lateinit var mainActivity: MainActivity

    fun setMainActivity(mainActivity: MainActivity) {
        this.mainActivity = mainActivity
    }

    fun getMainActivity(): MainActivity = mainActivity

}