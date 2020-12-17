package world.cepi.packer

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import java.net.URL
import java.security.MessageDigest


class ResourcepackLoader {

    private val urlCache: MutableMap<String, ByteArray> = mutableMapOf()

    private fun createSha1(url: String): ByteArray? {
        if (urlCache.containsKey(url)) return urlCache[url]
        val digest = MessageDigest.getInstance("SHA-1")
        val fileInputStream = URL(url).openStream()
        var n = 0
        val buffer = ByteArray(8192)
        while (n != -1) {
            n = fileInputStream.read(buffer)
            if (n > 0) {
                digest.update(buffer, 0, n)
            }
        }
        fileInputStream.close()
        urlCache[url] = digest.digest()
        return urlCache[url]
    }

    val url = "http://api.cepi.world/resourcepack"

    @Subscribe(order = PostOrder.EARLY)
    fun onPlayerChat(event: ServerConnectedEvent) {
        event.player.sendResourcePack(url, createSha1(url))
    }
}