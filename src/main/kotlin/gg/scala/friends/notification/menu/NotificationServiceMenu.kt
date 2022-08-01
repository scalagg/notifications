package gg.scala.friends.notification.menu

import gg.scala.friends.notification.Notification
import gg.scala.friends.notification.NotificationStatus
import gg.scala.lemon.util.CubedCacheUtil
import gg.scala.lemon.util.QuickAccess
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
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
class NotificationServiceMenu : PaginatedMenu()
{
    init
    {
        placeholdBorders = true
        updateAfterClick = true
    }

    override fun size(buttons: Map<Int, Button>) = 36
    override fun getAllPagesButtonSlots() = (10..16) + (19..25)

    override fun getGlobalButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf(
            4 to ItemBuilder
                .copyOf(
                    object : AddButton()
                    {}
                        .getButtonItem(player)
                )
                .name("${CC.YELLOW}Create notification")
                .addToLore(
                    "${CC.GRAY}Create a new, unnamed",
                    "${CC.GRAY}notification.",
                    "",
                    "${CC.GREEN}Click to create!"
                )
                .toButton { _, _ ->
                    val notification = Notification(
                        uniqueId = UUID.randomUUID(),
                        title = "Unnamed notification",
                        author = player.uniqueId,
                        content = mutableListOf(
                            "Lorem ipsum dolor sit amet",
                            "consectetur adipiscing elit",
                            "sed do eiusmod tempor incididunt",
                            "ut labore et dolore magna aliqua.",
                            "",
                            "Ut enim ad minim veniam, quis ",
                            "nostrud exercitation ullamco ",
                            "laboris nisi ut aliquip ex commodo."
                        ),
                        status = NotificationStatus.Draft,
                        creationTimestamp = Date()
                    )

                    notification.save().join()
                    player.sendMessage("${CC.GREEN}You've created a new notification.")
                }
        )
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        val notifications = DataStoreObjectControllerCache
            .findNotNull<Notification>()
            .loadAll(DataStoreStorageType.REDIS)
            .join()

        for (notification in notifications)
        {
            val colored = QuickAccess
                .computeColoredName(
                    notification.value.author,
                    CubedCacheUtil.fetchName(notification.value.author)!!
                )
                .join()

            buttons[buttons.size] = ItemBuilder
                .of(Material.PAPER)
                .name("${CC.YELLOW}${notification.value.title}")
                .addToLore(
                    "${CC.GRAY}Author: $colored",
                    "",
                    "${CC.GRAY}Private:",
                    "  ${CC.GRAY}Status: ${notification.value.status.chatColor}${notification.value.status.name}",
                    "  ${CC.GRAY}Creation: ${CC.WHITE}${TimeUtil.formatIntoFullCalendarString(notification.value.creationTimestamp)}",
                    ""
                )
                .apply {
                    addToLore(
                        *notification.value.content
                            .map { "${CC.GRAY}$it" }
                            .toTypedArray()
                    )
                }
                .addToLore("", "${CC.GREEN}Click to edit!")
                .toButton { _, _ ->
                    NotificationEditMenu(notification.value).openMenu(player)
                }
        }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) =
        "Notifications (Service Panel)"
}
