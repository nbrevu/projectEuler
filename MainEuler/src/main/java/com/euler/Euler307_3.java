package com.euler;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

public class Euler307_3 {
	// Not enough precision! Let's try C++'s 128 bit floats.
	private final static int DEFECTS=20000;
	private final static int CHIPS=1000000;
	
	// Total amount of distributions: CHIPS^DEFECTS.
	/* Total amount of distributions with no more than one defect per chip:
	 * 	In this case we just have a distribution of 20000 chips with one defect, and 980000 with no defect.
	 *  So we use a combinatorial number, (CHIPS over DEFECTS).
	 */
	/* Now let's suppose that we have N chips with exactly two defects.
	 *  So there are 2N defects in these N chips.
	 *  And then there are 20000-2N defects to be distributed among 1000000-N chips, no more than one per chip.
	 *  Therefore, there will be (20000-2N) chips with one defect, and the amount of chips with no defect is:
	 *  (1000000-N)-(20000-2N)=980000+N.
	 * This means that in this case we have [1000000 over (N,20000-2N,980000+N)] defects.
	 * This case can in fact be integrated in the previous one, with N=0.
	 */
	private static double[] getLogFactorials(int max)	{
		double[] result=new double[1+max];
		for (int i=2;i<=max;++i) result[i]=result[i-1]+Math.log(i);
		return result;
	}
	
	public static void main(String[] args)	{
		int maxWith2=DEFECTS/2;
		SortedMultiset<Double> sortedResults=TreeMultiset.create();
		double[] factLogs=getLogFactorials(CHIPS);
		double commonLog=factLogs[DEFECTS]+factLogs[CHIPS]-DEFECTS*Math.log(CHIPS);
		for (int i=0;i<=maxWith2;++i)	{
			double quotientLog=commonLog-(factLogs[i]+factLogs[DEFECTS-2*i]+factLogs[CHIPS-DEFECTS+i]+i*factLogs[2]);
			sortedResults.add(quotientLog);
		}
		double result=0.0;
		for (double log:sortedResults) result+=Math.exp(log);
		result=1.0-result;
		System.out.println(result);
	}
}
