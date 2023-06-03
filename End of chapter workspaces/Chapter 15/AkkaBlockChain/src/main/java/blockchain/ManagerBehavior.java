package blockchain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;
import model.Block;
import model.HashResult;

import java.io.Serializable;
import java.util.Objects;

public class ManagerBehavior extends AbstractBehavior<ManagerBehavior.Command> {

    private StashBuffer<Command> stashBuffer;

    public interface Command extends Serializable {}

    public static class MineBlockCommand implements Command {
        private static final long serialVersionUID = 1l;
        private Block block;
        private ActorRef<HashResult> sender;
        private int difficulty;

        public MineBlockCommand(Block block, ActorRef<HashResult> sender, int difficulty) {
            this.block = block;
            this.sender = sender;
            this.difficulty = difficulty;
        }

        public Block getBlock() {
            return block;
        }

        public ActorRef<HashResult> getSender() {
            return sender;
        }

        public int getDifficulty() {
            return difficulty;
        }
    }

    public static class HashResultCommand implements Command {
        private static final long serialVersionUID = 1l;
        private HashResult hashResult;

        public HashResultCommand(HashResult hashResult) {
            this.hashResult = hashResult;
        }

        public HashResult getHashResult() {
            return hashResult;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HashResultCommand that = (HashResultCommand) o;
            return Objects.equals(hashResult, that.hashResult);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hashResult);
        }
    }

    private ManagerBehavior(ActorContext<Command> context, StashBuffer<Command> stashBuffer) {
        super(context);
        this.stashBuffer = stashBuffer;
    }

    public static Behavior<Command> create() {
        return Behaviors.withStash(10,
                stash -> {
                    return Behaviors.setup( context -> {
                        return new ManagerBehavior(context, stash);
                    } );
                });
    }

    @Override
    public Receive<Command> createReceive() {
        return idleMessageHandler();
    }

    public Receive<Command> idleMessageHandler() {
        return newReceiveBuilder()
                .onSignal(Terminated.class, handler -> {
                    return Behaviors.same();
                })
                .onMessage(MineBlockCommand.class, message -> {
                    this.sender = message.getSender();
                    this.block = message.getBlock();
                    this.difficulty = message.getDifficulty();
                    this.currentlyMining = true;
                    for (int i = 0; i < 10; i++) {
                        startNextWorker();
                    }
                    return activeMessageHandler();
                })
                .build();
    }

    public Receive<Command> activeMessageHandler() {
        return newReceiveBuilder()
                .onSignal(Terminated.class, handler -> {
                    startNextWorker();
                    return Behaviors.same();
                })
                .onMessage(HashResultCommand.class, message -> {
                    for (ActorRef<Void> child : getContext().getChildren()) {
                        getContext().stop(child);
                    }
                    this.currentlyMining = false;
                    sender.tell(message.getHashResult());
                    return stashBuffer.unstashAll(idleMessageHandler());
                })
                .onMessage(MineBlockCommand.class, message -> {
                    System.out.println("Delaying a mining request");
                    //getContext().getSelf().tell(message);
                    if (!stashBuffer.isFull()) {
                        stashBuffer.stash(message);
                    }
                    return Behaviors.same();
                })
                .build();
    }


    private ActorRef<HashResult> sender;
    private Block block;
    private int difficulty;
    private int currentNonce = 0;
    private boolean currentlyMining;

    private void startNextWorker() {
        if (currentlyMining) {
            //System.out.println("About to start mining with nonces starting at " + currentNonce * 1000);

            Behavior<WorkerBehavior.Command> workerBehavior =
                    Behaviors.supervise(WorkerBehavior.create()).onFailure(SupervisorStrategy.resume());

            ActorRef<WorkerBehavior.Command> worker = getContext().spawn(workerBehavior, "worker" + currentNonce);
            getContext().watch(worker);
            worker.tell(new WorkerBehavior.Command(block, currentNonce * 1000, difficulty, getContext().getSelf()));
            currentNonce++;
        }
    }

}
