package gg.scala.friends.notification.menu

import gg.scala.friends.notification.Notification
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.Material
import org.bukkit.entity.Player

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
        placeholdBorders = true
        updateAfterClick = true
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf(
            1 to ItemBuilder
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
            2 to ItemBuilder
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
                }
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
