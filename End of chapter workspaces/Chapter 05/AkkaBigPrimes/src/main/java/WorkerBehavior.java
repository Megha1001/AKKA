import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.math.BigInteger;
import java.util.Random;

public class WorkerBehavior extends AbstractBehavior<String> {

    private WorkerBehavior(ActorContext<String> context) {
        super(context);
    }

    public static Behavior<String> create() {
        return Behaviors.setup(WorkerBehavior::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("start" , () -> {
                    BigInteger bigInteger = new BigInteger(2000, new Random());
                    System.out.println(bigInteger.nextProbablePrime());
                    return this;
                })
                .build();
    }
}
