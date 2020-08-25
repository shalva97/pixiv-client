package xyz.cssxsh.pixiv.client.api.aapi

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions
import xyz.cssxsh.pixiv.client.api.ApiTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UgoiraTest: ApiTest() {
    @Test
    fun getUgoiraMetadata() = runBlocking<Unit> {
        val data = pixivClient.getUgoiraMetadata(79007274L)
        Assertions.assertTrue(data.ugoiraMetadata.frames.isNotEmpty())
        Assertions.assertTrue(data.ugoiraMetadata.zipUrls.isNotEmpty())
    }
}