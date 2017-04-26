package com.redhat.services.akka;

import java.io.IOException;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class HelloWorldMain {
  private static final String CLUSTER_NAME = "HelloWorldSystem";

  public static void main(String[] args) throws IOException {
    ActorSystem actorSystem = ActorSystem.create(CLUSTER_NAME);
    actorSystem.actorOf(Props.create(HelloWorld.class));
  }
}
