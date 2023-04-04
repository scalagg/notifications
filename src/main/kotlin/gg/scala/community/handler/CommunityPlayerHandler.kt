package gg.scala.community.handler

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.community.player.CommunityPlayer
import gg.scala.flavor.service.Service
import java.util.*

/**
 * @author GrowlyX
 * @since 11/10/2021
 */
@Service
object CommunityPlayerHandler : ProfileOrchestrator<CommunityPlayer>()
{
    override fun new(uniqueId: UUID) = CommunityPlayer(uniqueId)
    override fun type() = CommunityPlayer::class
}
