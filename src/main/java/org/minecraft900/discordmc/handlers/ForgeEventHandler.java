package org.minecraft900.discordmc.handlers;

import org.minecraft900.discordmc.DiscordMC;
import org.minecraft900.discordmc.config.ServerConfig;
import org.minecraft900.discordmc.elements.commands.DiscordCommand;
import org.minecraft900.discordmc.elements.commands.MeToDiscordCommand;
import org.minecraft900.discordmc.elements.commands.SayToDiscordCommand;
import org.minecraft900.discordmc.elements.discord.DiscordEventHandler;
import org.minecraft900.discordmc.net.DiscordNet;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;


@Mod.EventBusSubscriber(
	modid = DiscordMC.MODID,
	bus = Mod.EventBusSubscriber.Bus.FORGE,
	value = Dist.DEDICATED_SERVER
)
public class ForgeEventHandler {


	@SubscribeEvent
	public static void handleServerStartingEvent( ServerStartingEvent event ) {

		DiscordEventHandler.setServer( event.getServer() );
	}

	@SubscribeEvent
	public static void handlerRegisterCommandsEvent( RegisterCommandsEvent event ) {

		DiscordCommand.register( event.getDispatcher() );
		MeToDiscordCommand.register( event.getDispatcher() );
		SayToDiscordCommand.register( event.getDispatcher() );
	}

	@SubscribeEvent
	public static void handleServerStartedEvent( ServerStartedEvent event ) {

		if( ServerConfig.getServerStartedMessageEnabled() ) {
			DiscordNet.sendMessage( ServerConfig.getServerStartedMessage() );
		}
	}

	@SubscribeEvent
	public static void handleServerStoppedEvent( ServerStoppedEvent event ) {

		if( event.getServer().isRunning() ) {
			if( ServerConfig.getServerCrashedMessageEnabled() ) {
				DiscordNet.sendMessage( ServerConfig.getServerCrashedMessage() );
			}
		} else {
			if( ServerConfig.getServerStoppedMessageEnabled() ) {
				DiscordNet.sendMessage( ServerConfig.getServerStoppedMessage() );
			}
		}
		DiscordNet.stop();
	}

	@SubscribeEvent
	public static void handlePlayerLoggedInEvent( PlayerEvent.PlayerLoggedInEvent event ) {

		if( ServerConfig.getPlayerJoinedMessageEnabled() ) {
			DiscordNet.sendPlayerJoinMessage( event.getPlayer(), ServerConfig.getPlayerJoinedMessage() );
		}
	}

	@SubscribeEvent
	public static void handlePlayerLoggedOutEvent( PlayerEvent.PlayerLoggedOutEvent event ) {

		if( ServerConfig.getPlayerLeftMessageEnabled() ) {
			DiscordNet.sendPlayerQuitMessage( event.getPlayer(), ServerConfig.getPlayerLeftMessage() );
		}
	}

	@SubscribeEvent
	public static void handleServerChatEvent( ServerChatEvent event ) {

		if( !event.isCanceled() ) {
			DiscordNet.sendChatMessage( event.getPlayer(), event.getMessage() );
		}
	}

	@SubscribeEvent
	public static void handleLivingDeathEvent( LivingDeathEvent event ) {

		LivingEntity entity = event.getEntityLiving();

		if( entity instanceof Player ) {
			if( ServerConfig.getPlayerDiedMessageEnabled() ) {
				DiscordNet.sendPlayerDeathMessage( event, ServerConfig.getPlayerDiedMessage() );
			}
		} else {
			if( entity instanceof TamableAnimal && ( (TamableAnimal)entity ).getOwnerUUID() != null ) {
				if( ServerConfig.getTamedMobDiedMessageEnabled() ) {
					DiscordNet.sendPetDeathMessage( event, ServerConfig.getTamedMobDiedMessage() );
				}
			}
		}
	}

	@SubscribeEvent
	public static void handleAdvancementEvent( AdvancementEvent event ) {

		DisplayInfo displayInfo = event.getAdvancement().getDisplay();

		if( displayInfo != null && displayInfo.shouldAnnounceChat() &&
			ServerConfig.getPlayerGotAdvancementMessageEnabled() ) {
			DiscordNet.sendAdvancementMessage(
				event.getPlayer(),
				String.format(
					"%s **%s**",
					ServerConfig.getPlayerGotAdvancementMessage(),
					displayInfo.getTitle().getString()),
				String.format("%s", displayInfo.getDescription().getString())
			);
		}
	}
}
