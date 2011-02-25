package jobs;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;


/**
 * Starts the rules engine on application start
 */
@OnApplicationStart
public class RulesBootstrapJob extends Job {
	@Override
	public void doJob() throws Exception {
		final String rulesProperty = Play.configuration.getProperty(
			"notification.rules", "false");
		final boolean doRules = "true".equals(rulesProperty);

		if (doRules) {
			Logger.info("Starting rules engine");
			RulesProcessor.startRulesProcessor();
		} else
			Logger.info("Not starting rules engine, set 'notification.rules=true' in application.conf to use it");
	}
}
