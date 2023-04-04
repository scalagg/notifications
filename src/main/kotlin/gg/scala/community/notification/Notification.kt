package gg.scala.community.notification

import gg.scala.common.Savable
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import org.bson.Document
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 7/31/2022
 */
data class Notification(
    private val uniqueId: UUID,
    var title: String,
    val author: UUID,
    val content: MutableList<String>,
    var status: NotificationStatus,
    val creationTimestamp: Date,
    var distributionTimestamp: Date = Date(),
    var expiryDate: Date? = null,
    var notificationCategory: NotificationCategory =
        NotificationCategory.General
) : Savable, IDataStoreObject
{
    override val identifier: UUID
        get() = uniqueId

    fun aggregateMetrics(matcher: String) =
        listOf(
            Document(
                "\$project",
                Document(
                    matcher,
                    Document(
                        "\$in",
                        listOf(identifier.toString(), "\$notifications${matcher.capitalize()}")
                    )
                )
            ),
            Document(
                "\$match",
                Document(matcher, true)
            ),
            Document("\$count", matcher)
        )

    override fun save(): CompletableFuture<Void>
    {
        return DataStoreObjectControllerCache
            .findNotNull<Notification>()
            .save(this, DataStoreStorageType.REDIS)
    }
}
