package org.minecraft900.discordmc.net;

import org.minecraft900.discordmc.config.ServerConfig;
import org.minecraft900.discordmc.elements.discord.DiscordEventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.net.URL;


public class DiscordNet {


	private static final Logger LOGGER = LogManager.getLogger( DiscordNet.class );

	public static final String FEEDBACK_START = "**```";

	public static final String FEEDBACK_END = "```**";

	private static JDA jda;

	private static TextChannel channel;
	private static TextChannel playerActionsChannel;
	private static TextChannel advancementsChannel;
	private static TextChannel petChannel;

	private static final List<GatewayIntent> INTENTS = Collections.singletonList( GatewayIntent.GUILD_MESSAGES );

	public static synchronized void init() {

		stop();
		if( ServerConfig.getActive() ) {
			try {
				jda = JDABuilder.create( ServerConfig.getBotToken(), INTENTS )
					.addEventListeners( new DiscordEventHandler() )
					.setAutoReconnect( true )
					.build();
				jda.awaitReady();
				channel = jda.getTextChannelById( ServerConfig.getChannelId() );
				playerActionsChannel = jda.getTextChannelById( ServerConfig.getPlayerActionsChannelId() );
				advancementsChannel = jda.getTextChannelById( ServerConfig.getAdvancementsChannelId() );
				petChannel = jda.getTextChannelById( ServerConfig.getPetChannelId() );
				if( channel == null ) {
					LOGGER.error( "Discord Text Channel {} not found", ServerConfig.getChannelId() );
				}
			} catch( LoginException | InterruptedException exception ) {
				LOGGER.error( "Login to Discord failed", exception );
			}
		}
	}

	public static synchronized void stop() {

		if( isJdaInitialized() ) {
			jda.shutdown();
			channel = null;
			jda = null;
		}
	}

	private static synchronized boolean isJdaInitialized() {

		return jda != null;
	}

	public static synchronized boolean isInitialized() {

		return isJdaInitialized() && channel != null;
	}

