package com.euler;

import java.util.HashMap;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.collect.ImmutableMap;

public class Euler609 {
	private final static int LIMIT=100000000;
	private final static long MOD=1000000007l;
	
	private static class LinkedUList	{
		private final Map<Integer,Integer> countOfNonPrimeSubSequences;
		private final boolean isComposite;
		public LinkedUList()	{
			countOfNonPrimeSubSequences=ImmutableMap.of(1,1);
			isComposite=true;	// Base number (1) is non-prime. Ok, "isComposite" is not a good name choice.
		}
		public LinkedUList(LinkedUList tail,boolean isComposite)	{
			countOfNonPrimeSubSequences=updateMap(tail.countOfNonPrimeSubSequences,isComposite);
			this.isComposite=isComposite;
		}
		public void addMeToMap(Map<Integer,Integer> counter)	{
			int me=isComposite?1:0;
			for (Map.Entry<Integer,Integer> entry:countOfNonPrimeSubSequences.entrySet())	{
				int key=entry.getKey();
				int value=entry.getValue();
				if (key==me)	{
					if (value==1) continue;
					else EulerUtils.increaseCounter(counter,key,value-1);
				}	else EulerUtils.increaseCounter(counter,key,value);
			}
		}
		private static Map<Integer,Integer> updateMap(Map<Integer,Integer> previous,boolean isComposite)	{
			if (!isComposite)	{
				Map<Integer,Integer> result=new HashMap<>(previous);
				EulerUtils.increaseCounter(result,0);
				return result;
			}	else	{
				Map<Integer,Integer> result=new HashMap<>();
				for (Map.Entry<Integer,Integer> entry:previous.entrySet()) result.put(1+entry.getKey(),entry.getValue());
				EulerUtils.increaseCounter(result,1);
				return result;
			}
		}
	}
	
	public static void main(String[] args)	{
		boolean[] composites=Primes.sieve(LIMIT);
		LinkedUList[] repositories=new LinkedUList[1+LIMIT];
		repositories[1]=new LinkedUList();
		int primesBehind=0;
		Map<Integer,Integer> totalSequences=new HashMap<>();
		for (int i=2;i<=LIMIT;++i)	{
			if ((i%100000)==0) System.out.println(""+i+"...");
			if (!composites[i])	{
				repositories[primesBehind]=null;	// Freeing unneeded memory.
				++primesBehind;
			}
			repositories[i]=new LinkedUList(repositories[primesBehind],composites[i]);
			repositories[i].addMeToMap(totalSequences);
		}
		long result=1;
		for (Integer counter:totalSequences.values())	{
			result*=counter.longValue();
			result%=MOD;
		}
		System.out.println(result);
	}
}
