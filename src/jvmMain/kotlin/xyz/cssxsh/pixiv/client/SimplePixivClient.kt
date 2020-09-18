package xyz.cssxsh.pixiv.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineName
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okio.ByteString.Companion.toByteString
import okhttp3.dnsoverhttps.DnsOverHttps
import xyz.cssxsh.pixiv.client.exception.ApiException
import xyz.cssxsh.pixiv.client.exception.AuthException
import kotlin.coroutines.CoroutineContext

actual open class SimplePixivClient
actual constructor(
    parentCoroutineContext: CoroutineContext,
    override val config: PixivConfig
) : PixivClient, AbstractPixivClient() {

    actual constructor(
        parentCoroutineContext: CoroutineContext,
        block: PixivConfig.() -> Unit
    ) : this(parentCoroutineContext, PixivConfig().apply(block))

    override val coroutineContext: CoroutineContext by lazy {
        parentCoroutineContext + CoroutineName("PixivHelper")
    }

    override val httpClient: HttpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        ContentEncoding {
            gzip()
            deflate()
            identity()
        }
        expectSuccess = false
        HttpResponseValidator {
            validateResponse { response ->
                val statusCode = response.status.value
                when (statusCode) {
                    in 300..399 -> throw RedirectResponseException(response)
                    in 400..499 -> {
                        println(response.request.headers)
                        // 判断是否为登录状态
                        when {
                            "grant_type" in response.request.headers -> {
                                response.content.read {
                                    throw AuthException(response, it.toByteString().string(response.charset() ?: Charsets.UTF_8))
                                }
                            }
                            "Authorization" in response.request.headers -> {
                                response.content.read {
                                    throw ApiException(response, it.toByteString().string(response.charset() ?: Charsets.UTF_8))
                                }
                            }
                            else -> throw ClientRequestException(response)
                        }
                    }
                    in 500..599 -> throw ServerResponseException(response)
                }

                if (statusCode >= 600) {
                    throw ResponseException(response)
                }
            }
        }
        engine {
            config {
                addInterceptor { chain ->
                    chain.request().let { request ->
                        request.newBuilder().apply {
                            // headers
                            config.headers.forEach(this::header)
                            if (request.url.host != config.auth.url.toHttpUrlOrNull()?.host) {
                                header("Authorization", "Bearer ${authInfo.accessToken}")
                            }
                            // proxy
                            proxy(Tool.getProxyByUrl(config.proxy))
                            // ssl
                            if (config.RubySSLFactory) {
                                sslSocketFactory(RubySSLSocketFactory, RubyX509TrustManager)
                                hostnameVerifier { _, _ -> true }
                            }
                        }.build()
                    }.let {
                        chain.proceed(it)
                    }
                }
                // dns
                dns(DnsOverHttps.Builder().apply {
                    config.dns.toHttpUrlOrNull()?.let { url(it) }
                    client(OkHttpClient())
                    post(true)
                    resolvePrivateAddresses(true)
                    resolvePublicAddresses(true)
                }.build())
            }
        }
    }
}