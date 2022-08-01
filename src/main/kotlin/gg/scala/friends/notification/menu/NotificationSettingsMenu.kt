package gg.scala.friends.notification.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.commons.scheme.impl.SinglePageSchemedMenu
import gg.scala.cookie.settings.builder.MultiOptionPlayerSettingsBuilder
import gg.scala.friends.handler.CommunityPlayerHandler
import gg.scala.friends.notification.NotificationCategory
import gg.scala.friends.player.CommunityPlayer
import gg.scala.lemon.player.metadata.Metadata
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 8/1/2022
 */
object NotificationSettingsMenu
{
    val scheme = SinglePageSchemedMenu()
        .title("Notification Settings")
        .configuration {
            this.updateAfterClick = true
        }
        .manuallyClosed {
            // TODO: supply player
            NotificationMenu()
        }
        .pattern(
            "000000000",
            "001020300",
            "000000000",
        )
        .map('0') { _, _ ->
            PaginatedMenu.PLACEHOLDER
        }
        .map('1') { player, _ ->
            val community = CommunityPlayerHandler.find(player)!!

            MultiOptionPlayerSettingsBuilder()
                .materialOf(XMaterial.HOPPER)
                .titleOf("${CC.B_GREEN}General")
                .descriptionOf(
                    "${CC.GRAY}General notifications."
                )
                .fallbackOf("Enabled")
                .orderedValuesOf(
                    "Enabled", "Disabled"
                )
                .providerOverrideOf { _, _ ->
                    Metadata(
                        if (community.notify(NotificationCategory.General))
                            "Enabled" else "Disabled"
                    )
                }
                .valueOverrideOf {
                    community.categories[NotificationCategory.General] = when (it)
                    {
                        "Enabled" -> true
                        "Disabled" -> false
                        else -> false
                    }
                    community.save()
                }
                .asButton()
        }
        .map('1') { player, _ ->
            val community = CommunityPlayerHandler.find(player)!!

            return@map this.toggleButton(
                material = XMaterial.HOPPER,
                community = community,
                type = NotificationCategory.General
            )
        }
        .map('2') { player, _ ->
            val community = CommunityPlayerHandler.find(player)!!

            return@map this.toggleButton(
                material = XMaterial.COMPASS,
                community = community,
                type = NotificationCategory.Sales
            )
        }
        .map('3') { player, _ ->
            val community = CommunityPlayerHandler.find(player)!!

            return@map this.toggleButton(
                material = XMaterial.SADDLE,
                community = community,
                type = NotificationCategory.Gamemodes
            )
        }
        .compose()

    private fun toggleButton(
        material: XMaterial, community: CommunityPlayer,
        type: NotificationCategory
    ): Button
    {
        return MultiOptionPlayerSettingsBuilder()
            .materialOf(material)
            .titleOf("${CC.B_GREEN}${type.name}")
            .descriptionOf(
                "${CC.GRAY}${type.name} notifications."
            )
            .fallbackOf("Enabled")
            .orderedValuesOf(
                "Enabled", "Disabled"
            )
            .providerOverrideOf { _, _ ->
                Metadata(
                    if (community.notify(type))
                        "Enabled" else "Disabled"
                )
            }
            .valueOverrideOf {
                community.categories[type] = when (it)
                {
                    "Enabled" -> true
                    "Disabled" -> false
                    else -> false
                }
                community.save()
            }
            .asButton()
    }
}
