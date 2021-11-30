package world.cepi.packer

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor


object PackerCommand {

    fun register(server: ProxyServer) {
        val packerNode = LiteralArgumentBuilder.literal<CommandSource>("refresh")
            .executes { context: CommandContext<CommandSource> ->
                EventListener.hash = EventListener.refreshSha1()

                context.source.sendMessage(Component.text("Refreshed resource pack hash", NamedTextColor.GREEN))
                1 // indicates success
            }


        val command = BrigadierCommand(packerNode)

        val meta = server.commandManager.metaBuilder("packerrefresh").build()

        server.commandManager.register(meta, command)
    }

}