package gg.scala.friends.notification.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.friends.notification.menu.NotificationMenu
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 11/11/2021
 */
@AutoRegister
object NotificationsCommand : ScalaCommand()
{
    @CommandAlias("notifications|notifs|notification")
    fun onNotifications(player: Player)
    {
        NotificationMenu().openMenu(player)
    }
}
