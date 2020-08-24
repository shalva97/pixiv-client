package xyz.cssxsh.pixiv.client.data.papi

import com.soywiz.klock.*
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElement
import xyz.cssxsh.pixiv.client.*
import xyz.cssxsh.pixiv.client.data.DateTimeSerializer
import xyz.cssxsh.pixiv.client.data.FileUrls
import xyz.cssxsh.pixiv.client.data.Metadata

@Serializable
data class ArtWork(
    @SerialName("age_limit")
    val ageLimit: String,
    @SerialName("book_style")
    val bookStyle: String,
    @SerialName("caption")
    val caption: String?,
    @SerialName("content_type")
    val contentType: JsonElement,
    @SerialName("created_time")
    @Serializable(DateTimeSerializer::class)
    val createdTime: DateTimeTz,
    @SerialName("favorite_id")
    val favoriteId: Long?,
    @SerialName("height")
    val height: Int,
    @SerialName("id")
    val pid: Long,
    @SerialName("image_urls")
    val imageUrls: FileUrls,
    @SerialName("is_liked")
    val isLiked: Boolean?,
    @SerialName("is_manga")
    val isManga: Boolean?,
    @SerialName("metadata")
    val metadata: Metadata?,
    @SerialName("page_count")
    val pageCount: Int,
    @SerialName("publicity")
    val publicity: PublicityType,
    @SerialName("reuploaded_time")
    @Serializable(DateTimeSerializer::class)
    val reuploadedTime: DateTimeTz,
    @SerialName("sanity_level")
    val sanityLevel: String,
    @SerialName("stats")
    val stats: ArtStats?,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("title")
    val title: String,
    @SerialName("tools")
    val tools: List<String>?,
    @SerialName("type")
    val type: String,
    @SerialName("user")
    val auther: ArtAuther,
    @SerialName("width")
    val width: Int
) {
    @Serializable
    data class ArtStats(
        @SerialName("commented_count")
        val commentedCount: Long?,
        @SerialName("favorited_count")
        val favoritedCount: Map<String, Long?>,
        @SerialName("score")
        val score: Long,
        @SerialName("scored_count")
        val scoredCount: Long,
        @SerialName("views_count")
        val viewsCount: Long
    )

    @Serializable
    data class ArtAuther(
        @SerialName("account")
        val account: String,
        @SerialName("id")
        val uid: Int,
        @SerialName("is_follower")
        val isFollower: Boolean?,
        @SerialName("is_following")
        val isFollowing: Boolean?,
        @SerialName("is_friend")
        val isFriend: Boolean?,
        @SerialName("is_premium")
        val isPremium: Boolean?,
        @SerialName("name")
        val name: String,
        @SerialName("profile")
        val profile: JsonElement,
        @SerialName("profile_image_urls")
        val profileImageUrls: FileUrls,
        @SerialName("stats")
        val stats: ArtStats?
    )
}