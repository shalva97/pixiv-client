package xyz.cssxsh.pixiv.client

import com.soywiz.klock.wrapped.WDateTime
import com.soywiz.krypto.md5
import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.pixiv.GrantType
import xyz.cssxsh.pixiv.data.AuthResult

abstract class AbstractPixivClient : PixivClient {

    override var authInfo: AuthResult.AuthInfo? = null

    abstract override val httpClient: HttpClient

    override suspend fun login(mailOrPixivID: String, password: String): AuthResult.AuthInfo = auth(GrantType.PASSWORD, config {
        account = PixivConfig.Account(mailOrPixivID, password)
    })

    override suspend fun refresh(token: String): AuthResult.AuthInfo = auth(GrantType.REFRESH_TOKEN, config {
        refreshToken = token
    })

    override suspend fun auth(grantType: GrantType, config: PixivConfig): AuthResult.AuthInfo =
        httpClient.post<AuthResult>(config.auth.url) {
            WDateTime.now().format("yyyy-MM-dd'T'HH:mm:ssXXX").let {
                header("X-Client-Hash", (it + config.client.hashSecret).encodeToByteArray().md5().hex)
                header("X-Client-Time", it)
            }
            body = FormDataContent(Parameters.build {
                append("get_secure_url", "1")
                append("client_id", config.client.id)
                append("client_secret", config.client.secret)
                append("grant_type", grantType.value())
                when (grantType) {
                    GrantType.PASSWORD -> requireNotNull(config.account) { "账户为空" }.let {
                        append("username", it.mailOrUID)
                        append("password", it.password)
                    }
                    GrantType.REFRESH_TOKEN -> requireNotNull(config.refreshToken) { "Token为空" }.let {
                        append("refresh_token", it)
                    }
                }
            })
        }.also {
            authInfo = it.info
        }.info
}