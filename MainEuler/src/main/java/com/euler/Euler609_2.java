package com.euler;

import com.euler.common.Primes;

public class Euler609_2 {
	private final static int LIMIT=100000000;
	private final static long MOD=1000000007l;
	
	private static class SubsequenceSummary	{
		private final int[] nonPrimeSubSequences;
		private final boolean nonPrime;
		public SubsequenceSummary()	{
			nonPrimeSubSequences=new int[]{0,1};
			nonPrime=true;
		}
		public SubsequenceSummary(SubsequenceSummary previous,boolean nonPrime)	{
			nonPrimeSubSequences=updateArray(previous.nonPrimeSubSequences,nonPrime);
			this.nonPrime=nonPrime;
		}
		public void addMeToMap(int[] counter)	{
			int me=nonPrime?1:0;
			for (int i=0;i<nonPrimeSubSequences.length;++i) counter[i]+=nonPrimeSubSequences[i];
			--counter[me];
		}
		private static int[] updateArray(int[] previous,boolean nonPrime)	{
			if (!nonPrime)	{
				int[] result=new int[previous.length];
				System.arraycopy(previous,0,result,0,previous.length);
				++result[0];
				return result;
			}	else	{
				int[] result=new int[1+previous.length];
				System.arraycopy(previous,0,result,1,previous.length);
				++result[1];
				return result;
			}
		}
	}
	
	private static int countPrimes(boolean[] composites)	{
		int result=0;
		for (boolean value:composites) if (!value) ++result;
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		boolean[] composites=Primes.sieve(LIMIT);
		int maxPrimes=countPrimes(composites);
		SubsequenceSummary[] repositories=new SubsequenceSummary[1+LIMIT];
		repositories[1]=new SubsequenceSummary();
		int primesBehind=0;
		int[] totalSequences=new int[maxPrimes];
		for (int i=2;i<=LIMIT;++i)	{
			if (!composites[i])	{
				repositories[primesBehind]=null;	// Freeing unneeded memory.
				++primesBehind;
			}
			repositories[i]=new SubsequenceSummary(repositories[primesBehind],composites[i]);
			repositories[i].addMeToMap(totalSequences);
			if (i>maxPrimes) repositories[i]=null;	// I've already used your data and I need the memory, so good bye.
		}
		long result=1;
		for (int counter:totalSequences)	{
			if (counter==0) break;
			result*=counter;
			result%=MOD;
		}
		long tac=System.nanoTime();
		double seconds=((tac-tic)/1e9);
		System.out.println(result);
		System.out.println("Result found in "+seconds+" seconds.");
	}
}
