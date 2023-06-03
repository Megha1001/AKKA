import akka.actor.testkit.typed.CapturedLogEvent;
import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import akka.actor.testkit.typed.javadsl.TestInbox;
import blockchain.WorkerBehavior;
import model.Block;
import model.HashResult;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
import utils.BlocksData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MiningTest {

    @Test
    void testMiningFails(){
        BehaviorTestKit<WorkerBehavior.Command>testActor = BehaviorTestKit.create(WorkerBehavior.create());
        Block block = BlocksData.getNextBlock(0,"0");

        TestInbox<HashResult> testInbox = TestInbox.create(); // it is an stub of an actor that can receive message of Type HashResult
        WorkerBehavior.Command message = new WorkerBehavior.Command(block,0,5,testInbox.getRef());
        testActor.run(message); // not tell method
        List<CapturedLogEvent> logMessages = testActor.getAllLogEntries();
        assertEquals(logMessages.size(),1);
        assertEquals(logMessages.get(0).message(),"null");
        assertEquals((logMessages.get(0).level()), Level.DEBUG);
    }

    @Test
    void testMiningPassesIfNonceIsInRange(){
        BehaviorTestKit<WorkerBehavior.Command>testActor = BehaviorTestKit.create(WorkerBehavior.create());
        Block block = BlocksData.getNextBlock(0,"0");
        TestInbox<HashResult> testInbox = TestInbox.create();
        WorkerBehavior.Command message = new WorkerBehavior.Command(block,82700,5,testInbox.getRef());
        testActor.run(message); // not tell method
        List<CapturedLogEvent> logMessages = testActor.getAllLogEntries();
        assertEquals(logMessages.size(),1);
//        String expectedResult = "82700 ; ";
//        assertEquals(logMessages.get(0).message(),expectedResult);
        assertEquals((logMessages.get(0).level()), Level.DEBUG);
    }


    @Test
    void testMessageReceivedIfNonceInRange(){
        BehaviorTestKit<WorkerBehavior.Command>testActor = BehaviorTestKit.create(WorkerBehavior.create());
        Block block = BlocksData.getNextBlock(0,"0");
        TestInbox<HashResult> testInbox = TestInbox.create();
        WorkerBehavior.Command message = new WorkerBehavior.Command(block,82700,5,testInbox.getRef());
        testActor.run(message); // not tell method
        HashResult expectHashResult = new HashResult();
        System.out.println(expectHashResult.getHash());
        System.out.println(expectHashResult.getNonce());
        expectHashResult.foundAHash(null, 0);
        testInbox.expectMessage(expectHashResult);
    }

}
