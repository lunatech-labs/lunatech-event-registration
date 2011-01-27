package jobs;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;


/**
 * Starts the rules engine on application start
 */
@OnApplicationStart
public class RulesBootstrapJob extends Job {
	@Override
	public void doJob() throws Exception {
		Logger.info("Starting rules engine");
		RulesProcessor.start();
	}
}
