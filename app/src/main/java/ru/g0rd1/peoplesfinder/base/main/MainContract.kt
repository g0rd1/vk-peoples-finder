package ru.g0rd1.peoplesfinder.base.main

interface MainContract {

    interface View

    interface Presenter {
        fun setView(view: View)
    }

}