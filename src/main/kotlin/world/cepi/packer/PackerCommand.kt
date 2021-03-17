package world.cepi.packer

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.command.BrigadierCommand

import net.kyori.adventure.text.format.NamedTextColor

import com.velocitypowered.api.command.CommandSource

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext

import com.velocitypowered.api.command.CommandMeta
import net.kyori.adventure.text.Component


object PackerCommand {

    fun register(server: ProxyServer) {
        val packerNode = LiteralArgumentBuilder.literal<CommandSource>("hash")
            .executes { context: CommandContext<CommandSource> ->
                val message: Component = Component.text("Hash: ${ResourcepackLoader.createSha1(ResourcepackLoader.url)}")
                context.source.sendMessage(message)
                1 // indicates success
            }


        val command = BrigadierCommand(packerNode)

        val meta: CommandMeta = server.commandManager.metaBuilder("packer").build()

        server.commandManager.register(meta, command)
    }

}