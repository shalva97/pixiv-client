package xyz.cssxsh.pixiv.api.apps

import io.ktor.client.request.*
import xyz.cssxsh.pixiv.client.PixivClient
import xyz.cssxsh.pixiv.data.apps.UgoiraMetadata
import xyz.cssxsh.pixiv.*

suspend fun PixivClient.ugoiraMetadata(
    pid: Long,
    url: String = AppApi.UGOIRA_METADATA,
): UgoiraMetadata = useHttpClient { client ->
    client.get(url) {
        parameter("illust_id", pid)
    }
}
