package gg.scala.community.notification.menu

import gg.scala.community.notification.Notification
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/31/2022
 */
class NotificationEditContentMenu(
    private val notification: Notification
) : TextEditorMenu(notification.content)
{
    override fun getPrePaginatedTitle(player: Player) =
        "Editing Notification Content..."

    override fun onClose(player: Player)
    {

    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        super.onClose(player, manualClose)

        if (manualClose)
        {
            Tasks.delayed(1L)
            {
                NotificationEditMenu(notification).openMenu(player)
            }
        }
    }

    override fun onSave(
        player: Player, list: List<String>
    )
    {
        this.notification.content.clear()
        this.notification.content.addAll(list)

        this.notification.save().thenRun {
            player.sendMessage("${CC.GRAY}Saved your notification changes!")
        }
    }
}
