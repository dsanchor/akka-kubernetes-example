package com.redhat.services.akka;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

public class RemoteMain {
  private static final String CLUSTER_NAME = "RemoteGreetingSystem";
  
  private static final String REMOTE_PATH = "remote.service.path";
  
  private static final String[] names = new String[]{"David", "Michael", "Alex", "George", "Jose"};

  public static void main(String[] args) throws IOException {
    ActorSystem actorSystem = ActorSystem.create(CLUSTER_NAME);
    final ActorRef actor = actorSystem.actorOf(
        Props.create(RemoteGreeting.class, getRemotePath()), "remoteGreeting");

        System.out.println("Started RemoteGreetingSystem");
        final Random r = new Random();
        actorSystem.scheduler().schedule(Duration.create(1, TimeUnit.SECONDS),
            Duration.create(1, TimeUnit.SECONDS), new Runnable() {
              @Override
              public void run() {
            	  int index = new Random().nextInt(5);
                  actor.tell(names[index], null);

              }
            }, actorSystem.dispatcher());
  }
  
  private static String getRemotePath() {
	  return System.getenv(REMOTE_PATH);
  }
}
