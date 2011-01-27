package jobs;

import irc.IRCBot;
import play.jobs.Job;

public class IRCMessageJob extends Job<Void>{
	
	private String message;

	public IRCMessageJob(String message){
		this.message = message;
	}
	
	@Override
	public void doJob() throws Exception {
		IRCBot.send(message);
	}
}
