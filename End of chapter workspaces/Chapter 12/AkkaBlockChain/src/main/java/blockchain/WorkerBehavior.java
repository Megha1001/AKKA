package blockchain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.Behaviors;
import model.Block;
import model.HashResult;
import utils.BlockChainUtils;

public class WorkerBehavior extends AbstractBehavior<WorkerBehavior.Command> {
	
	public static class Command {
		private Block block;
		private int startNonce;
		private int difficulty;
		private ActorRef<HashResult> controller;

		public Command(Block block, int startNonce, int difficulty, ActorRef<HashResult> controller) {
			this.block = block;
			this.startNonce = startNonce;
			this.difficulty = difficulty;
			this.controller = controller;
		}
		
		public Block getBlock() {
			return block;
		}
		public int getStartNonce() {
			return startNonce;
		}
		public int getDifficulty() {
			return difficulty;
		}
		public ActorRef<HashResult> getController() { return controller; }
	}

	private WorkerBehavior(ActorContext<Command> context) {
		super(context);
	}
	
	public static Behavior<Command> create() {
		return Behaviors.setup(WorkerBehavior::new);
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
				.onAnyMessage(message -> {
					String hash = new String(new char[message.getDifficulty()]).replace("\0", "X");
					String target = new String(new char[message.getDifficulty()]).replace("\0", "0");

					int nonce = message.getStartNonce();
					while (!hash.substring(0,message.getDifficulty()).equals(target) && nonce < message.getStartNonce() + 1000) {
						nonce++;
						String dataToEncode = message.getBlock().getPreviousHash() + Long.toString(message.getBlock().getTransaction().getTimestamp())
								+ Integer.toString(nonce) + message.getBlock().getTransaction();
						hash = BlockChainUtils.calculateHash(dataToEncode);
					}

					if (hash.substring(0,message.getDifficulty()).equals(target)) {
						HashResult hashResult = new HashResult();
						hashResult.foundAHash(hash, nonce);
						getContext().getLog().debug(hashResult.getNonce() + " : " + hashResult.getHash());
						message.getController().tell(hashResult);
						return Behaviors.same();
					}
					else {
						getContext().getLog().debug("null");
						return Behaviors.same();
					}
				})
				.build();
	}

}
