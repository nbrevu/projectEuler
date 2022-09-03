package com.euler;

import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler634_4 {
	private final static long LIMIT=3000000;
	
	private static boolean isExponentSetValid(int[] exponents)	{
		int any2=0;
		int any3=0;
		int any6=0;
		boolean anyComplete=false;
		for (int e:exponents) if (e==1) return false;
		else if ((e==2)||(e==4)) any2=1;
		else if (e==3) any3=1;
		else if (e==6) ++any6;	// Two sixes are acceptable.
		else anyComplete=true;	// If there is any exponent equal to 5 or >=7, and all the other ones are >=2, the combination is always valid.
		return anyComplete||(any2+any3+any6>=2);	// Kind of weird, but it works.
	}
	
	private static class Array implements Comparable<Array>	{
		public final int[] data;
		public final int hashCode;
		public Array(int[] data)	{
			Arrays.sort(data);
			for (int i=0;i<data.length/2;++i)	{
				int swap=data[data.length-1-i];
				data[data.length-1-i]=data[i];
				data[i]=swap;
			}
			this.data=data;
			hashCode=Arrays.hashCode(data);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Array aOther=(Array)other;
			return Arrays.equals(data,aOther.data);
		}
		@Override
		public String toString()	{
			return Arrays.toString(data);
		}
		@Override
		public int compareTo(Array other)	{
			for (int i=0;;++i)	{
				boolean exceededThis=i>=data.length;
				boolean exceededOther=i>=other.data.length;
				if (exceededThis&&exceededOther) return 0;
				else if (exceededThis) return -1;
				else if (exceededOther) return 1;
				int comparison=Integer.compare(data[i],other.data[i]);
				if (comparison!=0) return comparison;
			}
		}
	}

	public static void main(String[] args)	{
		// La única diferencia con el caso algorítmico es que de algún modo me estoy dejando 8 elementos del caso [3,2,2]. Rarro, rarro.
		long[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		SortedMap<Array,Integer> counters=new TreeMap<>();
		for (int i=2;i<=LIMIT;++i)	{
			DivisorHolder holder=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			int[] exponents=holder.getFactorMap().values().toIntArray();
			if (isExponentSetValid(exponents)) EulerUtils.increaseCounter(counters,new Array(exponents));
		}
		int total=0;
		for (int v:counters.values()) total+=v;
		System.out.println(total);
		for (Map.Entry<Array,Integer> entry:counters.entrySet()) System.out.println(entry.getKey()+": "+entry.getValue()+".");
	}
}
