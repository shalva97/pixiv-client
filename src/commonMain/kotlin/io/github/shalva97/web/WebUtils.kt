package io.github.shalva97.web

import io.github.shalva97.PixivJson
import io.github.shalva97.PixivWebClient
import io.github.shalva97.exception.WebApiException
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
public class WebApiResult(
    @SerialName("body")
    public val body: JsonElement,
    @SerialName("error")
    public val error: Boolean = false,
    @SerialName("message")
    public val message: String = "",
)

public suspend inline fun <reified T> PixivWebClient.ajax(
    api: String,
    crossinline block: HttpRequestBuilder.() -> Unit
): T = useHttpClient { client ->
    val result: WebApiResult = client.request(api, block).body()
    if (result.error) throw WebApiException(result)
    PixivJson.decodeFromJsonElement(result.body)
}

public suspend fun PixivWebClient.location(url: Url): String {
    return useHttpClient { client ->
        client.config {
            expectSuccess = false
            followRedirects = false
        }.head(url).headers[HttpHeaders.Location]
    } ?: throw IllegalStateException("redirect failure $url, Not Found Location.")
}

public object WepApiSet : KSerializer<Set<Long>> {
    private val serializer = SetSerializer(Long.serializer())

    override val descriptor: SerialDescriptor
        get() = serializer.descriptor

    override fun deserialize(decoder: Decoder): Set<Long> {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonObject) element.mapTo(HashSet()) { it.key.toLong() } else emptySet()
    }

    override fun serialize(encoder: Encoder, value: Set<Long>) {
        if (value.isNotEmpty()) {
            encoder.encodeSerializableValue(JsonObject.serializer(), buildJsonObject {
                for (item in value) {
                    put(item.toString(), JsonNull)
                }
            })
        } else {
            encoder.encodeSerializableValue(serializer, emptySet())
        }
    }
}

public sealed class WebApiMap<V : WebWorkInfo>(valueSerializer: KSerializer<V>) : KSerializer<Map<Long, V>> {
    private val serializer: KSerializer<Map<Long, V>> = MapSerializer(Long.serializer(), valueSerializer)

    override val descriptor: SerialDescriptor get() = serializer.descriptor

    override fun deserialize(decoder: Decoder): Map<Long, V> {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonObject) {
            decoder.decodeSerializableValue(serializer)
        } else {
            emptyMap()
        }
    }

    override fun serialize(encoder: Encoder, value: Map<Long, V>) {
        if (value.isNotEmpty()) {
            encoder.encodeSerializableValue(serializer, value)
        } else {
            encoder.encodeSerializableValue(ListSerializer(Unit.serializer()), emptyList())
        }
    }


    public object Illust : WebApiMap<WebIllust>(WebIllust.serializer())

    public object Novel : WebApiMap<WebNovel>(WebNovel.serializer())
}

// https://www.pixiv.net/ajax/tags/frequent/illust?lang=zh