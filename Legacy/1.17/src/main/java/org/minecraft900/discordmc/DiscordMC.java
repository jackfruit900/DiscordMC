package org.minecraft900.discordmc;

import org.minecraft900.discordmc.config.ServerConfig;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;


@SuppressWarnings( "UtilityClassWithPublicConstructor" )
@Mod( DiscordMC.MODID )
public class DiscordMC {
	
	
	public static final String MODID = "discordmc";
	
	public DiscordMC() {
		
		ModLoadingContext.get().registerConfig( ModConfig.Type.SERVER, ServerConfig.CONFIG );
		ModLoadingContext.get().registerExtensionPoint(
			IExtensionPoint.DisplayTest.class,
			() -> new IExtensionPoint.DisplayTest(
				() -> FMLNetworkConstants.IGNORESERVERONLY,
				( remote, isServer ) -> true
			)
		);
	}
}
