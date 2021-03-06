package jobs;

import irc.IRCBot;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class IRCBootstrapJob extends Job<Void> {
	
	@Override
	public void doJob() throws Exception {
		Logger.info("Starting IRC bot");
		IRCBot.start();
	}
}
