import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class FirstSimpleBehavior extends AbstractBehavior<String> {

    private FirstSimpleBehavior(ActorContext<String> context) {
        super(context);
    }

    public static Behavior<String> create() {
        return Behaviors.setup(FirstSimpleBehavior::new);
    }

    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    System.out.println("I received the message : " + message);
                    return this;
                })
                .build();
    }
}
