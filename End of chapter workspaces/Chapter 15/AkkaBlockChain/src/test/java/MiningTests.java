import akka.actor.testkit.typed.CapturedLogEvent;
import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import akka.actor.testkit.typed.javadsl.TestInbox;
import blockchain.ManagerBehavior;
import blockchain.WorkerBehavior;
import model.Block;
import model.HashResult;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
import utils.BlocksData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MiningTests {

    @Test
    void testMiningFailsIfNonceNotInRange() {
        BehaviorTestKit<WorkerBehavior.Command> testActor = BehaviorTestKit.create(WorkerBehavior.create());
        Block block = BlocksData.getNextBlock(0, "0");

        TestInbox<ManagerBehavior.Command> testInbox = TestInbox.create();

        WorkerBehavior.Command message = new WorkerBehavior.Command(block, 0, 5, testInbox.getRef());
        testActor.run(message);
        List<CapturedLogEvent> logMessages = testActor.getAllLogEntries();
        assertEquals(logMessages.size(), 1);
        assertEquals(logMessages.get(0).message() , "null" );
        assertEquals(logMessages.get(0).level() , Level.DEBUG );
    }

    @Test
    void testMiningPassesIfNonceIsInRange() {
        BehaviorTestKit<WorkerBehavior.Command> testActor = BehaviorTestKit.create(WorkerBehavior.create());
        Block block = BlocksData.getNextBlock(0, "0");

        TestInbox<ManagerBehavior.Command> testInbox = TestInbox.create();

        WorkerBehavior.Command message = new WorkerBehavior.Command(block, 82700, 5, testInbox.getRef());
        testActor.run(message);
        List<CapturedLogEvent> logMessages = testActor.getAllLogEntries();
        assertEquals(logMessages.size(), 1);
        String expectedResult = "82741 : 0000081e9d118bf0827bed8f4a3e142a99a42ef29c8c3d3e24ae2592456c440b";
        assertEquals(logMessages.get(0).message() , expectedResult );
        assertEquals(logMessages.get(0).level() , Level.DEBUG );
    }

    @Test
    void testMessageReceivedIfNonceInRange() {
        BehaviorTestKit<WorkerBehavior.Command> testActor = BehaviorTestKit.create(WorkerBehavior.create());
        Block block = BlocksData.getNextBlock(0, "0");

        TestInbox<ManagerBehavior.Command> testInbox = TestInbox.create();

        WorkerBehavior.Command message = new WorkerBehavior.Command(block, 82700, 5, testInbox.getRef());
        testActor.run(message);

        HashResult expectedHashResult = new HashResult();
        expectedHashResult.foundAHash("0000081e9d118bf0827bed8f4a3e142a99a42ef29c8c3d3e24ae2592456c440b", 82741);
        ManagerBehavior.Command expectedCommand = new ManagerBehavior.HashResultCommand(expectedHashResult);
        testInbox.expectMessage(expectedCommand);
    }

    @Test
    void testNoMessageReceivedIfNonceNotInRange() {
        BehaviorTestKit<WorkerBehavior.Command> testActor = BehaviorTestKit.create(WorkerBehavior.create());
        Block block = BlocksData.getNextBlock(0, "0");

        TestInbox<ManagerBehavior.Command> testInbox = TestInbox.create();

        WorkerBehavior.Command message = new WorkerBehavior.Command(block, 0, 5, testInbox.getRef());
        testActor.run(message);

        assertFalse(testInbox.hasMessages());
    }
}
