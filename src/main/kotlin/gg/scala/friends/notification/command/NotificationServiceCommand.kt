package gg.scala.friends.notification.command

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Description
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.friends.notification.menu.NotificationServiceMenu
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 11/11/2021
 */
@AutoRegister
@CommandAlias("ns|notificationservice")
@CommandPermission("commulink.command.notificationservice")
object NotificationServiceCommand : ScalaCommand()
{
    @HelpCommand
    @Subcommand("help")
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("control-panel")
    @Description("Enter the notification service control panel.")
    fun onControlPanel(player: Player)
    {
        NotificationServiceMenu().openMenu(player)
    }
}
