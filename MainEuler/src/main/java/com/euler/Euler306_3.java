package com.euler;

public class Euler306_3 {
	private final static int LIMIT=1000000;
	
	// https://oeis.org/A002187. A different option would be to generate these arrays using brute force, then continue with the same procedure.
	// Very stupid me was ADDING instead of counting. Replacing "sum" with "count" did the trick.
	private final static long[] FIRST_SEQUENCE=new long[]   {0,1,1,2,0,3,1,1,0,3,3,2,2,4,0,5,2,2,3,3,0,1,1,3,0,2,1,1,0,4,5,2,7,4};
	private final static long[] SECOND_SEQUENCE=new long[]  {0,1,1,2,0,3,1,1,0,3,3,2,2,4,4,5,5,2,3,3,0,1,1,3,0,2,1,1,0,4,5,3,7,4};
	private final static long[] PERIODIC_SEQUENCE=new long[]{8,1,1,2,0,3,1,1,0,3,3,2,2,4,4,5,5,9,3,3,0,1,1,3,0,2,1,1,0,4,5,3,7,4};
	
	private final static int SEQUENCES_LENGTH=FIRST_SEQUENCE.length;
	
	private static long countArray(long[] array,int maxLength)	{
		long result=0;
		for (int i=0;i<maxLength;++i) if (array[i]!=0) ++result;
		return result;
	}
	
	private static long countArray(long[] array)	{
		return countArray(array,array.length);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int q=(LIMIT/SEQUENCES_LENGTH)-2;
		int m=LIMIT%SEQUENCES_LENGTH;
		long result=countArray(FIRST_SEQUENCE)+countArray(SECOND_SEQUENCE)+q*countArray(PERIODIC_SEQUENCE)+countArray(PERIODIC_SEQUENCE,m);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
