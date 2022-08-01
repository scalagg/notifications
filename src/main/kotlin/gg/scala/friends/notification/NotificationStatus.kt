package gg.scala.friends.notification

import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 7/31/2022
 */
enum class NotificationStatus(
    val chatColor: ChatColor
)
{
    Draft(ChatColor.GOLD),
    Published(ChatColor.GREEN)
}
