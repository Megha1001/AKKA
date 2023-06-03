package org.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/*
To create behavior extends AbstractBehavior and <> accepts type of message it can receive
To compile :-
    -Create constructor
    -Create createReceive :- code is going to run when we receive the message
 */
public class FirstSimpleBehavior extends AbstractBehavior<String> { // In angle bracket we give what kind of message it will receive
    /*
    to call the constructor we need parameter of type ActorContext, that we might not able to get
    to get --> we can use Behaviors.Setup
     */
    private FirstSimpleBehavior(ActorContext<String> context) {
        super(context);
    }
    /*
    type of behavior must be same as message type and will call this method to get an object
    of FirstSimpleBehavior type
     */
    public static Behavior<String> create(){
        //call the constructor and return the result
        //import javasdsl packages and typed also present in imports
//       return Behaviors.setup(context ->{
//            return new FirstSimpleBehavior(context);
//        });
        return Behaviors.setup(FirstSimpleBehavior::new);
    }

    @Override
    public Receive<String> createReceive() {
        // ignore any message
//        return newReceiveBuilder()
//                .build(); // present in AbstractBehavior class

        //for processing
       /* return newReceiveBuilder()
                .onAnyMessage(message->{ // even work null string
                    //code to run , this need to return something for now return this
                    System.out.println("I received the message : "+message);
                    return this;
                })
                .build();*/

        return  newReceiveBuilder()
                .onMessageEquals("say hello",()->{
                    System.out.println("Hello");
                    return this;
                })
                .onMessageEquals("who are you", ()->{
                    System.out.println("My path is "+getContext().getSelf().path()); //To get the path of actor
                    return this;
                })
                .onMessageEquals("create a child",()->{
                    /*create a new actor with firstSimpleBehavior-> for this we use spawn method that exists in ActorContext and we can
                    access to ActorContext by calling a class getContext present in AbstractBehavior
                    spawn method takes two parameter
                        1. Instance of Behavior
                        2. name of actor
                    * */
                    ActorRef<String> secondActor= getContext().spawn(create(),"secondActor"); // this will return ActorRef
                    /*
                    ActorRef is actually an interface and ActorSystem extends it. AR has tell method
                    ActorSystem = ActorRef + somethingElse
                     */
                    secondActor.tell("who are you"); //--> akka://CodeGlance/user/secondActor
                    /*
                    akka:: --> it is datatype or url type that akka going to use
                    FirstActorSystem --> name of first actor --> this is user actor--> its user guardian
                     */
                    return this;

                })
                .onAnyMessage(message->{
                    System.out.println("I received the message : "+message);
                    return  this;
                })
                .build();
    }
}
