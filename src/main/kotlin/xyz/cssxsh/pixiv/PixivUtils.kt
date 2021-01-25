@file:Suppress("unused")

package xyz.cssxsh.pixiv

import io.ktor.client.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.encoding.Encoder
import xyz.cssxsh.pixiv.client.PixivClient
import kotlin.reflect.KClass

suspend fun <R> PixivClient.useHttpClient(
    ignore: suspend (Throwable) -> Boolean,
    block: suspend PixivClient.(HttpClient) -> R,
): R = httpClient().use { client ->
    runCatching {
        block(client)
    }.getOrElse { throwable ->
        if (ignore(throwable)) {
            useHttpClient(ignore = ignore, block = block)
        } else {
            throw throwable
        }
    }
}

typealias HeadersMap = Map<String, String>

typealias FileUrls = Map<String, String>

interface PixivParam {
    val name: String
    fun value() = name.toLowerCase()
}

open class PixivEnumSerializer<T>(
    with: KClass<T>,
    private val valueOf: (name: String) -> T,
) : KSerializer<T> where T : PixivParam, T : Enum<T> {

    override val descriptor: SerialDescriptor = buildSerialDescriptor(with.qualifiedName!!, SerialKind.ENUM)

    override fun serialize(encoder: Encoder, value: T) =
        encoder.encodeString(value.value())

    override fun deserialize(decoder: Decoder): T =
        valueOf(decoder.decodeString().toUpperCase())
}

open class PixivTypeSerializer<T>(
    with: KClass<T>,
    private val get: (index: Int) -> T,
) : KSerializer<T> where T : PixivParam, T : Enum<T> {

    override val descriptor: SerialDescriptor = buildSerialDescriptor(with.qualifiedName!!, SerialKind.ENUM)

    override fun serialize(encoder: Encoder, value: T) =
        encoder.encodeString(value.value())

    override fun deserialize(decoder: Decoder): T =
        get(decoder.decodeInt())
}

enum class GrantType : PixivParam {
    PASSWORD,
    REFRESH_TOKEN;
}

@Serializable(with = SearchMode.Companion::class)
enum class SearchMode : PixivParam {
    TEXT,
    CAPTION,
    TAG,
    EXACT_TAG;

    companion object : PixivEnumSerializer<SearchMode>(
        with = SearchMode::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = OrderType.Companion::class)
enum class OrderType : PixivParam {
    DESC,
    ASC;

    companion object : PixivEnumSerializer<OrderType>(
        with = OrderType::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = PeriodType.Companion::class)
enum class PeriodType : PixivParam {
    ALL,
    DAY,
    WEEK,
    MONTH;

    @Serializer(forClass = PeriodType::class)
    companion object : PixivEnumSerializer<PeriodType>(
        with = PeriodType::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = SortMode.Companion::class)
enum class SortMode : PixivParam {
    DATE,
    POPULAR;

    companion object : PixivEnumSerializer<SortMode>(
        with = SortMode::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = SortType.Companion::class)
enum class SortType : PixivParam {
    DATE_DESC,
    DATE_ASC;

    companion object : PixivEnumSerializer<SortType>(
        with = SortType::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = DurationType.Companion::class)
enum class DurationType : PixivParam {
    WITHIN_LAST_DAY,
    WITHIN_LAST_WEEK,
    WITHIN_LAST_MONTH;

    companion object : PixivEnumSerializer<DurationType>(
        with = DurationType::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = PublicityType.Companion::class)
enum class PublicityType : PixivParam {
    PUBLIC,
    PRIVATE;

    companion object : PixivEnumSerializer<PublicityType>(
        with = PublicityType::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = SearchTarget.Companion::class)
enum class SearchTarget : PixivParam {
    PARTIAL_MATCH_FOR_TAGS,
    EXACT_MATCH_FOR_TAGS,
    TITLE_AND_CAPTION;

    companion object : PixivEnumSerializer<SearchTarget>(
        with = SearchTarget::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = WorkContentType.Companion::class)
enum class WorkContentType : PixivParam {
    ILLUST,
    UGOIRA,
    MANGA;

    companion object : PixivEnumSerializer<WorkContentType>(
        with = WorkContentType::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = WorkContentType.Companion::class)
enum class RankMode : PixivParam {
    // MONTH
    MONTH,

    // WEEK
    WEEK,
    WEEK_ORIGINAL,
    WEEK_ROOKIE,

    // DAY
    DAY,
    DAY_MALE,
    DAY_FEMALE,
    DAY_MANGA,

    // R18
    DAY_R18,
    DAY_MALE_R18,
    DAY_FEMALE_R18,
    WEEK_R18,
    WEEK_R18G;

    companion object : PixivEnumSerializer<RankMode>(
        with = RankMode::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = SanityLevel.Companion::class)
enum class SanityLevel(val level: Int) : PixivParam {
    UNCHECKED(0),
    WHITE(2),
    SEMI_BLACK(4),
    BLACK(6);

    companion object : PixivEnumSerializer<SanityLevel>(
        with = SanityLevel::class,
        valueOf = ::enumValueOf
    )
}

@Serializable(with = AgeLimit.Companion::class)
enum class AgeLimit : PixivParam {
    ALL {
        override fun value() = "all-age"
    },
    R18 {
        override fun value() = "r18"
    },
    R18G {
        override fun value() = "r18g"
    };

    companion object : PixivEnumSerializer<AgeLimit>(
        with = AgeLimit::class,
        valueOf = ::enumValueOf
    )
}