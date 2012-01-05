/*
 * This file is part of IRCBot.
 * Copyright (c) 2011 Ryan Morrison
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions, and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions, and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of the author of this software nor the name of
 *  contributors to this software may be used to endorse or promote products
 *  derived from this software without specific prior written consent.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */

package us.rddt.IRCBot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;

import us.rddt.IRCBot.Enums.UserModes;
import us.rddt.IRCBot.Handlers.Fortune;
import us.rddt.IRCBot.Handlers.Sandwich;
import us.rddt.IRCBot.Handlers.Seen;
import us.rddt.IRCBot.Handlers.Shouts;
import us.rddt.IRCBot.Handlers.UserMode;

/**
 * @author Ryan Morrison
 */
public class IRCBotHandlers extends ListenerAdapter<PircBotX> {
	/**
	 * Checks incoming messages from users for potential bot commands
	 * @param event the MessageEvent to parse
	 * @return true if a command was parsed, false if no command was recognized
	 */
	private boolean checkForCommands(MessageEvent<PircBotX> event) {
		/*
		 * Most commands below spawn threads to prevent blocking.
		 */
		if(event.getMessage().startsWith("!who ")) {
			new Thread(new Shouts(event)).start();
			return true;
		}
		if(event.getMessage().startsWith("!decide ")) {
			new Thread(new Fortune(event)).start();
			return true;
		}
		if(event.getMessage().startsWith("!seen ")) {
			new Thread(new Seen(event)).start();
			return true;
		}
		if(event.getMessage().startsWith("!sandwich")) {
			new Thread(new Sandwich(event)).start();
			return true;
		}
		if(event.getMessage().equals("!leave")) {
			if(isUserAdmin(event.getUser())) {
				event.getBot().partChannel(event.getChannel());
				return true;
			}
		}
		if(event.getMessage().equals("!disconnect")) {
			if(isUserAdmin(event.getUser())) {
				event.getBot().quitServer("Disconnecting due to administrator request");
				System.exit(0);
			}
		}
		if(event.getMessage().equals("!reload")) {
			if(isUserAdmin(event.getUser())) {
				sendGlobalMessage(event.getBot(), "Reloading configuration...");
				try {
					Configuration.loadConfiguration();
					Configuration.startScheduler(event.getBot());
				} catch (Exception ex) {
					sendGlobalMessage(event.getBot(), "Failed to reload configuration: " + ex.getMessage());
					return true;
				}
				sendGlobalMessage(event.getBot(), "Successfully reloaded configuration.");
				return true;
			}
		}

		/*
		 * User mode change events
		 */
		if(event.getMessage().startsWith("!kick ") || event.getMessage().substring(0, 3).equals(".k ")) {
			new Thread(new UserMode(event, UserModes.KICK)).start();
			return true;
		}
		if(event.getMessage().startsWith("!kickban ") || event.getMessage().substring(0, 4).equals(".kb ")) {
			new Thread(new UserMode(event, UserModes.BAN)).start();
			return true;
		}
		if(event.getMessage().startsWith("!owner ")) {
			new Thread(new UserMode(event, UserModes.OWNER)).start();
			return true;
		}
		if(event.getMessage().startsWith("!deowner ")) {
			new Thread(new UserMode(event, UserModes.DEOWNER)).start();
			return true;
		}
		if(event.getMessage().startsWith("!protect ")) {
			new Thread(new UserMode(event, UserModes.SUPEROP)).start();
			return true;
		}
		if(event.getMessage().startsWith("!deprotect ")) {
			new Thread(new UserMode(event, UserModes.DESUPEROP)).start();
			return true;
		}
		if(event.getMessage().startsWith("!op ")) {
			new Thread(new UserMode(event, UserModes.OP)).start();
			return true;
		}
		if(event.getMessage().startsWith("!deop ")) {
			new Thread(new UserMode(event, UserModes.DEOP)).start();
			return true;
		}
		if(event.getMessage().startsWith("!halfop ")) {
			new Thread(new UserMode(event, UserModes.HALFOP)).start();
			return true;
		}
		if(event.getMessage().startsWith("!dehalfop ")) {
			new Thread(new UserMode(event, UserModes.DEHALFOP)).start();
			return true;
		}
		if(event.getMessage().startsWith("!voice ")) {
			new Thread(new UserMode(event, UserModes.VOICE)).start();
			return true;
		}
		if(event.getMessage().startsWith("!devoice ")) {
			new Thread(new UserMode(event, UserModes.DEVOICE)).start();
			return true;
		}
		return false;
	}

