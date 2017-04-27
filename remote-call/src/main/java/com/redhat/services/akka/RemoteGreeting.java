package com.redhat.services.akka;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.ReceiveTimeout;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import scala.concurrent.duration.Duration;

public class RemoteGreeting extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final String path;
	private ActorRef helloWorld = null;

	Cluster cluster = Cluster.get(getContext().system());

	public RemoteGreeting(String path) {
		log.info("Constructing remote greeting actor");
		this.path = path;
		sendIdentifyRequest();
	}

	// subscribe to cluster changes
	@Override
	public void preStart() {
		// #subscribe
		log.info("Subscribing remote greeting actor to cluster events");
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
		// #subscribe
	}

	// re-subscribe when restart
	@Override
	public void postStop() {
		log.info("Unsubscribing remote greeting actor to cluster events");
		cluster.unsubscribe(getSelf());
	}

	private void sendIdentifyRequest() {
		getContext().actorSelection(path).tell(new Identify(path), getSelf());
		getContext().system().scheduler().scheduleOnce(Duration.create(3, TimeUnit.SECONDS), getSelf(),
				ReceiveTimeout.getInstance(), getContext().dispatcher(), getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message;
			log.info("Member is Up: {}", mUp.member());
			helloWorld.tell(mUp.member().address().hostPort(), getSelf());

		} else if (message instanceof ActorIdentity) {
			helloWorld = ((ActorIdentity) message).getRef();
			if (helloWorld == null) {
				System.out.println("Remote actor not available: " + path);
			} else {
				getContext().watch(helloWorld);
				getContext().become(active, true);
			}

		} else if (message instanceof ReceiveTimeout) {
			sendIdentifyRequest();

		} else {
			System.out.println("Not ready yet");

		}
	}

	Procedure<Object> active = new Procedure<Object>() {
		@Override
		public void apply(Object message) {
			 if (message instanceof Terminated) {
				System.out.println("HelloWorld terminated");
				sendIdentifyRequest();
				getContext().unbecome();

			} else if (message instanceof ReceiveTimeout) {
				// ignore
			} else if (message instanceof String) {
				log.info("Message received: {}", (String) message);

			} else {
				unhandled(message);
			}

		}
	};
}
