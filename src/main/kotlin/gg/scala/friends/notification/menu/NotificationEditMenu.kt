package gg.scala.friends.notification.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.cookie.settings.builder.MultiOptionPlayerSettingsBuilder
import gg.scala.friends.notification.Notification
import gg.scala.friends.notification.NotificationCategory
import gg.scala.friends.notification.NotificationStatus
import gg.scala.lemon.player.metadata.Metadata
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.DurationPrompt
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

/**
 * @author GrowlyX
 * @since 7/31/2022
 */
class NotificationEditMenu(
    private val notification: Notification
) : Menu("Editing Notification...")
{
    init
    {
        placeholder = true
        updateAfterClick = true
    }

    override fun size(buttons: Map<Int, Button>) = 45

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf(
            19 to ItemBuilder
                .of(Material.SIGN)
                .name("${CC.YELLOW}Title")
                .addToLore(
                    "${CC.GRAY}Current:",
                    "${CC.WHITE}${notification.title}",
                    "",
                    "${CC.GREEN}Click to edit!"
                )
                .toButton { _, _ ->
                    player.closeInventory()

                    InputPrompt()
                        .withText("${CC.GREEN}Enter a new title.")
                        .acceptInput { _, title ->
                            notification.title = title

                            player.sendMessage("${CC.GREEN}You've set the new title to:")
                            player.sendMessage("${CC.WHITE}${notification.title}")

                            notification.save()

                            openMenu(player)
                        }
                        .start(player)
                },
            22 - 9 to ItemBuilder
                .copyOf(
                    object : TexturedHeadButton(Constants.GLOBE_ICON){}.getButtonItem(player)
                )
                .name("${CC.B_GREEN}Publish")
                .addToLore(
                    "${CC.GRAY}Click to publish!",
                    "",
                    "${CC.B_RED}Note:",
                    "${CC.RED}All users will be able",
                    "${CC.RED}to view this notification",
                    "${CC.RED}in the state it is in right",
                    "${CC.RED}now.",
                    "",
                    "${CC.GREEN}Click to publish!"
                )
                .toButton { _, _ ->
                    if (notification.status == NotificationStatus.Published)
                    {
                        player.sendMessage("${CC.RED}This notification is already published!")
                        return@toButton
                    }

                    notification.distributionTimestamp = Date()
                    notification.status = NotificationStatus.Published

                    notification.save().join()
                    player.sendMessage("${CC.GREEN}Published notification globally.")
                },
            22 to ItemBuilder
                .copyOf(
                    object : TexturedHeadButton(Constants.CAMERA_ICON){}.getButtonItem(player)
                )
                .name("${CC.B_GREEN}Metrics")
                .addToLore(
                    "${CC.GRAY}Click to view notification",
                    "${CC.GRAY}metrics!",
                    "",
                    "${CC.GREEN}Click to view!"
                )
                .toButton { _, _ ->
                    NotificationEditMetricsMenu(notification).openMenu(player)
                },
            22 + 9 to ItemBuilder
                .copyOf(
                    object : TexturedHeadButton(Constants.RUBIKS_CUBE_ICON){}.getButtonItem(player)
                )
                .name("${CC.B_GREEN}Unlist & Draft")
                .addToLore(
                    "${CC.GRAY}Click to unlist &",
                    "${CC.GRAY}mark this as a draft!",
                    "",
                    "${CC.GREEN}Click to draft!"
                )
                .toButton { _, _ ->
                    if (notification.status == NotificationStatus.Draft)
                    {
                        player.sendMessage("${CC.RED}This notification is already a draft!")
                        return@toButton
                    }

                    notification.status = NotificationStatus.Draft
                    notification.save().join()

                    player.sendMessage("${CC.GREEN}Drafted this notification.")
                },
            25 to ItemBuilder
                .of(Material.ARROW)
                .name("${CC.YELLOW}Expiry")
                .addToLore(
                    "${CC.GRAY}Current:",
                    "${CC.WHITE}${
                        TimeUtil.formatIntoFullCalendarString(
                            notification.expiryDate ?: Date()
                        )
                    }",
                    "",
                    "${CC.GREEN}Click to edit!"
                )
                .toButton { _, _ ->
                    player.closeInventory()

                    DurationPrompt {
                        notification.expiryDate = Date(
                            Instant.now().plusMillis(it.get()).toEpochMilli()
                        )

                        player.sendMessage("${CC.GREEN}You've set the new expiration date to:")
                        player.sendMessage(
                            "${CC.WHITE}${
                                TimeUtil.formatIntoFullCalendarString(
                                    notification.expiryDate ?: Date()
                                )
                            }"
                        )

                        notification.save()

                        openMenu(player)
                    }.start(player)
                },
            20 to ItemBuilder
                .of(Material.PAPER)
                .name("${CC.YELLOW}Lines")
                .addToLore(
                    "${CC.GRAY}Current:",
                )
                .apply {
                    addToLore(
                        *notification.content
                            .map { "${CC.WHITE}$it" }
                            .toTypedArray()
                    )
                }
                .addToLore(
                    "",
                    "${CC.GREEN}Left-Click to edit!",
                    "${CC.RED}Right-Click to clear!",
                )
                .toButton { _, type ->
                    if (type!!.isRightClick)
                    {
                        notification.content.clear()
                        notification.save().join()

                        player.sendMessage("${CC.GREEN}Cleared notification lines!")
                        return@toButton
                    }

                    NotificationEditContentMenu(notification).openMenu(player)
                },
            24 to MultiOptionPlayerSettingsBuilder()
                .materialOf(XMaterial.HOPPER)
                .titleOf("${CC.GREEN}Category")
                .descriptionOf(
                    "${CC.GRAY}Set the notification category."
                )
                .fallbackOf("General")
                .orderedValuesOf(
                    "General", "Sales", "Gamemodes"
                )
                .providerOverrideOf { _, _ ->
                    Metadata(notification.notificationCategory.name)
                }
                .valueOverrideOf {
                    notification.notificationCategory =
                        NotificationCategory.valueOf(it)
                    notification.save().join()
                }
                .asButton()
        )
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.delayed(1L)
            {
                NotificationServiceMenu().openMenu(player)
            }
        }
    }
}
