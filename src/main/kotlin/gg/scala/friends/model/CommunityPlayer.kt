package gg.scala.friends.model

import gg.scala.common.Savable
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.controller.annotations.Timestamp
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 11/11/2021
 */
class CommunityPlayer(
    private val uniqueId: UUID
) : Savable, IDataStoreObject
{
    @JvmField
    @Timestamp
    var timestamp = 0L

    override val identifier: UUID
        get() = uniqueId

    val readNotifications = mutableSetOf<UUID>()

    override fun save(): CompletableFuture<Void>
    {
        return DataStoreObjectControllerCache
            .findNotNull<CommunityPlayer>()
            .save(this, DataStoreStorageType.MONGO)
    }
}
