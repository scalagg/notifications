package gg.scala.friends.notification

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.friends.player.CommunityPlayer
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 7/31/2022
 */
@Service
object NotificationService
{
    @Configure
    fun configure()
    {
        DataStoreObjectControllerCache
            .create<Notification>()
    }

    fun findUnreadNotifications(
        player: CommunityPlayer,
        filterViewed: Boolean = false
    ): CompletableFuture<List<Notification>>
    {
        return DataStoreObjectControllerCache
            .findNotNull<Notification>()
            .loadAll(DataStoreStorageType.REDIS)
            .thenApply { mappings ->
                mappings.values
//                    .filter {
//                        player.notify(it.notificationCategory)
//                    }
                    .filter { it.status == NotificationStatus.Published }
                    .filter {
                        !player.notificationsRead.contains(it.identifier) &&
                                (!filterViewed || !player.notificationsViewed.contains(it.identifier))
                    }
            }
    }
}
