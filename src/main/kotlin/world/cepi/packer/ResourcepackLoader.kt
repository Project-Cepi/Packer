package world.cepi.packer

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.PlayerResourcePackStatusEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.player.ResourcePackInfo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import java.net.URL
import java.security.MessageDigest
import java.util.*

object ResourcepackLoader {

    const val url = "https://github.com/Project-Cepi/Resourcepack/releases/download/latest/pack.zip"

    val prefix = Component.text("[!]", NamedTextColor.RED).append(Component.space())

    private val userLoadedCache: MutableSet<UUID> = mutableSetOf()

    fun createSha1(url: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-1")
        val fileInputStream = URL(url).openStream()
        var n = 0
        val buffer = ByteArray(8192)
        while (n != -1) {
            n = fileInputStream.read(buffer)
            if (n > 0)
                digest.update(buffer, 0, n)
        }
        fileInputStream.close()
        return digest.digest()
    }

    @Subscribe(order = PostOrder.EARLY)
    fun onPlayerConnect(event: ServerPostConnectEvent) {
        if (event.player.uniqueId !in userLoadedCache)
            event.player.sendResourcePackOffer(
                PackerPlugin.server.createResourcePackBuilder(url)
                    .setHash(createSha1(url))
                    .setPrompt(Component.text("Please install the Cepi resource pack!", NamedTextColor.YELLOW))
                    .build()
            )
    }

    @Subscribe
    fun onPlayerDisconnect(event: DisconnectEvent) {
        userLoadedCache.remove(event.player.uniqueId)
    }

    @Subscribe
    fun onPlayerAccept(event: PlayerResourcePackStatusEvent) {
        when (event.status) {
            PlayerResourcePackStatusEvent.Status.SUCCESSFUL ->
                userLoadedCache.add(event.player.uniqueId)
            PlayerResourcePackStatusEvent.Status.DECLINED ->
                event.player.sendMessage(
                    prefix.append(Component.text("We highly reccomend accepting the resourcepack!", NamedTextColor.GRAY))
                        .hoverEvent(HoverEvent.showText(Component.text("Or download directly", NamedTextColor.GRAY)))
                        .clickEvent(ClickEvent.openUrl(url))
                )
            PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD ->
                event.player.sendMessage(
                    prefix.append(Component.text("The download failed; We reccomed relogging to try again.", NamedTextColor.GRAY))
                        .hoverEvent(HoverEvent.showText(Component.text("Or download directly", NamedTextColor.GRAY)))
                        .clickEvent(ClickEvent.openUrl(url))
                )
            else -> {

            }
        }
    }
}