package xyz.cssxsh.pixiv.apps

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonElement
import xyz.cssxsh.pixiv.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

suspend fun PixivAppClient.novelBookmarkAdd(
    pid: Long,
    tags: List<String>,
    restrict: PublicityType = PublicityType.PUBLIC,
    url: String = NOVEL_BOOKMARK_ADD,
): JsonElement = useHttpClient { client ->
    client.post(url) {
        body = FormDataContent(Parameters.build {
            append("novel_id", pid.toString())
            append("tags", tags.joinToString(separator = " ", postfix = " "))
            append("restrict", restrict.value())
        })
    }
}

suspend fun PixivAppClient.novelBookmarkDelete(
    pid: Long,
    url: String = NOVEL_BOOKMARK_DELETE,
): JsonElement = useHttpClient { client ->
    client.post(url) {
        body = FormDataContent(Parameters.build {
            append("novel_id", pid.toString())
        })
    }
}

suspend fun PixivAppClient.novelBookmarkDetail(
    pid: Long,
    url: String = NOVEL_BOOKMARK_DETAIL,
): BookmarkDetailSingle = useHttpClient { client ->
    client.get(url) {
        parameter("novel_id", pid)
    }
}


suspend fun PixivAppClient.novelComments(
    pid: Long,
    offset: Long = 0,
    includeTotalComments: Boolean? = null,
    url: String = NOVEL_COMMENTS,
): CommentData = useHttpClient { client ->
    client.get(url) {
        parameter("novel_id", pid.toString())
        parameter("offset", offset)
        parameter("include_total_comments", includeTotalComments)
    }
}

suspend fun PixivAppClient.novelDetail(
    pid: Long,
    url: String = NOVEL_DETAIL,
): IllustSingle = useHttpClient { client ->
    client.get(url) {
        parameter("novel_id", pid.toString())
    }
}

suspend fun PixivAppClient.novelFollow(
    restrict: PublicityType = PublicityType.PUBLIC,
    filter: FilterType = FilterType.FOR_ANDROID,
    offset: Long = 0,
    url: String = NOVEL_FOLLOW,
): NovelData = useHttpClient { client ->
    client.get(url) {
        parameter("restrict", restrict.value())
        parameter("filter", filter.value())
        parameter("offset", offset)
    }
}

suspend fun PixivAppClient.novelMyPixiv(
    restrict: PublicityType = PublicityType.PUBLIC,
    filter: FilterType = FilterType.FOR_ANDROID,
    offset: Long = 0,
    url: String = NOVEL_MYPIXIV,
): NovelData = useHttpClient { client ->
    client.get(url) {
        parameter("restrict", restrict.value())
        parameter("filter", filter.value())
        parameter("offset", offset)
    }
}

suspend fun PixivAppClient.novelNew(
    restrict: PublicityType = PublicityType.PUBLIC,
    filter: FilterType = FilterType.FOR_ANDROID,
    offset: Long = 0,
    url: String = NOVEL_NEW,
): NovelData = useHttpClient { client ->
    client.get(url) {
        parameter("restrict", restrict.value())
        parameter("filter", filter.value())
        parameter("offset", offset)
    }
}

suspend fun PixivAppClient.novelRanking(
    date: String? = null,
    mode: RankMode? = null,
    filter: FilterType = FilterType.FOR_ANDROID,
    offset: Long = 0,
    url: String = NOVEL_RANKING,
): NovelData = useHttpClient { client ->
    client.get(url) {
        parameter("date", date)
        parameter("mode", mode?.value())
        parameter("filter", filter.value())
        parameter("offset", offset)
    }
}

suspend fun PixivAppClient.novelRanking(
    date: LocalDate,
    mode: RankMode? = null,
    filter: FilterType = FilterType.FOR_ANDROID,
    offset: Long = 0,
    url: String = NOVEL_RANKING,
): NovelData = novelRanking(
    date = date.format(DateTimeFormatter.ISO_DATE),
    mode = mode,
    filter = filter,
    offset = offset,
    url = url,
)

suspend fun PixivAppClient.novelRecommended(
    workContentType: WorkContentType = WorkContentType.ILLUST,
    filter: FilterType? = null,
    includeRankingLabel: Boolean = true,
    includePrivacyPolicy: Boolean = true,
    minBookmarkIdForRecentIllust: Long? = null,
    maxBookmarkIdForRecommend: Long? = null,
    url: String = NOVEL_RECOMMENDED,
): RecommendedData = useHttpClient { client ->
    client.get(url) {
        parameter("content_type", workContentType.value())
        parameter("filter", filter?.value())
        parameter("include_ranking_label", includeRankingLabel)
        parameter("include_privacy_policy", includePrivacyPolicy)
        parameter("min_bookmark_id_for_recent_illust", minBookmarkIdForRecentIllust)
        parameter("max_bookmark_id_for_recommend", maxBookmarkIdForRecommend)
    }
}