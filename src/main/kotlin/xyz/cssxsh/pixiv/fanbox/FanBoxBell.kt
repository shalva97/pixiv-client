package xyz.cssxsh.pixiv.fanbox

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import xyz.cssxsh.pixiv.*
import xyz.cssxsh.pixiv.web.*

class FanBoxBell(val client: PixivWebClient) {
    companion object {
        internal const val COUNT_UNREAD = "https://api.fanbox.cc/bell.countUnread"
    }

    suspend fun countUnread(): Int {
        return client.ajax<JsonObject>(api = COUNT_UNREAD) {
            header(HttpHeaders.Origin, "https://www.fanbox.cc")
            header(HttpHeaders.Referrer, "https://www.fanbox.cc/")
        }["count"]!!.jsonPrimitive.int
    }
}