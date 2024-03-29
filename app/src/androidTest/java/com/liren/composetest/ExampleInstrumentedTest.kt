package com.liren.composetest

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.liren.composetest", appContext.packageName)

        GlobalScope.launch(Dispatchers.Unconfined) {
            println("My job is ${coroutineContext[Job]}")
            println("running in ${Thread.currentThread().name}")
            withContext(Dispatchers.IO) {
                println("running in ${Thread.currentThread().name}")
            }
        }
    }
}