package gg.scala.friends.notification.menu

import gg.scala.friends.notification.Notification
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
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
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        TODO("Not yet implemented")
    }
}
