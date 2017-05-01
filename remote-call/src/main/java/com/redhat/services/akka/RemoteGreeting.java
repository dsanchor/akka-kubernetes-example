package com.redhat.services.akka;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.ReachabilityEvent;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.dispatch.forkjoin.ThreadLocalRandom;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class RemoteGreeting extends UntypedActor {

	final String servicePath;
	final Cancellable greetingTask;
	final Set<Address> nodes = new HashSet<Address>();

	Cluster cluster = Cluster.get(getContext().system());

	private static String NAME = "David";

	private static String ROLE = "hello-world";

	public RemoteGreeting(String servicePath) {
		this.servicePath = servicePath;
		FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
		greetingTask = getContext().system().scheduler().schedule(interval, interval, self(), NAME,
				getContext().dispatcher(), null);
	}

	// subscribe to cluster changes, MemberEvent
	@Override
	public void preStart() {
		cluster.subscribe(self(),  ClusterEvent.initialStateAsEvents(), MemberEvent.class, ReachabilityEvent.class);
	}

	// re-subscribe when restart
	@Override
	public void postStop() {
		cluster.unsubscribe(self());
		greetingTask.cancel();
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof String) {
			if (NAME.equals(message)) {
				if (nodes != null && nodes.size() > 0) {
					System.out.println("Sending message:" + message);

					// just pick any one
					List<Address> nodesList = new ArrayList<>(nodes);
					Address address = nodesList.get(ThreadLocalRandom.current().nextInt(nodesList.size()));
					ActorSelection service = getContext().actorSelection(address + servicePath);
					service.tell(message, self());
				} else {
					System.out.println("No remote servers available!!!");
				}

			} else {
				System.out.println("Response:" + message);
			}
		} else if (message instanceof CurrentClusterState) {
			nodes.clear();
			for (Member member : ((CurrentClusterState) message).getMembers()) {
				if (member.hasRole(ROLE) && member.status().equals(MemberStatus.up())) {
					nodes.add(member.address());
				}
			}
		} else if (message instanceof MemberUp) {
			if (((MemberUp) message).member().hasRole(ROLE))
				nodes.add(((MemberUp) message).member().address());
		} else if (message instanceof MemberEvent) {
			nodes.remove(((MemberEvent) message).member().address());
		} else if (message instanceof UnreachableMember) {
			nodes.remove(((UnreachableMember) message).member().address());
		} else if (message instanceof ReachableMember) {
			if (((ReachableMember) message).member().hasRole(ROLE))
				nodes.add(((ReachableMember) message).member().address());
		} else {
			unhandled(message);

		}

	}

	// LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	//
	// private final String path;
	// private ActorRef helloWorld = null;
	//
	// Cluster cluster = Cluster.get(getContext().system());
	//
	// public RemoteGreeting(String path) {
	// log.info("Constructing remote greeting actor");
	// this.path = path;
	// sendIdentifyRequest();
	// }
	//
	// // subscribe to cluster changes
	// @Override
	// public void preStart() {
	// // #subscribe
	// log.info("Subscribing remote greeting actor to cluster events");
	// cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
	// MemberEvent.class, UnreachableMember.class);
	// // #subscribe
	// }
	//
	// // re-subscribe when restart
	// @Override
	// public void postStop() {
	// log.info("Unsubscribing remote greeting actor to cluster events");
	// cluster.unsubscribe(getSelf());
	// }
	//
	// private void sendIdentifyRequest() {
	// getContext().actorSelection(path).tell(new Identify(path), getSelf());
	// getContext().system().scheduler().scheduleOnce(Duration.create(3,
	// TimeUnit.SECONDS), getSelf(),
	// ReceiveTimeout.getInstance(), getContext().dispatcher(), getSelf());
	// }
	//
	// @Override
	// public void onReceive(Object message) throws Exception {
	// if (message instanceof MemberUp) {
	// MemberUp mUp = (MemberUp) message;
	// log.info("Member is Up: {}", mUp.member());
	// getContext().actorSelection(path).tell(mUp.member().address().hostPort(),
	// getSelf());
	//// helloWorld.tell(mUp.member().address().hostPort(), getSelf());
	//
	// } else if (message instanceof ActorIdentity) {
	// helloWorld = ((ActorIdentity) message).getRef();
	// if (helloWorld == null) {
	// System.out.println("Remote actor not available: " + path);
	// } else {
	// getContext().watch(helloWorld);
	// getContext().become(active, true);
	// }
	//
	// } else if (message instanceof ReceiveTimeout) {
	// sendIdentifyRequest();
	//
	// } else {
	// System.out.println("Not ready yet");
	//
	// }
	// }
	//
	// Procedure<Object> active = new Procedure<Object>() {
	// @Override
	// public void apply(Object message) {
	// if (message instanceof Terminated) {
	// System.out.println("HelloWorld terminated");
	// sendIdentifyRequest();
	// getContext().unbecome();
	//
	// } else if (message instanceof ReceiveTimeout) {
	// // ignore
	// } else if (message instanceof String) {
	// log.info("Message received: {}", (String) message);
	//
	// } else {
	// unhandled(message);
	// }
	//
	// }
	// };
}
