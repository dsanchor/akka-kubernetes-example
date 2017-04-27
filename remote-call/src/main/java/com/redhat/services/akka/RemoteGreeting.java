package com.redhat.services.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class RemoteGreeting extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final String path;
	private ActorRef helloWorld = null;

	Cluster cluster = Cluster.get(getContext().system());

	public RemoteGreeting(String path) {
		this.path = path;
	}

	// subscribe to cluster changes
	@Override
	public void preStart() {
		// #subscribe
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
		// #subscribe
	}

	// re-subscribe when restart
	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message;
			log.info("Member is Up: {}", mUp.member());
			 getContext().actorSelection(path).tell(mUp.member().address().hostPort(),getSelf());

		} else if (message instanceof MemberEvent) {
			// ignore
		} else if (message instanceof String) {
			log.info((String)message);

		} else {
			unhandled(message);
		}
	}
}
