package com.euler;

public class Euler306 {
	private final static int LIMIT=1000000;
	
	// https://oeis.org/A002187. A different option would be to generate these arrays using brute force, then continue with the same procedure.
	// Apparently the sequence is wrong.
	private final static long[] FIRST_SEQUENCE=new long[]{0,1,1,2,0,3,1,1,0,3,3,2,2,4,0,5,2,2,3,3,0,1,1,3,0,2,1,1,0,4,5,2,7,4};
	private final static long[] SECOND_SEQUENCE=new long[]{0,1,1,2,0,3,1,1,0,3,3,2,2,4,4,5,5,2,3,3,0,1,1,3,0,2,1,1,0,4,5,3,7,4};
	private final static long[] PERIODIC_SEQUENCE=new long[]{8,1,1,2,0,3,1,1,0,3,3,2,2,4,4,5,5,9,3,3,0,1,1,3,0,2,1,1,0,4,5,3,7,4};
	
	private final static int SEQUENCES_LENGTH=FIRST_SEQUENCE.length;
	
	private static long sumArray(long[] array,int maxLength)	{
		long result=0;
		for (int i=0;i<maxLength;++i) result+=array[i];
		return result;
	}
	
	private static long sumArray(long[] array)	{
		return sumArray(array,array.length);
	}
	
	public static void main(String[] args)	{
		assert FIRST_SEQUENCE.length==SEQUENCES_LENGTH;
		assert SECOND_SEQUENCE.length==SEQUENCES_LENGTH;
		assert PERIODIC_SEQUENCE.length==SEQUENCES_LENGTH;
		long result=sumArray(FIRST_SEQUENCE)+sumArray(SECOND_SEQUENCE);
		int q=(LIMIT/SEQUENCES_LENGTH)-2;
		int m=LIMIT%SEQUENCES_LENGTH;
		result+=q*sumArray(PERIODIC_SEQUENCE)+sumArray(PERIODIC_SEQUENCE,m);
		System.out.println(result);
	}
}
