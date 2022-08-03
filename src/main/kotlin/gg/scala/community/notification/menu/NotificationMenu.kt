package gg.scala.community.notification.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.community.handler.CommunityPlayerHandler
import gg.scala.community.notification.Notification
import gg.scala.community.notification.NotificationService
import gg.scala.lemon.util.CubedCacheUtil
import gg.scala.lemon.util.QuickAccess
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
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

    enum class UIState
    {
        All, Unread
    }

    private var uiState = UIState.Unread

    override fun asyncLoadResources(
        player: Player, callback: (Boolean) -> Unit
    )
    {
        val community = CommunityPlayerHandler.find(player)
            ?: return callback.invoke(false)

        NotificationService
            .findUnreadNotifications(
                community, filterViewed = false,
                filterRead = this.uiState == UIState.Unread
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
            3 to ItemBuilder
                .of(
                    when (this.uiState)
                    {
                        UIState.Unread -> XMaterial.CHEST
                        UIState.All -> XMaterial.SADDLE
                    }
                )
                .name(
                    when (this.uiState)
                    {
                        UIState.All -> "${CC.B_GREEN}Unread notifications"
                        UIState.Unread -> "${CC.B_GREEN}All notifications"
                    }
                )
                .setLore(
                    when (this.uiState)
                    {
                        UIState.Unread -> listOf(
                            "${CC.GRAY}All notifications you",
                            "${CC.GRAY}have already read.",
                        )
                        UIState.All -> listOf(
                            "${CC.GRAY}Notifications you have",
                            "${CC.GRAY}not yet read.",
                        )
                    }
                )
                .addToLore(
                    "",
                    "${CC.GREEN}Click to view!"
                )
                .toButton { _, _ ->
                    this.uiState = when (this.uiState)
                    {
                        UIState.All -> UIState.Unread
                        UIState.Unread -> UIState.All
                    }
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

    override fun getPrePaginatedTitle(player: Player) = "Notifications ${Constants.DOUBLE_ARROW_RIGHT} Viewing ${uiState.name}"
}
