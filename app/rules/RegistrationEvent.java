package rules;

import models.Event;
import models.Participant;

/**
 * Containts information about the registration event
 * ie. the {@link Participant} and {@link Event}
 */
public class RegistrationEvent {
	final private String lastName;
	final private String firstName;
	final private String emailAddress;
	final private String company;
	final private int participants;
	final private String title;

	/**
	 * @param participant the {@link models.Participant} that registered
	 * @param event the {@link Event} that the participant registered too
	 */
	public RegistrationEvent(final Participant participant, final Event event) {
		this.lastName = participant.lastName;
		this.firstName = participant.firstName;
		this.emailAddress = participant.emailAddress;
		this.company = participant.company;
		this.participants = event.participants.size();
		this.title = event.title;
	}

	/**
	 * Returns the full name of the participant in the form of
	 * "<first name> <surname>"
	 * @return a String with the participant's name
	 */
	public String getParticipantName() {
		return String.format("%s %s", this.firstName,
			this.lastName);
	}

	/**
	 * Returns information about the participant in the form of
	 * "<first name> <surname> (<e-mail address>) from <company>" as String
	 * @return a String with information about the participant
	 */
	public String getParticipantInfo() {
		return String.format("%s (%s) from %s", getParticipantName(), this.emailAddress,
			this.company);
	}

	/**
	 * Returns information about the event in the form of
	 * "<event title>, which now has <number> participant(s)"
	 * @return a String with information about the event
	 */
	public String getEventInfo() {
		return String.format("%s, which now has %s participant(s)",
			this.title, this.participants);
	}

	/**
	 * Return information about this registration event in the form of
	 * "<{@link #getParticipantInfo}>, has registered for <{@link #getEventInfo}>"
	 * @return
	 */
	public String getRegistrationInfo() {
		return String.format("%s, has registered for %s", getParticipantInfo(), getEventInfo());
	}
}
