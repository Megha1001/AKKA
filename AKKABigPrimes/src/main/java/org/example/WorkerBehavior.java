package org.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

public class WorkerBehavior extends AbstractBehavior<WorkerBehavior.Command> {


    /*
    This must be serializable, but if we are working on same machine it doesnt matter but if we move
    to cluster then it will create problem.
     */
    public static class Command implements Serializable {
        private String message;
        private ActorRef<ManagerBehavior.Command> sender;//manager is expecting String
        private static final long serialVersionUID = 1;

        //Messages should be immutable --> create getter not setters

        public Command(String message, ActorRef<ManagerBehavior.Command> sender) {
            this.message = message;
            this.sender = sender;
        }

        public String getMessage() {
            return message;
        }

        public ActorRef<ManagerBehavior.Command> getSender() {
            return sender;
        }
    }
/*
Since Worker will going to create instance of Command class hence, we need to make Command class as static
 */

    private WorkerBehavior(ActorContext<Command> context) {// Worker going to receive Command message
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(WorkerBehavior::new);
    }


    @Override
    public Receive<Command> createReceive() {
        return handleMessageWhenWeDontYetHaveAPrimeNumber();
    }

    public Receive<Command> handleMessageWhenWeDontYetHaveAPrimeNumber() {
        return newReceiveBuilder()
                .onAnyMessage(command -> {
//                    if(command.message.equals("start")){
//                        if(prime == null) {// first time it will calculate next time it will send back the same
                    BigInteger bigInteger = new BigInteger(2000, new Random());
                    BigInteger prime = bigInteger.nextProbablePrime();
//                        }
                    Random r = new Random();
                    if (r.nextInt(5) < 2) {
                        command.getSender().tell(new ManagerBehavior.ResultCommand(prime));
                    }
//                        System.out.println(bigInteger.nextProbablePrime());
//                    }
                    return handleMessagesWhenWeAlreadyHaveAPrimeNumber(prime); // call this method when we receive next message
                })
//                .onMessageEquals("start",()->{
//                    BigInteger bigInteger = new BigInteger(2000, new Random());
//                    System.out.println(bigInteger.nextProbablePrime());
//                    System.out.println(getContext().getSelf().path());
//                    return this;
//                })
                .build();
    }

    public Receive<Command> handleMessagesWhenWeAlreadyHaveAPrimeNumber(BigInteger prime) {
        return newReceiveBuilder()
                .onAnyMessage(command -> {
                    Random r = new Random();
                    if (r.nextInt(5) < 2) {
                        command.getSender().tell(new ManagerBehavior.ResultCommand(prime));
                    }
                    command.getSender().tell(new ManagerBehavior.ResultCommand(prime));
                    return this;
                })
                .build();
    }


}
