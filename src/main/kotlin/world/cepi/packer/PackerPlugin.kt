package world.cepi.packer

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger

@Plugin(
    id = "packer",
    name = "Packer Plugin",
    version = "0.1.0",
    url = "https://cepi.world",
    description = "Handles resourcepacks from an external URL",
    authors = ["Cepi"],
    dependencies = [Dependency(id = "votlin")]
)
class PackerPlugin @Inject constructor(private val server: ProxyServer, private val logger: Logger) {
    @Subscribe
    fun onProxyInitialization(@SuppressWarnings event: ProxyInitializeEvent) {
        server.eventManager.register(this, ResourcepackLoader)
        PackerCommand.register(server)
        logger.info("[Packer] has been enabled!")
    }
}