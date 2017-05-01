package com.redhat.services.akka;

import java.io.IOException;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class RemoteMain {
	private static final String CLUSTER_NAME = "CLUSTER";

	private static final String REMOTE_PATH = "REMOTE_SERVICE_PATH";

	private static final String REMOTE_ADDRESS = "REMOTE_SERVICE_ADDRESS";

	public static void main(String[] args) throws IOException {
		ActorSystem actorSystem = ActorSystem.create(getClusterName());
		actorSystem.actorOf(Props.create(RemoteGreeting.class, getRemoteAddress(), getRemotePath()), "remoteGreeting");
		System.out.println("Started RemoteGreetingSystem");
	}

	private static String getRemoteAddress() {
		return System.getenv(REMOTE_ADDRESS);
	}

	private static String getRemotePath() {
		return System.getenv(REMOTE_PATH);
	}

	private static String getClusterName() {
		return System.getenv(CLUSTER_NAME);
	}
}
