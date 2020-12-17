package world.cepi.packer

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.PlayerResourcePackStatusEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import java.net.URL
import java.security.MessageDigest
import java.util.*


class ResourcepackLoader {

    private val urlCache: MutableMap<String, ByteArray> = mutableMapOf()
    private val userLoadedCache: MutableList<UUID> = mutableListOf()

    private fun createSha1(url: String): ByteArray? {
        if (urlCache.containsKey(url)) return urlCache[url]
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
        urlCache[url] = digest.digest()
        return urlCache[url]
    }

    val url = "http://api.cepi.world/resourcepack"

    @Subscribe(order = PostOrder.EARLY)
    fun onPlayerConnect(event: ServerConnectedEvent) {
        if (event.player.uniqueId !in userLoadedCache)
            event.player.sendResourcePack(url, createSha1(url))
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
                event.player.sendMessage(Component.text("We highly reccomend accepting the resourcepack!"))
            PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD ->
                event.player.sendMessage(Component.text("The download failed; We reccomed relogging to try again."))
        }
    }
}