	/**
	 * Checks to see if a string is uppercase
	 * @param s the string to check
	 * @return true if the string is uppercase, false if it is not
	 */
	private boolean isUpperCase(String s) {
		// Boolean value to ensure that an all numeric string does not trigger the shouting functions
		boolean includesLetter = false;
		// Loop through each character in the string individually
		for(int i = 0; i < s.length(); i++) {
			// If there's at least one letter then the string could qualify as being a 'shout'
			if(Character.isLetter(s.charAt(i))) includesLetter = true;
			// Any lower case letters immediately disqualifies the string, return immediately instead of continuing the loop
			if(Character.isLowerCase(s.charAt(i))) return false;
		}
		// If there's at least one letter in the string return true, otherwise disqualify it
		if(includesLetter) return true;
		else return false;
	}

	/**
	 * Handler when a channel invite has been received
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onInvite(org.pircbotx.hooks.events.InviteEvent)
	 * @param event the InviteEvent to parse
	 */
	public void onInvite(InviteEvent<PircBotX> event) {
		if(event.getUser().equals(Configuration.getAdminNick())) event.getBot().joinChannel(event.getChannel());
		return;
	}

	/**
	 * Handler when the bot has been kicked from the channel
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onKick(org.pircbotx.hooks.events.KickEvent)
	 * @param event the KickEvent to parse
	 */
	public void onKick(KickEvent<PircBotX> event) {
		// Nobody should be able to kick the bot from the channel, so rejoin immediately if we are kicked
		event.getBot().joinChannel(event.getChannel().getName());
	}

	/**
	 * Handler when messages are received from the bot
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events.MessageEvent)
	 * @param event the MessageEvent to parse
	 * @throws Exception
	 */
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		// If the message is in upper case and not from ourselves, spawn a new thread to handle the shout
		if(isUpperCase(event.getMessage()) && event.getMessage().replaceAll("^\\s+", "").replaceAll("\\s+$", "").length() > 5 && event.getUser() != event.getBot().getUserBot()) {
			new Thread(new Shouts(event, true)).start();
			return;
		}
		if(checkForCommands(event)) return;
		// Split the message using a space delimiter and attempt to form a URL from each split string
		// If a MalformedURLException is thrown, the string isn't a valid URL and continue on
		// If a URL can be formed from it, spawn a new thread to process it for a title
		String[] splitMessage = event.getMessage().split(" ");
		// We don't want to process more than 2 URLs at a time to prevent abuse and spam
		int urlCount = 0;
		for(int i = 0; i < splitMessage.length; i++) {
			try {
				URL url = new URL(splitMessage[i]);
				new Thread(new URLGrabber(event, url)).start();
				urlCount++;
			} catch (MalformedURLException ex) {
				continue;
			}
			if(urlCount == 2) break;
		}
	}

	/**
	 * Handler when a user has left the channel
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onPart(org.pircbotx.hooks.events.PartEvent)
	 * @param event the PartEvent to parse
	 */
	public void onPart(PartEvent<PircBotX> event) {
		new Thread(new Seen(event)).start();
	}

	/**
	 * Handler when a private message has been sent to the bot
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onPrivateMessage(org.pircbotx.hooks.events.PrivateMessageEvent)
	 * @param event the PrivateMessageEvent to parse
	 */
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event) {
		// There's no reason for anyone to privately message the bot - remind them that they are messaging a bot!
		event.respond("Hi! I am IRCBot version " + IRCBot.class.getPackage().getImplementationVersion() + ". If you don't know already, I'm just a bot and can't respond to your questions/comments. :( You might want to talk to my creator, got_milk, instead!");
	}

	/**
	 * Handler when a user disconnects from the IRC server
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onQuit(org.pircbotx.hooks.events.QuitEvent)
	 * @param event the QuitEvent to parse
	 */
	public void onQuit(QuitEvent<PircBotX> event) {
		new Thread(new Seen(event)).start();
	}
	
	/**
	 * Checks to see if a user is a bot administrator
	 * @param user the user to check
	 * @return true if the user is a bot administrator, false if they are not
	 */
	private boolean isUserAdmin(User user) {
		if(user.getNick().equals(Configuration.getAdminNick()) && user.getHostmask().equals(Configuration.getAdminHostmask())) return true;
		else return false;
	}
	
	/**
	 * Sends a message to each channel the bot is currently in
	 * @param bot the IRC bot
	 * @param message the message to send
	 */
	private void sendGlobalMessage(PircBotX bot, String message) {
		Iterator<Channel> itr = bot.getChannels().iterator();
		while(itr.hasNext()) {
			bot.sendMessage(itr.next(), message);
		}
	}
}