package org.example;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;

import java.math.BigInteger;
import java.time.Duration;
import java.util.SortedSet;
import java.util.concurrent.CompletionStage;

public class Main {


//    public static void main(String[] args){
//        //To instantiate the first actor in the system call ActorSystem, pick one from akka.actor.typed
//        //1- parameter is the instance of  Behavior that we want our actor to have
//        //2- parameter -> Name of actor
//        // ActorSystem<String> --> it is generic type that defines the type of messages our actor can receive
//        ActorSystem<String> actorSystem = ActorSystem.create(FirstSimpleBehavior.create(),"FirstActorSystem");
////        ActorSystem<String> actorSystem1 = ActorSystem.create(FirstSimpleBehavior.create(),"FirstActorSystem");
//        /* ActorSystem is the wrapper that will act as an entry point to Akka. Actors can create more actors
//        or child actors but will always communicate through the very first actor we create the one we called the
//        ActorSystem
//        ActorSytem = Actor with few extra features
//        * */
//        actorSystem.tell("say hello");
//        actorSystem.tell("who are you");//--->akka://FirstActorSystem/user
////        actorSystem1.tell("who are you");
//        /*Akka allows creating multiple actor systems with the same name. While it is not a recommended practice, Akka does not enforce uniqueness of actor system names. The behavior may vary depending on the specific version and configuration of Akka being used.*/
//        actorSystem.tell("Hello are you there ?");
//        actorSystem.tell("This is the second message");
//
//        /*
//        When we instantiated actorSystem we actually created an actor with name, path , message queue
//        and set of behavior(FirstBehavior.create())
//         */
//
//        /*
//        One we execute the class the application will keeps on running until we shut it down
//        why --> because our actor can receive multiple messages
//        we can stop it programmatically
//         */
//
//
//        /*
//        ActorName cannot contain spaces since it will be a part of URL
//         */
//
//        actorSystem.tell("create a child");
//    }

    public static void main(String[] args) {
        ActorSystem<ManagerBehavior.Command> bigPrimes= ActorSystem.create(ManagerBehavior.create(),"BigPrimes");
//        bigPrimes.tell("start");

//        bigPrimes.tell(new ManagerBehavior.IntructionCommand("start"));
        CompletionStage<SortedSet<BigInteger>>result = AskPattern.ask(bigPrimes, (me)->new ManagerBehavior.IntructionCommand("start", me),
                Duration.ofSeconds(20),
                bigPrimes.scheduler());

        // execute when manager responded or timeout/time is elapsed(20sec)
        result.whenComplete(
                (reply, failure)->{
                    if(reply != null){
                        // reply is our sorted set
                        reply.forEach(System.out::println);
                    }else{
                        System.out.println("System didn't respond in time.");
                    }
                    bigPrimes.terminate();
                }
        );
        /*
        Manager should able to receive two different object type --> not at same time
        like worker will send big integer but main enter point system will send String
        1 message from system i.e. start
        2. big integers from worker
        we have two ways -->
            object inheritance
            interfaces --> will try this
         */
    }

    /*
    Actor can receive any message that is serializable
     */

}
