import akka.actor.typed.ActorSystem;

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
        bigPrimes.tell( new ManagerBehavior.InstructionCommand("start"));
    }
}
