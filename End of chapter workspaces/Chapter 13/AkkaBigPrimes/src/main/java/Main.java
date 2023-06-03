import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;

import java.math.BigInteger;
import java.time.Duration;
import java.util.SortedSet;
import java.util.concurrent.CompletionStage;

public class Main {
//    public static void main(String[] args) {
//       ActorSystem<String> actorSystem = ActorSystem.create(FirstSimpleBehavior.create(),"FirstActorSystem");
//       actorSystem.tell("say hello");
//       actorSystem.tell("who are you");
//       actorSystem.tell("create a child");
//       actorSystem.tell("This is the second message.");
//    }

    public static void main(String[] args) {
        ActorSystem<ManagerBehavior.Command> bigPrimes = ActorSystem.create(ManagerBehavior.create(), "BigPrimes");
        CompletionStage<SortedSet<BigInteger>> result = AskPattern.ask(bigPrimes,
                (me) -> new ManagerBehavior.InstructionCommand("start", me),
                Duration.ofSeconds(60),
                bigPrimes.scheduler());

        result.whenComplete(
                (reply, failure) -> {
                    if (reply != null) {
                        reply.forEach(System.out::println);
                    }
                    else {
                        System.out.println("The system didn't respond in time");
                    }
                    bigPrimes.terminate();
                }

        );
    }
}
