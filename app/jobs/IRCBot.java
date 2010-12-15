package jobs;

import org.jibble.pircbot.PircBot;

import play.Logger;
import play.Play;
import play.Play.Mode;

import com.lunatech.events.StaticHolder;

public class IRCBot {

	private static PircBot getBot() {
		return (PircBot) StaticHolder.refs.get("irc-bot");
	}
	
	public static void send(String message) throws Exception{
		PircBot bot = getBot();
		if(bot == null){
			Logger.info("Auto-init IRC bot for message");
			start();
		}
		bot = getBot();
		if(bot != null)
			bot.sendMessage(Play.configuration.getProperty("irc.channel", "#test"), message);
		else
			Logger.error("Failed to init IRC bot for message");
	}

	public static void start() throws Exception {
		PircBot bot = getBot();
		if(bot != null){
			Logger.info("disconnecting first");
			bot.disconnect();
			Thread.sleep(1000);
			Logger.info("disconnecting done");
		}else{
			bot = new PircBot(){
				{
					setName(Play.configuration.getProperty("irc.nick", "EventBot"));
				}
			};
			StaticHolder.refs.put("irc-bot", bot);
			if(Play.mode == Mode.DEV)
				bot.setVerbose(true);
		}
		bot.setAutoNickChange(true);
		bot.setEncoding("UTF-8");
		bot.connect(Play.configuration.getProperty("irc.server", "irc.lunatech.com"));
		bot.joinChannel(Play.configuration.getProperty("irc.channel", "#test"));
	}

}
