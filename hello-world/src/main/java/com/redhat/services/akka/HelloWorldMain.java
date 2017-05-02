package com.redhat.services.akka;

import java.io.IOException;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;

public class HelloWorldMain {
	private static final String CLUSTER_NAME = "CLUSTER";

	public static void main(String[] args) throws IOException {
		ActorSystem actorSystem = ActorSystem.create(getClusterName(), ConfigFactory.load("router.conf"));

//		actorSystem.actorOf(Props.create(HelloWorldService.class), "helloWorldService");
		actorSystem.actorOf(FromConfig.getInstance().props(Props.create(HelloWorldWorker.class)),
				"workerRouter");

	//	actorSystem.actorOf(Props.create(HelloWorldWorker.class), "helloWorldWorker");
	}

	private static String getClusterName() {
		return System.getenv(CLUSTER_NAME);
	}
}
