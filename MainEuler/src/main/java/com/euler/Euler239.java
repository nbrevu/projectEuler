package com.euler;

import java.io.PrintStream;

import com.euler.common.EulerUtils.IntPermutation;
import com.euler.common.EulerUtils.IntPermutationGenerator;

public class Euler239 {
	/*
	private final static int TOTAL=100;
	private final static int PRIMES=25;
	private final static int DERANGED=22;
	*/
	
	private final static int SIZE=10;
	
	// This is just a simulation.
	// We define:
	// P(N,S,C,M) as the amount of permutations of N elements, S of which are special, so that "C" of them
	// are in their exact position, and "M" are in an special position which is not theirs.
	//
	// P(N,S,C) as the amount of permutations of N elements, S of which are special, so that "C" of them
	// are in their exact position. It follows that the sum from M=0 to S-C of P(N,S,C,M) must be equal
	// to P(N,S,C).
	private static int[] count(IntPermutation p,int S)	{
		int[] result=new int[2];
		final int[] numbers=p.getNumbers();
		for (int i=0;i<S;++i) if (numbers[i]==i) ++result[0];
		else if (numbers[i]<S) ++result[1];
		return result;
	}
	
	public static void main(String[] args)	{
		int[][][] P_4=new int[SIZE+1][SIZE+1][SIZE+1];
		int[][] P_3=new int[SIZE+1][SIZE+1];
		for (IntPermutation perm:new IntPermutationGenerator(10)) for (int S=1;S<=10;++S)	{
			int[] result=count(perm,S);
			int C=result[0];
			int M=result[1];
			++P_4[S][C][M];
			++P_3[S][C];
		}
		try (PrintStream ps=new PrintStream("C:\\239.txt"))	{
			for (int S=1;S<=SIZE;++S)	{
				for (int C=0;C<=S;++C)	{
					for (int M=0;M<=S-C;++M) ps.println("P("+SIZE+","+S+","+C+","+M+")="+P_4[S][C][M]);
					ps.println();
				}
				ps.println();
			}
			ps.println();
			for (int S=1;S<=SIZE;++S)	{
				for (int C=0;C<=S;++C) ps.println("P("+SIZE+","+S+","+C+")="+P_3[S][C]);
				ps.println();
			}
		}	catch (Exception exc)	{
			System.out.println("Pues va a ser que ha habido das kleine Problemica: "+exc);
		}
		// Y ahora, das Sanity check.
		for (int S=0;S<=SIZE;++S) for (int C=0;C<=S;++C)	{
			int s=0;
			for (int M=0;M<=S;++M) s+=P_4[S][C][M];
			try	{
				assert s==P_3[S][C];
			}	catch (AssertionError err)	{
				System.out.println("Falla para S="+S+", C="+C+". P_4="+s+"; P_3="+P_3[S][C]+".");
			}
		}
	}
}
