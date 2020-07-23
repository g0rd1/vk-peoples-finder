package ru.g0rd1.peoplesfinder

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import ru.g0rd1.peoplesfinder.base.TestBaseApplication

class MockTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestBaseApplication::class.java.name, context)
    }
}