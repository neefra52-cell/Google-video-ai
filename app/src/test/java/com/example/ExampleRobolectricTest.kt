package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Veo Studio AI", appName)
  }

  @Test
  fun `verify max duration formatting`() {
    val maxDuration = com.example.model.DurationFormatter.MAX_MINUTES
    assertEquals(1_000_000_000L, maxDuration)
    val formatted = com.example.model.DurationFormatter.formatMinutesToHumanReadable(maxDuration)
    org.junit.Assert.assertTrue(formatted.contains("1,000,000,000"))
  }
}
