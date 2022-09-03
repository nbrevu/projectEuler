package com.euler;

public class Euler326_2 {
	private final static int N=100000;
	private final static int BIG_N=5000000;
	
	/*
	 * The cycle length REALLY is 6*N :O. At least for all the common cases.
	 */
	public static void main(String[] args)	{
		long[] result=new long[BIG_N];
		long[] moddedResult=new long[BIG_N];
		result[0]=1;
		moddedResult[0]=1;
		long sum=0;
		for (int i=1;i<BIG_N;++i)	{
			sum+=result[i-1]*i;
			if (sum<0) throw new IllegalStateException("PUES CATACROCKER.");
			result[i]=sum%(i+1);
			moddedResult[i]=result[i]%N;
			if ((moddedResult[i]==1)&&(moddedResult[i-1]==0)&&((i%N)==0)) System.out.println("Presunto ciclo encontrado en N="+i+".");
		}
		/*-
		System.out.println(Arrays.toString(result));
		System.out.println(Arrays.toString(moddedResult));
		*/
	}
}
