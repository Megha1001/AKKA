package blockchain;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import blockchain.ManagerBehavior;
import model.Block;
import model.BlockChain;
import model.BlockValidationException;
import model.HashResult;
import utils.BlocksData;

public class BlockChainMiner {
	
	int difficultyLevel = 5;
	BlockChain blocks = new BlockChain();
	long start = System.currentTimeMillis();
	ActorSystem<ManagerBehavior.Command> actorSystem;

	private void mineNextBlock() {
		int nextBlockId = blocks.getSize();
		if (nextBlockId < 10) {
							String lastHash = nextBlockId > 0 ? blocks.getLastHash() : "0";
			Block block = BlocksData.getNextBlock(nextBlockId, lastHash);
			CompletionStage<HashResult> results = AskPattern.ask(actorSystem,
					me -> new ManagerBehavior.MineBlockCommand(block, me, 5),
					Duration.ofSeconds(30),
					actorSystem.scheduler());
				
			results.whenComplete( (reply,failure) -> {
				
				if (reply == null || !reply.isComplete()) {
					System.out.println("ERROR: No valid hash was found for a block");
				}
				
				block.setHash(reply.getHash());
				block.setNonce(reply.getNonce());
				
				try {
					blocks.addBlock(block);
					System.out.println("Block added with hash : " + block.getHash());
					System.out.println("Block added with nonce: " + block.getNonce());
					mineNextBlock();
				} catch (BlockValidationException e) {
					System.out.println("ERROR: No valid hash was found for a block");
				}
			});
			
		}
		else {
			Long end = System.currentTimeMillis();
			actorSystem.terminate();
			blocks.printAndValidate();
			System.out.println("Time taken " + (end - start) + " ms.");
		}
	}

	public void mineAnIndependentBlock() {
		Block block = BlocksData.getNextBlock( 7, "123456");

		CompletionStage<HashResult> results = AskPattern.ask(actorSystem,
				me -> new ManagerBehavior.MineBlockCommand(block, me, 5),
				Duration.ofSeconds(30),
				actorSystem.scheduler());

		results.whenComplete( (reply, failure) ->  {
			if (reply == null) {
				System.out.println("ERROR: No hash found");
			}
			else {
				System.out.println("Everything is ok");
			}
		});
	}
	
	public void mineBlocks() {
		
		actorSystem = ActorSystem.create(MiningSystemBehavior.create(), "BlockChainMiner");
		mineNextBlock();
		mineAnIndependentBlock();
	}
	
}
