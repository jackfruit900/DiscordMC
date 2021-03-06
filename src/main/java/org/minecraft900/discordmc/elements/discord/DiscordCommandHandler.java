package org.minecraft900.discordmc.elements.discord;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import org.minecraft900.discordmc.config.CommandConfig;
import org.minecraft900.discordmc.config.ServerConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.Objects;


//package-private
class DiscordCommandHandler {
	
	
	private static final String MOD_NAME = ModLoadingContext.get().getActiveContainer().getModInfo().getDisplayName();
	
	//package-private
	static boolean handleCommand( String command, MinecraftServer server ) {
		
		DiscordCommandSource discordCommandSource = new DiscordCommandSource();
		CommandSourceStack source = buildSource( server, discordCommandSource );
		for( AbstractCommentedConfig abstractCommentedConfig : ServerConfig.getCommands() ) {
			String discordCommand = buildDiscordCommand( abstractCommentedConfig );
			if( CommandConfig.getEnabled( abstractCommentedConfig ) && (
				discordCommand.equals( command ) ||
					CommandConfig.getUseParameter( abstractCommentedConfig ) &&
						command.startsWith( discordCommand + " " ) ) ) {
				server.getCommands().performCommand(
					source,
					buildMinecraftCommand( abstractCommentedConfig, discordCommand, command )
				);
				discordCommandSource.sendMessage();
				return true;
			}
		}
		return false;
	}
	
	private static CommandSourceStack buildSource(
		MinecraftServer server,
		DiscordCommandSource discordCommandSource ) {
		
		return new CommandSourceStack(
			discordCommandSource,
			Vec3.ZERO,
			Vec2.ZERO,
			Objects.requireNonNull( server.overworld() ),
			4,
			MOD_NAME,
			new TextComponent( MOD_NAME ),
			server,
			null
		);
	}
	
	private static String buildDiscordCommand( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return ServerConfig.getCommandPrefix() + CommandConfig.getDiscordCommand( abstractCommentedConfig );
	}
	
	private static String buildMinecraftCommand(
		AbstractCommentedConfig abstractCommentedConfig,
		String discordCommand,
		String command ) {
		
		if( CommandConfig.getUseParameter( abstractCommentedConfig ) ) {
			return "/" + CommandConfig.getMinecraftCommand( abstractCommentedConfig ) + command.replaceFirst(
				discordCommand,
				""
			);
		}
		return "/" + CommandConfig.getMinecraftCommand( abstractCommentedConfig );
	}
}
