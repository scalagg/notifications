package gg.scala.friends.notification

import gg.scala.common.Savable
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 7/31/2022
 */
data class Notification(
    private val uniqueId: UUID,
    val title: String,
    val author: UUID,
    val content: List<String>,
    val status: NotificationStatus,
    val creationTimestamp: Date,
    var distributionTimestamp: Date = Date(),
    var expiryDate: Date? = null,
) : Savable, IDataStoreObject
{
    override val identifier: UUID
        get() = uniqueId

    override fun save(): CompletableFuture<Void>
    {
        return DataStoreObjectControllerCache
            .findNotNull<Notification>()
            .save(this, DataStoreStorageType.REDIS)
    }
}
