package utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import model.Block;
import model.Transaction;

public class BlocksData {
	private static final ZonedDateTime date = ZonedDateTime.of(2015,6,22,14,21,0,0, ZoneId.of("Etc/UTC"));
	public static final long[] timeStamps = {
			date.toInstant().toEpochMilli(),
			date.plusMinutes(6).toInstant().toEpochMilli(),
			date.plusMinutes(8).toInstant().toEpochMilli(),
			date.plusMinutes(12).toInstant().toEpochMilli(),
			date.plusMinutes(17).toInstant().toEpochMilli(),
			date.plusMinutes(20).toInstant().toEpochMilli(),
			date.plusMinutes(25).toInstant().toEpochMilli(),
			date.plusMinutes(26).toInstant().toEpochMilli(),
			date.plusMinutes(30).toInstant().toEpochMilli(),
			date.plusMinutes(34).toInstant().toEpochMilli(),
	};
	private static int[] customerIds = {1732,1650,2209,4545,324,1944,6565,1805,1765,7001};
	private static double[] amounts = {103.27,66.54,-21.09,44.65,177.99,189.02,17.00,32.99,60.00,-10.00};

	public static Block getNextBlock(int id, String lastHash) {
		
		Transaction transaction = new Transaction(id, timeStamps[id], customerIds[id], amounts[id]);
		
		Block nextBlock = new Block(transaction, lastHash);
		return nextBlock;
	}
	
	
}
