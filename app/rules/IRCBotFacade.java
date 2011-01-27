package rules;

import irc.IRCBot;
import play.Logger;

/**
 * Allows access to the IRCBot singleton so that
 * IRC messages can be sent from the rules engine
 */
public class IRCBotFacade {
	/**
	 * Uses the IRCBot singleton to send a message
	 * to the IRC channel
	 * @param message the actual message to be sent
	 */
	public void send(final String message) {
		try {
			IRCBot.send(message);
		} catch (Exception e) {
			Logger.error(String.format("Failed to send message [%s] to IRC", message), e);
		}
	}
}
