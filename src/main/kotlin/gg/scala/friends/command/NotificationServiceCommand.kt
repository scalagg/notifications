package gg.scala.friends.command

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand

/**
 * @author GrowlyX
 * @since 11/11/2021
 */
@AutoRegister
@CommandAlias("ns|notificationservice")
@CommandPermission("commulink.command.notificationservice")
class NotificationServiceCommand : ScalaCommand()
{
    @HelpCommand
    @Subcommand("help")
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }
}
