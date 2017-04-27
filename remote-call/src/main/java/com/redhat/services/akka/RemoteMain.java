package com.redhat.services.akka;

import java.io.IOException;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class RemoteMain {
	private static final String CLUSTER_NAME = "RemoteGreetingSystem";

	private static final String REMOTE_PATH = "REMOTE_SERVICE_PATH";

	public static void main(String[] args) throws IOException {
		ActorSystem actorSystem = ActorSystem.create(CLUSTER_NAME);
		actorSystem.actorOf(Props.create(RemoteGreeting.class, getRemotePath()), "remoteGreeting");
		System.out.println("Started RemoteGreetingSystem");
	}

	private static String getRemotePath() {
		return System.getenv(REMOTE_PATH);
	}
}
