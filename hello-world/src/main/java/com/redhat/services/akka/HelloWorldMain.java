package com.redhat.services.akka;

import java.io.IOException;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class HelloWorldMain {
	private static final String CLUSTER_NAME = "CLUSTER";
	private static final String ROLE = "ROLE";

	public static void main(String[] args) throws IOException {
		ActorSystem actorSystem = ActorSystem.create(getClusterName(), ConfigFactory.load("router.conf"));

		actorSystem.actorOf(Props.create(HelloWorldService.class), "helloWorldService");
		actorSystem.actorOf(Props.create(HelloWorldWorker.class), "helloWorldWorker");
	}

	private static String getClusterName() {
		return System.getenv(CLUSTER_NAME);
	}

	private static String getClusterRole() {
		return System.getenv(ROLE);
	}
}
