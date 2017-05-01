package com.redhat.services.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;
import akka.routing.FromConfig;

public class HelloWorldService extends UntypedActor {

	// This router is used both with lookup and deploy of routees. If you
	// have a router with only lookup of routees you can use Props.empty()
	// instead of Props.create(StatsWorker.class).
	ActorRef workerRouter = getContext().actorOf(FromConfig.getInstance().props(Props.empty()), "workerRouter");

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof String) {
			System.out.println("Hello in service");
			workerRouter.tell(new ConsistentHashableEnvelope(message, message), sender());

		} else {
			unhandled(message);

		}

	}

//	@Override
//	public Receive createReceive() {
//		return receiveBuilder().match(StatsJob.class, job -> !job.getText().isEmpty(), job -> {
//			String[] words = job.getText().split(" ");
//			ActorRef replyTo = sender();
//
//			// create actor that collects replies from workers
//			ActorRef aggregator = getContext().actorOf(Props.create(StatsAggregator.class, words.length, replyTo));
//
//			// send each word to a worker
//			for (String word : words) {
//				workerRouter.tell(new ConsistentHashableEnvelope(word, word), aggregator);
//			}
//		}).build();
//	}

}