	public static synchronized boolean feedBackAllowed( TextChannel _channel, User author ) {

		return _channel.getIdLong() == ServerConfig.getChannelId() && _channel.getIdLong() == channel.getIdLong() &&
			author.getIdLong() != jda.getSelfUser().getIdLong();
	}

//player info related functions
	public static String getPlayerUUID(String name) {
        String url = "https://api.mojang.com/users/profiles/minecraft/"+name;
        try {
            @SuppressWarnings("deprecation")
            String UUIDJson = IOUtils.toString(new URL(url));
            if(UUIDJson.isEmpty()) return "invalid name";
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            return UUIDObject.get("id").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return "error";
	}

	private static String getPlayerName( Player player ) {

		return player.getDisplayName().getString();
	}

//player related messages
	//death message parsing
	public static void sendPlayerDeathMessage( LivingDeathEvent event, String customMessage ) {

		LivingEntity entity = event.getEntityLiving();
		String name = entity.getDisplayName().getString();
		if( customMessage.isEmpty() ) {
			sendDeathMessage(event.getSource().getLocalizedDeathMessage( entity ).getString().replace( name, name), name);
		} else {
			sendDeathMessage( String.format( "%s %s", entity.getDisplayName().getString(), customMessage ), name );
		}
	}
	//death message sending
	public static synchronized void sendDeathMessage( String msg, String player ) {

			if( isInitialized() ) {
				try {
					EmbedBuilder playerEB = new EmbedBuilder();
					String uuid = getPlayerUUID(player);
					String avatar = "https://crafatar.com/avatars/" + uuid;
					String usrAcct = "https://mcuuid.net/?q=" + uuid;

					playerEB.setAuthor(msg, usrAcct, avatar);
					playerEB.setColor(new Color(0, 0, 0));

					playerActionsChannel.sendMessage(playerEB.build()).queue();
					LOGGER.info( "Player Message Sent: MC -> Discord" );
				} catch( Exception exception ) {
					LOGGER.error( "Message could not be sent", exception );
				}
			}
		}

		//player joining message
		public static synchronized void sendPlayerJoinMessage( Player player, String message ) {

			if( isInitialized() ) {
				try {
					EmbedBuilder playerEB = new EmbedBuilder();
					String msg = getPlayerName(player) + " " + message;
					String uuid = getPlayerUUID(getPlayerName(player));
					String avatar = "https://crafatar.com/avatars/" + uuid;
					String usrAcct = "https://mcuuid.net/?q=" + uuid;

					playerEB.setAuthor(msg, usrAcct, avatar);
					playerEB.setColor(new Color(0, 255, 0));

					playerActionsChannel.sendMessage(playerEB.build()).queue();
					LOGGER.info( "Player Message Sent: MC -> Discord" );
				} catch( Exception exception ) {
					LOGGER.error( "Message could not be sent", exception );
				}
			}
		}
		//player leaving message
		public static synchronized void sendPlayerQuitMessage(Player player, String message ) {

			if( isInitialized() ) {
				try {
					EmbedBuilder playerEB = new EmbedBuilder();
					String msg = getPlayerName(player) + " " + message;
					String uuid = getPlayerUUID(getPlayerName(player));
					String avatar = "https://crafatar.com/avatars/" + uuid;
					String usrAcct = "https://mcuuid.net/?q=" + uuid;

					playerEB.setAuthor(msg, usrAcct, avatar);
					playerEB.setColor(new Color(255, 0, 0));

					playerActionsChannel.sendMessage(playerEB.build()).queue();
					LOGGER.info( "Player Message Sent: MC -> Discord" );
				} catch( Exception exception ) {
					LOGGER.error( "Message could not be sent", exception );
				}
			}
		}

		//player advancement message
		public static synchronized void sendAdvancementMessage( Player player, String message, String description ) {

			if( isInitialized() ) {
				try {
					EmbedBuilder playerEB = new EmbedBuilder();
					String msg = getPlayerName(player) + " " + message;
					String uuid = getPlayerUUID(getPlayerName(player));
					String avatar = "https://crafatar.com/avatars/" + uuid;
					String usrAcct = "https://mcuuid.net/?q=" + uuid;

					playerEB.setAuthor(msg, usrAcct, avatar);
					playerEB.setDescription(description);
					playerEB.setColor(new Color(249, 217, 73));

					advancementsChannel.sendMessage(playerEB.build()).queue();
					LOGGER.info( "Advancement Message Sent: MC -> Discord" );
				} catch( Exception exception ) {
					LOGGER.error( "Message could not be sent", exception );
				}
			}
		}

//other entity related messages
	//parses pet message
	public static void sendPetDeathMessage( LivingDeathEvent event, String customMessage ) {

		LivingEntity entity = event.getEntityLiving();
		String name = entity.getDisplayName().getString();
		if( customMessage.isEmpty() ) {
			sendPetMessage(
				event.getSource()
					.getLocalizedDeathMessage( entity )
					.getString()
					.replace( name, "**" + name + "**" )
			);
		} else {
			sendPetMessage( String.format( "%s %s", entity.getDisplayName().getString(), customMessage ) );
		}
	}
	//sends pet message
	public static synchronized void sendPetMessage( String msg ) {

		if( isInitialized() ) {
			try {
				EmbedBuilder playerEB = new EmbedBuilder();

				playerEB.setAuthor(msg, null, null);
				playerEB.setColor(new Color(0, 0, 0));

				petChannel.sendMessage(playerEB.build()).queue();
				LOGGER.info( "Pet Message Sent: MC -> Discord" );
			} catch( Exception exception ) {
				LOGGER.error( "Message could not be sent", exception );
			}
		}
	}

//other general messages
	public static void sendChatMessage( Player player, String message ) {

		sendChatMessage( getPlayerName( player ), message );
	}
	public static void sendChatMessage( CommandSourceStack source, Component message ) {

		sendCommandChatMessage( source, message.getString() );
	}
	private static void sendChatMessage( String name, String message ) {

		sendMessage( String.format( "**%s** %s", name, message ) );
	}

	public static void sendMeChatMessage( CommandSourceStack source, String action ) {

		sendCommandChatMessage( source, String.format( "*%s*", action ) );
	}

	private static void sendCommandChatMessage( CommandSourceStack source, String message ) {

		sendChatMessage( source.getDisplayName().getString(), message );
	}

	public static void sendFeedbackMessage( String message ) {

		for( int start = 0; start <= message.length(); start += 1990 ) {
			sendMessage( FEEDBACK_START + message.substring( start, Math.min( message.length(), start + 1990 ) ) +
				FEEDBACK_END );
		}
	}

	//NOT MC  - Only sends to Discord (Discord Event Handler contains Discord -> MC)
	public static synchronized void sendMessage( String message ) {

		if( isInitialized() ) {
			try {
				for( int start = 0; start < message.length(); start += 2000 ) {
					channel.sendMessage( message.substring( start, Math.min( message.length(), start + 2000 ) ) )
						.queue();
					LOGGER.info( "Message Sent: MC -> Discord" );
				}
			} catch( Exception exception ) {
				LOGGER.error( "Chat Message could not be sent", exception );
			}
		}
	}
}
