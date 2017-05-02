package com.redhat.services.akka;

import akka.actor.UntypedActor;

public class HelloWorldWorker extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			String m = "Hello " + message;
			System.out.println(m);
			sender().tell(m, self());

		} else {
			unhandled(message);

		}
	}

	// @Override
	// public Receive createReceive() {
	// return receiveBuilder()
	// .match(String.class, m -> {
	// String message = "Hello " + m.toUpperCase();
	// System.out.println(message);
	// sender().tell(message, self());
	// })
	// .build();
	// }
}
