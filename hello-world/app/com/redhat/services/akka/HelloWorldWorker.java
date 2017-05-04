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
}
