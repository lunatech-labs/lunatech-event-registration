package jobs;

import models.Event;
import models.Participant;
import play.Logger;
import play.jobs.Job;

/**
 * Allows a fact to be inserted into the working memory without
 * holding up the HTTP request
 */
public class RulesNotificationJob extends Job<Void> {
	private final Event event;
	private final Participant participant;

	/**
	 * Instantiates the job
	 * @param participant the {@link models.Participant} that registered
	 * @param event the {@link Event} that the participant registered too
	 */
	public RulesNotificationJob(final Participant participant, final Event event) {
		this.participant = participant;
		this.event = event;
	}

	@Override
	public void doJob() throws Exception {
		Logger.info("got another participant");
		RulesProcessor.getInstance().addEvent(this.participant, this.event);
	}
}
