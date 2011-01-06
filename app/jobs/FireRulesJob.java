package jobs;

import play.jobs.Every;
import play.jobs.Job;

/**
 * Instructs the rules processor to do its thing
 */
@Every("30s")
public class FireRulesJob extends Job<Void> {
	@Override
	public void doJob() throws Exception {
		RulesProcessor.getInstance().doNotifications();
	}
}
