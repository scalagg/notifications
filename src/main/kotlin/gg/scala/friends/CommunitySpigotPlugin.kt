package gg.scala.friends

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.friends.handler.CommunityPlayerHandler
import gg.scala.friends.notification.NotificationService
import me.lucko.helper.Events
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @author GrowlyX
 * @since 11/11/2021
 */
@Plugin(
    name = "CommunityLink",
    apiVersion = "1.18",
    depends = [
        PluginDependency("scala-commons"),
        PluginDependency("Lemon"),
        PluginDependency("Cookie"),
        PluginDependency("store-spigot"),
        PluginDependency("cloudsync", soft = true)
    ]
)
class CommunitySpigotPlugin : ExtendedScalaPlugin()
{
    @ContainerEnable
    fun containerEnable()
    {
        Events.subscribe(PlayerJoinEvent::class.java)
            .handler { event ->
                val community = CommunityPlayerHandler
                    .find(event.player)!!

                NotificationService
                    .findUnreadNotifications(
                        community, filterViewed = true
                    )
                    .exceptionally {
                        it.printStackTrace()
                        return@exceptionally null
                    }
                    .thenAccept {
                        val fancy = FancyMessage()
                            .withMessage(
                                "${CC.D_GRAY}You have ${CC.BD_GRAY}${it.size}${CC.D_GRAY} unread notifications... "
                            )
                            .withMessage(
                                "${CC.D_GRAY}(Click to view)"
                            )
                            .andHoverOf("${CC.D_GRAY}View unread notifications...")
                            .andCommandOf(
                                ClickEvent.Action.RUN_COMMAND,
                                "/notifications"
                            )

                        fancy.sendToPlayer(event.player)
                    }
            }
    }
}
