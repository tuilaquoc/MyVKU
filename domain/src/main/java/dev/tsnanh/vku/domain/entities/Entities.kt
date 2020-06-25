package dev.tsnanh.vku.domain.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

// A generic class that contains data and status about loading this data.
sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

@JsonClass(generateAdapter = true)
@Entity(tableName = "news")
data class News(
    @PrimaryKey
    val url: String,
    val title: String,
    val category: String,
    val date: String
)

@JsonClass(generateAdapter = true)
data class Forum(
    @field:Json(name = "_id")
    val id: String = "",
    val title: String,
    val numberOfThreads: Int,
    val numberOfReplies: Int,
    val latestThread: String?,
    val tag: String
)

@JsonClass(generateAdapter = true)
data class NetworkCustomForum(
    @field:Json(name = "_id")
    val id: String = "",
    val title: String,
    val numberOfThreads: Int,
    val numberOfReplies: Int,
    val latestThread: NetworkForumThread?,
    val tag: String
)

@JsonClass(generateAdapter = true)
data class ForumThread(
    @field:Json(name = "_id")
    val id: String = "",
    val uid: String? = null,
    val title: String,
    val forumId: String = "",
    val numberOfReplies: Int = 1,
    val numberOfViews: Int = 0,
    val likes: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val latestReply: NetworkReply? = null/*,
    val editHistory: List<String> = emptyList()*/
)

@JsonClass(generateAdapter = true)
data class NetworkForumThread(
    @field:Json(name = "_id")
    val id: String = "",
    val uid: String? = null,
    val title: String,
    val forumId: String = "",
    val numberOfReplies: Int = 1,
    val numberOfViews: Int = 1,
    val likes: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val latestReply: String? = null/*,
    val editHistory: List<String> = emptyList()*/
)

@JsonClass(generateAdapter = true)
data class NetworkForumThreadCustom(
    @field:Json(name = "_id")
    val id: String = "",
    val uid: User? = null,
    val title: String,
    val forumId: String = "",
    val numberOfReplies: Int = 1,
    val numberOfViews: Int = 1,
    val likes: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val latestReply: NetworkReply? = null
)

@JsonClass(generateAdapter = true)
data class User(
    @field:Json(name = "_id")
    val id: String = "",
    val uidGG: String,
    val displayName: String,
    val photoURL: String,
    val email: String,
    val role: Int = 0
)

@JsonClass(generateAdapter = true)
data class Reply(
    @field:Json(name = "_id")
    val id: String = "",
    val uid: String = "",
    val content: String,
    var images: List<String> = emptyList(),
    val threadId: String = "",
    val forumId: String = "",
    val editHistory: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val quoted: String = "",
    val quotedReply: Reply? = null
)

@JsonClass(generateAdapter = true)
data class NetworkReply(
    @field:Json(name = "_id")
    val id: String = "",
    val uid: String = "",
    val content: String,
    var images: List<String> = emptyList(),
    val threadId: String = "",
    val forumId: String = "",
    val editHistory: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val quoted: String = "",
    val quotedReply: String? = null
)

@Entity(tableName = "subjects")
@JsonClass(generateAdapter = true)
@Parcelize
data class Subject(
    @field:Json(name = "tenlop")
    @PrimaryKey
    val className: String,
    @field:Json(name = "chucdanh")
    val title: String,
    @field:Json(name = "hoten")
    val fullName: String,
    @field: Json(name = "tuan")
    val week: String,
    @field:Json(name = "thu")
    val dayOfWeek: String,
    @field:Json(name = "tiet")
    val lesson: String,
    @field:Json(name = "phong")
    val room: String
) : Parcelable