package ru.g0rd1.peoplesfinder.repo.access

import android.content.Context
import ru.g0rd1.peoplesfinder.repo.SimpleSharedPrefRepo
import javax.inject.Inject

class SharedPrefVKAccessRepo @Inject constructor(
    context: Context
) : SimpleSharedPrefRepo(SHARED_PREF_NAME, context), VKAccessRepo {

    override fun setUserToken(token: String) {
        putString(USER_TOKEN_KEY, token)
    }

    override fun getUserToken(): String {
        return getStringOrNull(USER_TOKEN_KEY) ?: throw IllegalStateException("No token in shared preferences")
    }

    override fun setUserId(id: Int) {
        putInt(USER_ID_KEY, id)
    }

    override fun getUserId(): Int {
        return getInt(USER_ID_KEY, -1)
    }

    companion object {
        private const val SHARED_PREF_NAME = "VKAccessRepo"
        private const val USER_TOKEN_KEY = "userTokenKey"
        private const val USER_ID_KEY = "userIdKey"
    }

}