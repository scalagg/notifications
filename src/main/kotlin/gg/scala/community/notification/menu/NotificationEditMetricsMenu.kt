package gg.scala.community.notification.menu

import gg.scala.community.notification.Notification
import gg.scala.community.player.CommunityPlayer
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/31/2022
 */
class NotificationEditMetricsMenu(
    private val notification: Notification
) : Menu("Viewing Notification Metrics...")
{
    private val mappings = mutableMapOf<String, Int>()

    init
    {
        autoUpdate = true
        autoUpdateInterval = 10L

        DataStoreObjectControllerCache
            .findNotNull<CommunityPlayer>()
            .loadAll(DataStoreStorageType.MONGO)
            .thenAccept {
                this.mappings["viewed"] = it.values
                    .count { player ->
                        player.notificationsViewed
                            .contains(this.notification.identifier)
                    }

                this.mappings["read"] = it.values
                    .count { player ->
                        player.notificationsRead
                            .contains(this.notification.identifier)
                    }
            }
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf(
            0 to ItemBuilder
                .of(Material.PAPER)
                .name("${CC.GREEN}Viewed")
                .addToLore(
                    "${CC.GRAY}Amount: ${CC.WHITE}${
                        this.mappings["viewed"] ?: "Loading..."
                    }"
                )
                .toButton(),
            1 to ItemBuilder
                .of(Material.FISHING_ROD)
                .name("${CC.GREEN}Read")
                .addToLore(
                    "${CC.GRAY}Amount: ${CC.WHITE}${
                        this.mappings["read"] ?: "Loading..."
                    }"
                )
                .toButton()
        )
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.delayed(1L)
            {
                NotificationEditMenu(notification).openMenu(player)
            }
        }
    }
}
