package ru.g0rd1.peoplesfinder.base.main

import javax.inject.Inject

class MainPresenter @Inject constructor() : MainContract.Presenter {

    private lateinit var view: MainContract.View

    override fun setView(view: MainContract.View) {
        this.view = view
    }

}