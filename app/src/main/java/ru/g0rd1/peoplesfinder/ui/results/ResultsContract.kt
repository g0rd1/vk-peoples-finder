package ru.g0rd1.peoplesfinder.ui.results

import ru.g0rd1.peoplesfinder.db.entity.UserEntity

interface ResultsContract {

    interface View {
        fun setUsers(users: List<UserEntity>)
    }

    interface Presenter {
        fun setView(view: View)
        fun onStart()
        fun onStop()
    }

}