package gg.scala.community.notification

import gg.scala.community.handler.CommunityPlayerHandler
import gg.scala.community.player.CommunityPlayer
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.entity.Player
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

    fun informOfUnread(player: Player): CompletableFuture<Void>
    {
        val community = CommunityPlayerHandler
            .find(player)
            ?: return CompletableFuture
                .completedFuture(null)

        return findUnreadNotifications(community, filterViewed = true)
            .exceptionally {
                it.printStackTrace()
                return@exceptionally null
            }
            .thenAccept {
                if (it.isNotEmpty())
                {
                    val fancy = FancyMessage()
                        .withMessage(
                            "${CC.B_PRI}Server ${CC.GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${CC.SEC}You have ${CC.PRI}${it.size}${CC.SEC} unread notification${
                                if (it.size == 1) "" else "s"
                            }! "
                        )
                        .withMessage(
                            "${CC.D_AQUA}(Click to read)"
                        )
                        .andHoverOf("${CC.GREEN}View unread notifications...")
                        .andCommandOf(
                            ClickEvent.Action.RUN_COMMAND,
                            "/notifications"
                        )

                    fancy.sendToPlayer(player)
                }
            }
    }

    fun findUnreadNotifications(
        player: CommunityPlayer,
        filterViewed: Boolean = false,
        filterRead: Boolean = true,
    ): CompletableFuture<List<Notification>>
    {
        return DataStoreObjectControllerCache
            .findNotNull<Notification>()
            .loadAll(DataStoreStorageType.REDIS)
            .thenApply { mappings ->
//                val current = Date()

                mappings.values
                    .filter {
                        player.notify(it.notificationCategory)
                    }
                    .filter {
                        it.status == NotificationStatus.Published
                    }
                    .filter {
                        (!filterRead || !player.notificationsRead.contains(it.identifier)) &&
                                (!filterViewed || !player.notificationsViewed.contains(it.identifier))
                    }
            }
    }
}
