package jobs;

import com.lunatech.events.StaticHolder;
import models.Event;
import models.Participant;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import play.Logger;
import rules.IRCBotFacade;
import rules.RegistrationEvent;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;

/**
 * Instantiates a rules engine, loads 'registration.drl' and its globals. 
 */
public class RulesProcessor extends Thread {
	private static final String MY_REF = "rules-processor";
	private final StatefulKnowledgeSession statefulKnowledgeSession;

	/**
	 * Sets up the rules engine. Private since this is a singleton.
	 * @throws InstantiationException if there's a failure trying to setup the rules engine
	 */
	private RulesProcessor() throws InstantiationException {
		Properties properties = new Properties();
		properties.put("drools.dialect.java.compiler", "JANINO");
		final KnowledgeBuilderConfiguration knowledgeBuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(properties, ClassLoader.getSystemClassLoader());
		final KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(knowledgeBuilderConfiguration);
		
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("registration.drl", getClass()), ResourceType.DRL);

		if (knowledgeBuilder.hasErrors()) {
			throw new InstantiationException(String.format("Failed to initialize rules engine [%s]", knowledgeBuilder.getErrors().toString()));
		}

		final KnowledgeBaseConfiguration knowledgeBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		knowledgeBaseConfiguration.setOption(EventProcessingOption.STREAM);
		final KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(knowledgeBaseConfiguration);
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
		this.statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		this.statefulKnowledgeSession.setGlobal("IRCBOT", new IRCBotFacade());
	}

	@Override
	public void run(){
		statefulKnowledgeSession.fireUntilHalt();
	}
	
	/**
	 * We are doing the cleanup in a method defined outside this classloader's class base: in 
	 * Thread.interrupt, so that we don't have to deal with class mismatch when Play reloads this class
	 * in a new classloader when we want to cleanup the previous classes' thread.
	 */
	@Override
	public void interrupt(){
		statefulKnowledgeSession.halt();
		statefulKnowledgeSession.dispose();
		super.interrupt();
	}
	
	/**
	 * Initializes the singleton
	 * @throws InstantiationException if there's a failure trying to setup the rules engine
	 */
	public static void startRulesProcessor() throws InstantiationException {
		Logger.info("Starting rules engine");

		init();
	}

	/**
	 * Internal initializer that sets up the rules engine
	 * @return the initialized singleton
	 * @throws InstantiationException if there's a failure trying to setup the rules engine
	 */
	private static RulesProcessor init() throws InstantiationException {
		// only access it as parent-classloader type (Thread) see #interrupt for explanation
		Thread rulesProcessor = (Thread) StaticHolder.refs.get(MY_REF);

		if (rulesProcessor != null) {
			Logger.info("Destroying old Rules engine");
			rulesProcessor.interrupt();
		}

		Logger.info("Starting rules processor");
		rulesProcessor = new RulesProcessor();
		rulesProcessor.start();
		StaticHolder.refs.put(MY_REF, rulesProcessor);
		return (RulesProcessor)rulesProcessor;
	}

	/**
	 * Gives access to this singleton, initializing it if required
	 * @return the Singleton instance of this class
	 * @throws InstantiationException if there's a failure trying to setup the rules engine
	 */
	public static RulesProcessor getInstance() throws InstantiationException {
		final RulesProcessor rulesProcessor = (RulesProcessor)StaticHolder.refs.get(MY_REF);
		
		if (rulesProcessor == null)
			return init();
		
		return rulesProcessor;
	}

	/**
	 * Adds an event (rules-lingo) to the working memory
	 * @param participant the {@link models.Participant} that registered
	 * @param event the {@link Event} that the participant registered too
	 */
	public void addEvent(final Participant participant, final Event event) {
		this.statefulKnowledgeSession.insert(new RegistrationEvent(participant, event));
	}

	/**
	 * Fires any events and possibly leads to IRC messages being sent in response to
	 * registrations that may still be in the working memory
	 * @throws Exception
	 */
	public void doNotifications() throws Exception {
		this.statefulKnowledgeSession.fireAllRules();
	}
}
