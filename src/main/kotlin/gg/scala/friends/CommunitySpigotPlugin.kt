package gg.scala.friends

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.friends.notification.Notification
import gg.scala.store.controller.DataStoreObjectControllerCache
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency

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
    private val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("community")
            .codec(AwareMessageCodec)
            .logger(logger)
            .build()
    }

    @ContainerEnable
    fun containerEnable()
    {
        DataStoreObjectControllerCache
            .create<Notification>()

        this.aware.connect()
    }

    @ContainerDisable
    fun containerDisable()
    {
        this.aware.shutdown()
    }
}
