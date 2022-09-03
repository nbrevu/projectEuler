package com.euler;

import java.util.Arrays;

import com.euler.common.Primes;

public class Euler492 {
	private final static int PRIME_START=1000000000;
	private final static int PRIME_SHIFT=10000000;
	private final static int PRIME_END=PRIME_START+PRIME_SHIFT;
	private final static long GOAL=1000000000000000l;
	
	private final static int MAX_SIZE=(PRIME_END/3)+2;
	private final static long[] POSITION_TO_VALUE=new long[MAX_SIZE];
	private final static int[] VALUE_TO_POSITION=new int[PRIME_END];
	static	{
		Arrays.fill(VALUE_TO_POSITION,-1);
	}
	
	private static long getNthTerm(long n,long mod)	{
		long a=1;
		for (int i=0;;++i)	{
			if (n==(long)i) return a;
			int oldIndex=VALUE_TO_POSITION[(int)a];
			if (oldIndex!=-1)	{
				long loopSize=i-oldIndex;
				System.out.println("\tFound loop of size "+loopSize+"!");
				long diff=n-oldIndex;
				long posInLoop=diff%loopSize;
				int realPos=(int)(oldIndex+posInLoop);
				long result=POSITION_TO_VALUE[realPos];
				for (int j=0;j<i;++j) VALUE_TO_POSITION[(int)POSITION_TO_VALUE[j]]=-1;	// Undo the current numbers...
				return result;
			}
			VALUE_TO_POSITION[(int)a]=i;
			POSITION_TO_VALUE[i]=a;
			a=(a*(6*a+10)+3)%mod;
		}
	}
	
	// Vale, funciona pero es hiperlento.
	public static void main(String[] args)	{
		int totalPrimeLimit=PRIME_START+PRIME_SHIFT;
		boolean[] composites=Primes.sieve(totalPrimeLimit);
		long res=0;
		for (int i=PRIME_START;i<=totalPrimeLimit;++i) if (!composites[i])	{
			System.out.println(""+i+"...");
			res+=getNthTerm(GOAL-1,i);
		}
		System.out.println(res);
	}
}
