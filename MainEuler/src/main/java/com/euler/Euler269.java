package com.euler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;

public class Euler269 {
	private final int DIGITS=5;
	
	private static class RemainderCounter	{
		public static Map<Long,Long> getRemainderCounter(int generations,int factor)	{
			// ZUTUN!
			/*
			 * Hay que darle una pensada a esto. A lo mejor lo ideal es devolver un multiset. No sé si un multiset de 1e8
			 * elementos cabe bien en memoria (es cuestión de probar), pero al menos tiene mejor pinta que un map de 1e8
			 * elementos...
			 */
			return null;
		}
	}
	
	private static Map<Integer,List<Integer>> createDivisorMap()	{
		Map<Integer,List<Integer>> result=new HashMap<>();
		result.put(1,Arrays.asList(1));
		result.put(2,Arrays.asList(1,2));
		result.put(3,Arrays.asList(1,3));
		result.put(4,Arrays.asList(1,2,4));
		result.put(5,Arrays.asList(1,5));
		result.put(6,Arrays.asList(1,2,3,6));
		result.put(7,Arrays.asList(1,7));
		result.put(8,Arrays.asList(1,2,4,8));
		result.put(9,Arrays.asList(1,3,9));
		return result;
	}
	
	private static boolean evaluate(int n,int div)	{
		int sum=0;
		int current=1;
		while (n>0)	{
			int rem=n%10;
			n/=10;
			sum+=rem*current;
			current*=-div;
		}
		return sum==0;
	}
	
	public static void main(String[] args)	{
		int counter=0;
		Map<Integer,Integer> multiplicities=new HashMap<>();
		Map<Integer,List<Integer>> divisors=createDivisorMap();
		for (int i=1;i<=1000000000;++i)	{
			int rem=i%10;
			if (rem==0) ++counter;
			else	{
				if (i<10) continue;
				int matches=0;
				for (int divisor:divisors.get(rem)) if (evaluate(i,divisor)) ++matches;
				if (matches>0)	{
					EulerUtils.increaseCounter(multiplicities,matches);
					++counter;
					if (matches>2) System.out.println(""+i+" => "+matches+".");
				}
			}
		}
		System.out.println(counter);
		System.out.println(multiplicities);
	}
}
