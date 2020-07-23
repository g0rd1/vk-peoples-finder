package ru.g0rd1.peoplesfinder.ui.authorization

interface AuthorizationContract {

    interface View

    interface Presenter {
        fun setView(view: View)
        fun onStart()
        fun onStop()
    }

}