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

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

public class IRCBotHandlers extends ListenerAdapter {
	// This handler is called upon receiving any message in a channel
	public void onMessage(MessageEvent event) throws Exception {
		// If the message is in upper case and not from ourselves, spawn a new thread to handle the shout
		if(isUpperCase(event.getMessage()) && event.getMessage().replaceAll("^\\s+", "").replaceAll("\\s+$", "").length() > 5 && event.getUser() != event.getBot().getUserBot()) {
			new Thread(new ShoutHandler(event, true)).start();
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
	
	// This handler is called when a private message has been sent to the bot
	public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
		// There's no reason for anyone to privately message the bot - remind them that they are messaging a bot!
		event.respond("Hi! If you don't know already, I'm just a bot and can't respond to your questions/comments. :( You might want to talk to my creator, got_milk, instead!");
	}
	
	// This handler is called when a user has been kicked from the channel
	public void onKick(KickEvent event) throws Exception {
		// In the case an op goes mad and kicks the bot, rejoin immediately unless got_milk kicks the bot
		if(event.getRecipient() == event.getBot().getUserBot() && !event.getSource().getNick().equals("got_milk")) {
			event.getBot().joinChannel(event.getChannel().getName());
		}
	}
	
	public void onPart(PartEvent event) {
		new Thread(new SeenHandler(event)).start();
	}
	
	public void onQuit(QuitEvent event) {
		new Thread(new SeenHandler(event)).start();
	}
	
	public void onNickChange(NickChangeEvent event) {
		new Thread(new VotekickHandler(event)).start();
	}
	
	public void onJoin(JoinEvent event) {
		new Thread(new KingHandler(event)).start();
	}
	
	// Method to check if a string is uppercase
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
	
	// Method to check for any commands that may be received from a user
	private boolean checkForCommands(MessageEvent event) {
		// If the message contains !who at the start, spawn a new thread to handle the request
		if(event.getMessage().startsWith("!who ")) {
			new Thread(new ShoutHandler(event)).start();
			return true;
		}
		// ..or !decide
		if(event.getMessage().startsWith("!decide ")) {
			new Thread(new FortuneHandler(event)).start();
			return true;
		}
		// ..or !votekick
		if(event.getMessage().startsWith("!votekick ")) {
			new Thread(new VotekickHandler(event)).start();
			return true;
		}
		// ..or !seen
		if(event.getMessage().startsWith("!seen ")) {
			new Thread(new SeenHandler(event)).start();
			return true;
		}
		// ..or !lottery
		if(event.getMessage().startsWith("!lottery ")) {
			new Thread(new LotteryHandler(event)).start();
			return true;
		}
		if(event.getMessage().startsWith("!king")) {
			new Thread(new KingHandler(event, true)).start();
			return true;
		}
		if(event.getMessage().startsWith("!kick ") || event.getMessage().substring(0, 3).equals(".k ")) {
			new Thread(new KickBanHandler(event, false)).start();
			return true;
		}
		if(event.getMessage().startsWith("!ban ") || event.getMessage().substring(0, 4).equals(".kb ")) {
			new Thread(new KickBanHandler(event, true)).start();
			return true;
		}
		return false;
	}
}
