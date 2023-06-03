package utils;

import java.util.GregorianCalendar;

import model.Block;
import model.Transaction;

public class BlocksData {
	
	private static long[] timeStamps = {new GregorianCalendar(2015,5,22,14,21).getTimeInMillis(),
								new GregorianCalendar(2015,5,22,14,27).getTimeInMillis(),
								new GregorianCalendar(2015,5,22,14,29).getTimeInMillis(),
								new GregorianCalendar(2015,5,22,14,33).getTimeInMillis(),
								new GregorianCalendar(2015,5,22,14,38).getTimeInMillis(),
								new GregorianCalendar(2015,5,22,14,41).getTimeInMillis(),
								new GregorianCalendar(2015,5,22,14,46).getTimeInMillis(),
								new GregorianCalendar(2015,5,22,14,47).getTimeInMillis(),
								new GregorianCalendar(2015,5,22,14,51).getTimeInMillis(),
								new GregorianCalendar(2015,5,22,14,55).getTimeInMillis()};
	
	private static int[] customerIds = {1732,1650,2209,4545,324,1944,6565,1805,1765,7001};
	private static double[] amounts = {103.27,66.54,-21.09,44.65,177.99,189.02,17.00,32.99,60.00,-10.00};

	public static Block getNextBlock(int id, String lastHash) {
		
		Transaction transaction = new Transaction(id, timeStamps[id], customerIds[id], amounts[id]);
		
		Block nextBlock = new Block(transaction, lastHash);
		return nextBlock;
	}
	
	
}
