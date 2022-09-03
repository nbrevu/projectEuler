package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler570_7 {
	private final static int N=10000000;
	
	// Represents a succession of the form a_n=a_0+kn.
	private static class LinearProgression	{
		public final long a0;
		public final long k;
		public LinearProgression(long a0,long k)	{
			this.a0=a0;
			this.k=k;
		}
		public LinearProgression chineseRemainderTheorem(long otherMod,long p)	{
			long coeff=EulerUtils.solveChineseRemainder(a0,k,otherMod,p);
			return new LinearProgression(coeff,k*p);
		}
	}
	
	private static LinearProgression findT1Cycle(long p)	{
		long a0=2;
		long p4=2;
		long p3=1;
		LongSet visited=HashLongSets.newMutableSet();
		for (;;)	{
			p4=(4*p4)%p;
			p3=(3*p3)%p;
			++a0;
			if (p3==p4) break;
			if (!visited.add(p3*p+p4)) return null;
		}
		long a1=a0;
		for (;;)	{
			p4=(4*p4)%p;
			p3=(3*p3)%p;
			++a1;
			if (p3==p4) break;
		}
		return new LinearProgression(a0,a1-a0);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] gcds=new long[1+N];
		Arrays.fill(gcds,3,1+N,6);
		long lastPrime=N/5;
		long[] primes=Primes.listLongPrimesAsArraySkippingFirstOnes(lastPrime,2);
		for (long p:primes)	{
			LinearProgression t1Zeroes=findT1Cycle(p);
			if (t1Zeroes==null) continue;
			long mod=((p-3)*EulerUtils.modulusInverse(7,p))%p;	// For 7 there aren't cycles. No degenerate results.
			LinearProgression t3Zeroes=t1Zeroes.chineseRemainderTheorem(mod,p);
			for (long n=t3Zeroes.a0;n<=N;n+=t3Zeroes.k)	{
				System.out.println(p+"!");
				gcds[(int)n]*=p;
			}
		}
		long result=0;
		for (int i=3;i<=N;++i) result+=gcds[i];
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
