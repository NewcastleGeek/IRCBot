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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import us.rddt.IRCBot.Enums.LogLevels;

/*
 * @author Ryan Morrison
 */
public class IRCBot extends ListenerAdapter<PircBotX> {
	/*
	 * The main entry point of the application.
	 * @param args arguments passed through the command line
	 */
	public static void main(String[] args) throws Exception {
		Properties property = new Properties();
		try {
			property.load(new FileInputStream("IRCBot.properties"));
		} catch (IOException ex) {
			IRCUtils.Log(LogLevels.FATAL, "Could not load properties file");
			System.exit(-1);
		}
		IRCUtils.Log(LogLevels.INFORMATION, "Initialzing bot (IRCBot version " + IRCBot.class.getPackage().getImplementationVersion() + ")");
		// Create a new instance of the IRC bot
		PircBotX bot = new PircBotX();
		// Add new listeners for the actions we want the bot to handle
		bot.getListenerManager().addListener(new IRCBotHandlers());
		// Set the bot's nick
		bot.setName(property.getProperty("nick", "BOT"));
		// Set the bot's user
		bot.setLogin(property.getProperty("user", "BOT"));
		// Connect to the IRC server
		connect(property, bot);
	}
	
	/*
	 * Connects to the IRC server.
	 * @param p the Properties object to read configuration from
	 * @param bot the IRC bot
	 */
	private static void connect(Properties p, PircBotX bot) {
		// Attempt to connect to the server and join the required channel(s)
		IRCUtils.Log(LogLevels.INFORMATION, "Connecting to " + p.getProperty("server") + " and joining channel " + p.getProperty("channel"));
		try {
			bot.connect(p.getProperty("server"), Integer.parseInt(p.getProperty("port")), p.getProperty("password"));
			joinChannels(p, bot);
		} catch (Exception ex) {
			IRCUtils.Log(LogLevels.FATAL, ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	/*
	 * Joins channels as defined in the bot's configuration.
	 * @param p the Properties object to read configuration from
	 * @param bot the IRC bot
	 */
	private static void joinChannels(Properties p, PircBotX bot) {
		String[] channels = p.getProperty("channel").split(",");
		for (int i = 0; i < channels.length; i++) {
			bot.joinChannel(channels[i]);
		}
	}
}