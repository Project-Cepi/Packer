package world.cepi.packer

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.player.PlayerResourcePackStatusEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import world.cepi.packer.PackerPlugin.Companion.server
import java.net.URL
import java.security.MessageDigest

object EventListener {

    private const val url = "https://github.com/EmortalMC/Resourcepack/releases/download/latest/pack.zip"

    private val resourcePackPrompt = Component.text(
        "Please accept this resource pack.\nWe recommend enabling 'Server Resource Packs' to make this prompt disappear",
        NamedTextColor.YELLOW
    )

    var hash = refreshSha1()
        set(value) {
            resourcePackBuilder = server.createResourcePackBuilder(url)
                .setHash(value)
                .setPrompt(resourcePackPrompt)
                .build()

            field = value
        }

    private var resourcePackBuilder = server.createResourcePackBuilder(url)
        .setHash(hash)
        .setPrompt(resourcePackPrompt)
        .build()

    fun refreshSha1(): ByteArray {
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
    fun onPlayerConnect(event: PlayerChooseInitialServerEvent) {
        event.player.sendResourcePackOffer(resourcePackBuilder)
    }

    @Subscribe
    fun onPlayerResourceStatus(event: PlayerResourcePackStatusEvent) {
        when (event.status) {
            PlayerResourcePackStatusEvent.Status.DECLINED ->
                event.player.disconnect(
                    Component.text(
                        "Using the resource pack is required. It isn't big and only has to be downloaded once.\nIf the dialog is annoying, you can enable 'Server Resource Packs' when adding the server and the prompt will disappear.",
                        NamedTextColor.GRAY
                    )
                )

            PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD ->
                event.player.disconnect(
                    Component.text(
                        "The resource pack download failed.\nIf the issue persists, contact a staff member",
                        NamedTextColor.RED
                    )
                )

            else -> {

            }
        }
    }

}