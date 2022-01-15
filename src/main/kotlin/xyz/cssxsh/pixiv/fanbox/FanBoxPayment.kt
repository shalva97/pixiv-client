package xyz.cssxsh.pixiv.fanbox

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.pixiv.*
import xyz.cssxsh.pixiv.web.ajax

class FanBoxPayment(val client: PixivWebClient) {
    companion object {
        internal const val LIST_PAID = "https://api.fanbox.cc/payment.listPaid"

        internal const val LIST_UNPAID = "https://api.fanbox.cc/payment.listUnpaid"
    }

    suspend fun listPaid(): List<JsonObject> {
        return client.ajax(api = LIST_PAID) {
            header(HttpHeaders.Origin, "https://www.fanbox.cc")
            header(HttpHeaders.Referrer, "https://www.fanbox.cc/")
        }
    }

    suspend fun listUnpaid(): List<JsonObject> {
        return client.ajax(api = LIST_UNPAID) {
            header(HttpHeaders.Origin, "https://www.fanbox.cc")
            header(HttpHeaders.Referrer, "https://www.fanbox.cc/")
        }
    }
}