package com.alimoradi.file_api_android

import com.alimoradi.file_api_android.internal.MediaScannerAndroid
import com.alimoradi.file_api.MediaScanner
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MediaScannerTest {

    @Mock
    private lateinit var addOn: MediaScannerAndroid.AddOn

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun refresh() {
        // Given
        val path = "/path"
        val mediaScanner = createInstanceToTest()

        // When
        mediaScanner.refresh(path)

        // Then
        Mockito.verify(addOn).refreshSystemMediaScanDataBase(path)
    }

    private fun createInstanceToTest(): MediaScanner {
        return MediaScannerAndroid(
            addOn
        )
    }
}
