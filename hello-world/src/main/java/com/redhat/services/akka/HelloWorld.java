package com.redhat.services.akka;

import akka.actor.AbstractActor;

public class HelloWorld extends AbstractActor {
	
	  @Override
	  public Receive createReceive() {
	    return receiveBuilder()
	      .match(String.class, m -> {
	    	  String message = "Hello " + m.toUpperCase();
	          System.out.println(message);
	          sender().tell(message, self());
	      })
	      .build();
	  }
}
