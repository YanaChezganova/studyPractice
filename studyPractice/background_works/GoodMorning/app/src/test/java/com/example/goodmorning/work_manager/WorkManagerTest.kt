package com.example.goodmorning.work_manager

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.example.goodmorning.location.LastLocationProvider
import com.example.goodmorning.ui.main.MainFragment
import com.example.goodmorning.ui.main.MainViewModel
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

class WorkManagerTest {

    val getWorkManager = mock<CountWorker>()

    @Test
    fun `should detect rise time`(){
        val savResult = 1684275477000
        Mockito.`when`(getWorkManager.counterSunrise(any(),any(),any(),any(),any(),any() )).thenReturn(savResult)
      val actual = getWorkManager.counterSunrise(2023, 5, 17,
          55.0170749, 83.0013537, "Asia/Bangkok")
        val expected = 1684275477000
        Assertions.assertEquals(expected,actual)
    }

    @AfterEach
    fun afterEach(){
        Mockito.reset(getWorkManager)
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @BeforeEach
    fun beforeEach(){
        ArchTaskExecutor.getInstance().setDelegate(
            object : TaskExecutor(){
                override fun executeOnDiskIO(runnable: Runnable) {
                    runnable.run()
                }

                override fun isMainThread(): Boolean {
                    return true
                }

                override fun postToMainThread(runnable: Runnable) {
                    runnable.run()
                }
            }
        )
    }


}