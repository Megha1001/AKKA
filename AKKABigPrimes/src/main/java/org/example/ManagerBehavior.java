package org.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Duration;
import java.util.SortedSet;
import java.util.TreeSet;

public class ManagerBehavior extends AbstractBehavior<ManagerBehavior.Command> { // accept string message from main system and resulttype from worker behavior that's we created Command as interface

    //To have multiple types
    public interface Command extends Serializable{}
    /*
          Manager should be able to receive two different object type --> not at same time
          like worker will send big integer but main enter point system will send String
          1 message from system i.e. start
          2. big integers from worker
          we have two ways -->
              object inheritance
              interfaces --> will try this
           */
    //For system
    public static class IntructionCommand implements  Command{
        public static final long serialVersionUID = 1L;
        private String message;
        private ActorRef<SortedSet<BigInteger>> sender;

        public IntructionCommand(String message,ActorRef<SortedSet<BigInteger>> sender ) {
            this.message = message;
            this.sender = sender;
        }

        public ActorRef<SortedSet<BigInteger>> getSender() {
            return sender;
        }

        public String getMessage() { // for making immutable
            return message;
        }
    }

    //for worker
    public static class ResultCommand implements  Command{
        public static final long serialVersionUID = 1L;
        private BigInteger prime;

        public ResultCommand(BigInteger prime) {
            this.prime = prime;
        }

        public BigInteger getPrime() {
            return prime;
        }
    }

    private  class NoResponseReceivedCommand implements  Command{
        public static final long serialVersionUID = 1L;
        private ActorRef<WorkerBehavior.Command> worker;

        public NoResponseReceivedCommand(ActorRef<WorkerBehavior.Command> worker) {
            this.worker = worker;
        }

        public ActorRef<WorkerBehavior.Command> getWorker() {
            return worker;
        }
    }
    public ManagerBehavior(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create(){
        return Behaviors.setup(ManagerBehavior::new);
    }

    private SortedSet<BigInteger> primes = new TreeSet<BigInteger>();
    private ActorRef<SortedSet<BigInteger>> sender;
    @Override
    public Receive<Command> createReceive() {
        /*
        When we use Command as return type it could be start message from system i.e. InstructionCommand
        or could be ResultCommand from worker
         */
        return newReceiveBuilder()
                .onMessage(IntructionCommand.class,command->{
                    if(command.getMessage().equals("start")){
                        this.sender = command.getSender();
                        for(int i=0; i<20; i++){
                            ActorRef<WorkerBehavior.Command> worker=getContext().spawn(WorkerBehavior.create(),"workerBehavior_"+i);
//                            worker.tell(new WorkerBehavior.Command("start", getContext().getSelf())); // message to worker , reference to manager
//                            worker.tell(new WorkerBehavior.Command("start", getContext().getSelf()));
                            askWorkerForAPrime(worker);
                        }
                    }
                    return this;
                })
                .onMessage(ResultCommand.class, command->{
                    primes.add(command.getPrime());
                    System.out.println("I have received "+primes.size()+" prime numbers");
//                    if(primes.size() == 20){
//                        /*
//                        when we call worker twice it will print the number twice
//                        since when it is going to become 20 in count at that time too
//                        two messages will be send and for first 20th message worker
//                        will calculate the number and return it back and then it will print the numbers
//                        (all 20) on second message(duplicate 20) it will just return one calculated previously
//                        but still print all the prime number since count is still 20.
//                         */
//                        primes.forEach(System.out::println);
//                    }
                    //send the list of primes to the main method
                    if(primes.size()==20){
                        this.sender.tell(primes);
                    }
                    return Behaviors.same();
                })
//                .onMessageEquals("start",()->{
//                    for(int i=0; i<10; i++){
//                       ActorRef<WorkerBehavior.Command> actorRef=getContext().spawn(WorkerBehavior.create(),"workerBehavior_"+i);
//                       actorRef.tell(new WorkerBehavior.Command("start", getContext().getSelf())); // message to worker , reference to manager
//                    }
//                    return this;
//                })
                .onMessage(NoResponseReceivedCommand.class,command->{
                    System.out.println("Retrying with worker :"+command.getWorker().path());
                    askWorkerForAPrime(command.getWorker());
                    return Behaviors.same();

                })
                .build();
    }

    // Ask pattern
    private void askWorkerForAPrime(ActorRef<WorkerBehavior.Command> worker){
        //to send message using ask method instead of tell.
        getContext().ask(Command.class,worker, Duration.ofSeconds(5),
//                (me)->new WorkerBehavior.Command("start", getContext().getSelf()), // we can also use 'me'
                (me)->new WorkerBehavior.Command("start", me),
                (response, throwable)->{// // what we are going to do if we get a response or if we dont get a response.
                    /*
                    we are not going to be process message, we process message in the messagehandler(up)
                    Here we say yes, that fine there's a message send it to message handler or we are going to say that is wont a message

                    if we get a response which is either an instance of command or null
                     */

                    if(response != null){// got the response in valid time from worker
                            return response;
                    }else{ // we have something hasn't gone right
                        // return something out of here implement command
                        System.out.println("Worker "+worker.path()+" failed to response.");
                        return new NoResponseReceivedCommand(worker);
                    }
                });

    }
}
