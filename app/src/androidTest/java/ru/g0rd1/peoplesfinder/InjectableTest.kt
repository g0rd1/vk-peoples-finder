package ru.g0rd1.peoplesfinder

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import ru.g0rd1.peoplesfinder.base.TestBaseApplication
import ru.g0rd1.peoplesfinder.di.TestAppComponent

abstract class InjectableTest {

    private lateinit var appComponent: TestAppComponent

    private var injected = false

    @Before
    fun before() {
        if (injected) return
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication
        appComponent = app.appComponent
        inject()
        injected = true
    }

    abstract fun inject(testAppComponent: TestAppComponent = appComponent)

    companion object

}