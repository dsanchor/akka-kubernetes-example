package com.redhat.services.akka;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.ReceiveTimeout;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.japi.Procedure;
import scala.concurrent.duration.Duration;

public class RemoteGreeting extends UntypedActor {
	
	private final String path;
	  private ActorRef helloWorld = null;

	  public RemoteGreeting(String path) {
	    this.path = path;
	    sendIdentifyRequest();
	  }

	  private void sendIdentifyRequest() {
	    getContext().actorSelection(path).tell(new Identify(path), getSelf());
	    getContext()
	        .system()
	        .scheduler()
	        .scheduleOnce(Duration.create(3, TimeUnit.SECONDS), getSelf(),
	            ReceiveTimeout.getInstance(), getContext().dispatcher(), getSelf());
	  }

	  @Override
	  public void onReceive(Object message) throws Exception {
	    if (message instanceof ActorIdentity) {
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
	      if (message instanceof String) {
	        // send message to server actor
	        helloWorld.tell(message, getSelf());

	      } else if (message instanceof Terminated) {
	        System.out.println("HelloWorld terminated");
	        sendIdentifyRequest();
	        getContext().unbecome();

	      } else if (message instanceof ReceiveTimeout) {
	        // ignore

	      } else {
	        unhandled(message);
	      }

	    }
	  };
}
