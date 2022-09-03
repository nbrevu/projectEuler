package com.euler;

import com.euler.common.EulerUtils;

// D'OH, I had misunderstood the problem. The real problem seems to be much more difficult.
public class Euler767_3 {
	private final static int S=16;
	private final static long MOD=1_000_000_007l;
	private final static int K=2;//IntMath.pow(10,5);
	private final static long N=4;//LongMath.pow(10l,16);

	private final static long REPS=N/K;
	
	// Is this insane? PROBABLY. Will it end in a reasonable time? PROBABLY NOT!
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		// Combinatorial numbers.
		int modInt=(int)MOD;
		int[][] combis=new int[1+K][];
		combis[0]=new int[] {1};
		for (int i=1;i<=K;++i)	{
			combis[i]=new int[1+i];
			combis[i][0]=1;
			for (int j=1;j<i;++j) combis[i][j]=(combis[i-1][j-1]+combis[i-1][j])%modInt;
			combis[i][i]=1;
		}
		// Powers of S^REPS. (linear... basically tiny in comparison of the other O(n^2) storage).
		long[] sPowers=new long[1+K];
		sPowers[0]=1l;
		sPowers[1]=EulerUtils.expMod(S,REPS,MOD);
		for (int i=2;i<=K;++i) sPowers[i]=(sPowers[i-1]*sPowers[1])%MOD;
		int[][] storage=new int[1+K][];
		// Initialise for a=1.
		for (int b=0;b<=K;++b)	{
			storage[b]=new int[1+b];
			for (int c=0;c<=b;++c)	{
				long val=combis[b][c]*sPowers[c];
				storage[b][c]=(int)(val%MOD);
			}
		}
		// Iterate!! We reuse the same storage, because we are already spending too much!
		for (int a=2;a<S;++a)	{
			// If we use decreasing instead of increasing order, all the data we read corresponds to the previous "a" and the values are correct.
			for (int b=K;b>=0;--b) for (int c=b;c>=0;--c)	{
				long val=0;
				int iB=b;
				int iC=c/a;
				int maxI=(iB<iC)?iB:iC;
				for (int i=0;i<=maxI;++i)	{
					long tmpVal=storage[b-i][c-i*a];
					tmpVal=(tmpVal*combis[b][i])%MOD;
					tmpVal=(tmpVal*sPowers[i])%MOD;
					val+=tmpVal;
				}
				storage[b][c]=(int)(val%MOD);
			}
		}
		// Final value (a=S). We want to calculate f(S,K,K).
		int maxI=K/S;
		long result=0;
		for (int i=0;i<=maxI;++i)	{
			long tmpVal=storage[K-i][K-i*S];
			tmpVal=(tmpVal*combis[K][i])%MOD;
			tmpVal=(tmpVal*sPowers[i])%MOD;
			result+=tmpVal;
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
