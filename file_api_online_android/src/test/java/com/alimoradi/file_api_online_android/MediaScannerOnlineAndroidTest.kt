package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.MediaScanner
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MediaScannerOnlineAndroidTest {

    @Mock
    private lateinit var listener: MediaScanner.RefreshListener

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun refreshNotifyListener() {
        // Given
        val path = "/path"
        val mediaScanner = createInstanceToTest()
        mediaScanner.registerListener(listener)

        // When
        mediaScanner.refresh(path)

        // Then
        Mockito.verify(listener).onContentChanged(path)
    }

    private fun createInstanceToTest(): MediaScanner {
        return MediaScannerOnlineAndroid()
    }
}
