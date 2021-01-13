package ru.g0rd1.peoplesfinder.repo.access

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPrefVKAccessRepo @Inject constructor(
    context: Context
) : VKAccessRepo {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    override fun setUserToken(token: String) {
        sharedPreferences.edit().putString(USER_TOKEN_KEY, token).apply()
    }

    override fun getUserToken(): String {
        return sharedPreferences.getString(USER_TOKEN_KEY, null)
            ?: throw IllegalStateException("No token in shared preferences")
    }

    override fun setUserId(id: Int) {
        sharedPreferences.edit().putInt(USER_ID_KEY, id).apply()
    }

    override fun getUserId(): Int {
        return sharedPreferences.getInt(USER_ID_KEY, -1)
    }

    companion object {

        private val SHARED_PREF_NAME = SharedPrefVKAccessRepo::class.java.simpleName
        private const val USER_TOKEN_KEY = "userTokenKey"
        private const val USER_ID_KEY = "userIdKey"

    }

}