package gg.scala.friends.notification.menu

import gg.scala.friends.handler.CommunityPlayerHandler
import gg.scala.friends.notification.Notification
import gg.scala.friends.notification.NotificationService
import gg.scala.lemon.util.CubedCacheUtil
import gg.scala.lemon.util.QuickAccess
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 7/31/2022
 */
class NotificationMenu : PaginatedMenu()
{
    init
    {
        placeholdBorders = true
        updateAfterClick = true
        async = true
    }

    override fun size(buttons: Map<Int, Button>) = 45
    override fun getAllPagesButtonSlots() =
        (10..16) + (19..25) + (28..34)

    private val mappings = mutableMapOf<UUID, String>()
    private val notifications = mutableListOf<Notification>()

    private var initialLoad = false

    override fun asyncLoadResources(
        player: Player, callback: (Boolean) -> Unit
    )
    {
        val community = CommunityPlayerHandler.find(player)
            ?: return callback.invoke(false)

        NotificationService
            .findUnreadNotifications(
                community, filterViewed = false
            )
            .exceptionally {
                it.printStackTrace()
                callback.invoke(false)
                return@exceptionally null
            }
            .thenAccept {
                this.notifications.clear()
                this.notifications.addAll(it)

                this.notifications.forEach { notification ->
                    kotlin.runCatching {
                        this.mappings[notification.author] = QuickAccess
                            .computeColoredName(
                                notification.author,
                                CubedCacheUtil.fetchName(notification.author)!!
                            )
                            .join()
                    }.onFailure { throwable ->
                        throwable.printStackTrace()
                        callback.invoke(false)
                    }
                }

                if (!this.initialLoad)
                {
                    this.initialLoad = true

                    if (this.notifications.isEmpty())
                    {
                        player.sendMessage("${CC.GREEN}You do not have any new notifications. Check back later!")
                    }
                }

                callback.invoke(true)
            }
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf(
            3 to ItemBuilder.of(Material.MAP)
                .name("${CC.B_GREEN}All notifications")
                .addToLore(
                    "${CC.GRAY}All previously viewed",
                    "${CC.GRAY}notifications.",
                    "",
                    "${CC.GREEN}Click to view!"
                )
                .toButton { _, _ ->

                },
            5 to ItemBuilder.of(Material.MAGMA_CREAM)
                .name("${CC.B_GREEN}Settings")
                .addToLore(
                    "${CC.GRAY}Edit your notification settings.",
                    "",
                    "${CC.GREEN}Click to edit!"
                )
                .toButton { _, _ ->
                    NotificationSettingsMenu.scheme.createAndOpen(player)
                }
        )
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        val community = CommunityPlayerHandler
            .find(player) ?: return buttons

        if (this.notifications.isEmpty())
        {
            return buttons
        }

        for (notification in this.notifications)
        {
            val colored = this.mappings[notification.author]!!

            if (!community.notificationsViewed.contains(notification.identifier))
            {
                community.notificationsViewed.add(notification.identifier)
            }

            buttons[buttons.size] = ItemBuilder
                .of(Material.PAPER)
                .name("${CC.YELLOW}${notification.title}")
                .addToLore(
                    "${CC.GRAY}Author: $colored",
                    "${CC.GRAY}Published: ${CC.WHITE}${
                        TimeUtil.formatIntoFullCalendarString(notification.distributionTimestamp)
                    }",
                    ""
                )
                .apply {
                    addToLore(
                        *notification.content
                            .map { "${CC.GRAY}$it" }
                            .toTypedArray()
                    )
                }
                .addToLore("", "${CC.GREEN}Click to mark as read!")
                .toButton { _, _ ->
                    community.notificationsRead
                        .add(notification.identifier)

                    community.save()
                }
        }

        community.save()

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) = "Notifications"
}
