package com.euler;

import java.util.Arrays;
import java.util.List;

import com.euler.common.Primes;

public class Euler581 {
	// I'm pretty sure that this doesn't give me the proper solution. If I could determine a limit...
	private final static int LIMIT=2000000000;
	private final static int GOAL=47;
	
	private static boolean[] getUnacceptableIndices(boolean[] composites,int firstInvalid)	{
		int N=composites.length;
		boolean[] res=new boolean[N-1];
		for (int i=firstInvalid;i<N;++i) if (!composites[i]) for (long j=i;j<N;j+=i)	{
			if (j<N-1) res[(int)j]=true;
			res[(int)(j-1)]=true;
		}
		return res;
	}
	
	private static int findLastFalseValue(boolean[] sieve)	{
		for (int i=sieve.length-1;i>=0;--i) if (!sieve[i]) return i;
		return -1;
	}
	
	public static void main(String[] args)	{
		boolean[] composites=Primes.sieve(LIMIT);
		// DAS PRUEBA!!!!!
		List<Integer> primes=Arrays.asList(2,3,5,7,11,13,17,19,23,29,31,37,41,47);
		for (int p:primes)	{
			boolean[] sieve=getUnacceptableIndices(composites,p);
			int lastIndex=findLastFalseValue(sieve);
			System.out.println("Primo: "+p+". Último índice encontrado: "+lastIndex);
		}
	}
}
