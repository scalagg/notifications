package gg.scala.community.npc

import gg.scala.community.handler.CommunityPlayerHandler
import gg.scala.community.notification.NotificationService
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Schedulers
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit

/**
 * @author Topu
 * @date 3/8/2025
 */
@Service
object NpcTextUpdater : Runnable
{
    @Configure
    fun configure()
    {
        Schedulers.async().runRepeating(
            this, 0L, 10L
        )
    }

    override fun run()
    {
        EntityHandler.getEntitiesByType(NpcEntity::class.java).forEach {
            if (it.condition != "notification")
            {
                return@forEach
            }
            val players = Bukkit
                .getOnlinePlayers()

            if (players.isEmpty())
                return@forEach

            val profile = CommunityPlayerHandler.find(players.first().uniqueId)!!
            val lines = mutableListOf<String>()

            lines.add("${CC.B_PRI}Notifications")
            lines.add("${CC.GRAY}Stay up to date with the latest news!")
            lines.add("")
            if (NotificationService.findUnreadNotifications(profile, filterViewed = true).get().isNotEmpty())
            {
                lines.add(
                    "${CC.SEC}You have ${CC.GREEN}${
                        NotificationService.findUnreadNotifications(
                            profile,
                            filterViewed = true
                        ).get().size
                    } ${CC.B_PRI}NEW ${CC.SEC}notification${
                        if (NotificationService.findUnreadNotifications(
                                profile,
                                filterViewed = true
                            ).get().size == 1
                        ) "" else "s"
                    }"
                )
            } else {
                lines.add("${CC.RED}You have no notifications")
            }
            lines.add("")
            lines.add("${CC.GREEN}Click to browse!")

            it.hologram.updateLines(lines)
        }
    }
}