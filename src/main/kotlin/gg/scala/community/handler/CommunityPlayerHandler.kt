package gg.scala.community.handler

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.community.player.CommunityPlayer
import java.util.*

/**
 * @author GrowlyX
 * @since 11/10/2021
 */
@Service
object CommunityPlayerHandler : ProfileOrchestrator<CommunityPlayer>()
{
    @Configure
    fun configure()
    {
        this.subscribe()
    }

    override fun new(uniqueId: UUID) = CommunityPlayer(uniqueId)
    override fun type() = CommunityPlayer::class
}
