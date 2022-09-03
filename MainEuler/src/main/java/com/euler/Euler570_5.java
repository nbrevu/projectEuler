package com.euler;

import com.euler.common.Primes;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler570_5 {
	// Represents a succession of the form a_n=a_0+kn.
	private static class LinearProgression	{
		public final long a0;
		public final long k;
		public LinearProgression(long a0,long k)	{
			this.a0=a0;
			this.k=k;
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
		// Verification!!!!! This can be removed from the "proper" end algorithm.
		{
			long a2=a1;
			for (;;)	{
				p4=(4*p4)%p;
				p3=(3*p3)%p;
				++a2;
				if (p3==p4) break;
			}
			if ((a2-a1)!=(a1-a0)) throw new RuntimeException("Vaya.");
		}
		return new LinearProgression(a0,a1-a0);
	}
	
	private static long getProperMod(long n,long p)	{
		n=n%p;	// This is in [-(p-1)..p-1].
		return (p+n)%p;	// This is in [0..p-1].
	}
	
	private static LinearProgression findT3Cycle(long p)	{
		long a0=2;
		long p4=2;
		long p3=1;
		long f1=getProperMod(-17,p);
		long f2=17%p;
		long pSq=p*p;
		long pCb=pSq*p;
		LongSet visited=HashLongSets.newMutableSet();
		for (;;)	{
			p4=(4*p4)%p;
			p3=(3*p3)%p;
			f1=(f1+3)%p;
			f2+=(f2+2)%p;
			++a0;
			if (((f1*p3+f2*p4)%p)==0) break;
			if (!visited.add(f2*pCb+f1*pSq+p3*p+p4)) return null;
		}
		long a1=a0;
		for (;;)	{
			p4=(4*p4)%p;
			p3=(3*p3)%p;
			f1=(f1+3)%p;
			f2+=(f2+2)%p;
			++a1;
			if (((f1*p3+f2*p4)%p)==0) break;
		}
		// Verification!!!!! This can be removed from the "proper" end algorithm.
		{
			long a2=a1;
			for (;;)	{
				p4=(4*p4)%p;
				p3=(3*p3)%p;
				f1=(f1+3)%p;
				f2+=(f2+2)%p;
				++a2;
				if (((f1*p3+f2*p4)%p)==0) break;
			}
			if ((a2-a1)!=(a1-a0)) throw new RuntimeException("Bueno.");
		}
		return new LinearProgression(a0,a1-a0);
	}
	
	public static void main(String[] args)	{
		long maxPrime=10000;
		long[] primes=Primes.listLongPrimesAsArraySkippingFirstOnes(maxPrime,2);
		for (long p:primes)	{
			LinearProgression lp=findT1Cycle(p);
			if (lp==null) System.out.println("p="+p+": no hay ciclo.");
			else	{
				System.out.println("p="+p+": ciclo de la forma "+lp.a0+"+"+lp.k+"·n.");
				// EXCEPTION! non-linear cycles (or more than one cycle?) for even p=5.
				findT3Cycle(p);
			}
		}
	}
}
