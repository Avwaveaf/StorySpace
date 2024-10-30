package com.avwaveaf.storyspace.utils
import android.util.Log
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.Mockito

class LoggingRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                Mockito.mockStatic(Log::class.java).use { mockedLog ->
                    mockedLog.`when`<Int> { Log.v(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
                    mockedLog.`when`<Int> { Log.d(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
                    mockedLog.`when`<Int> { Log.i(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
                    mockedLog.`when`<Int> { Log.w(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
                    mockedLog.`when`<Int> { Log.e(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
                    mockedLog.`when`<Boolean> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }.thenReturn(true)
                    base.evaluate()
                }
            }
        }
    }
}