package org.minecraft900.discordmc.handlers;

import org.minecraft900.discordmc.DiscordMC;
import org.minecraft900.discordmc.config.ServerConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;


@Mod.EventBusSubscriber( modid = DiscordMC.MODID,
	bus = Mod.EventBusSubscriber.Bus.MOD,
	value = Dist.DEDICATED_SERVER )
public class ModEventHandler {
	
	
	@SubscribeEvent
	public static void handleModConfigLoadingEvent( ModConfigEvent.Loading event ) {
		
		ServerConfig.handleConfigEvent();
	}
	
	@SubscribeEvent
	public static void handleModConfigReloadingEvent( ModConfigEvent.Reloading event ) {
		
		ServerConfig.handleConfigEvent();
	}
}
