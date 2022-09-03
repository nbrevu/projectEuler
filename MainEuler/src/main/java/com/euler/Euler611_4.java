package com.euler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.LongPair;
import com.google.common.math.LongMath;

public class Euler611_4 {
	private final static Comparator<int[]> ARRAY_COMPARATOR=new Comparator<>()	{
		@Override
		public int compare(int[] a1,int[] a2) {
			for (int i=0;i<a1.length;++i)	{
				int diff=a1[i]-a2[i];
				if (diff!=0) return diff;
			}
			return 0;
		}
	};
	
	private static class SquarePatternExaminator	{
		private final long[] primes;
		private final long[] limits;
		private final SortedMap<int[],Integer> patterns;
		private final SumSquareDecomposer decomposer;
		public SquarePatternExaminator(long limit,long... primes)	{
			this.primes=primes;
			this.limits=new long[primes.length];
			for (int i=0;i<limits.length;++i) limits[i]=limit/primes[i];
			patterns=new TreeMap<>(ARRAY_COMPARATOR);
			decomposer=new SumSquareDecomposer();
		}
		public void examine()	{
			int[] exps=new int[primes.length];
			exps[0]=1;
			long number=primes[0];
			DivisorHolder divs=new DivisorHolder();
			divs.addFactor(primes[0],1);
			examineRecursive(exps,number,divs);
		}
		private void examineRecursive(int[] exps,long number,DivisorHolder divs)	{
			BaseSquareDecomposition decomps=decomposer.getFor(divs);
			int valid=0;
			for (LongPair pair:decomps.getBaseCombinations()) if ((pair.x>0)&&(pair.x!=pair.y)) ++valid;
			else if (pair.x>pair.y) throw new IllegalStateException("Un momento. Ese hombre eh un impostol.");
			int[] copy=Arrays.copyOf(exps,exps.length);
			patterns.put(copy,valid);
			for (int i=0;i<primes.length;++i) if ((number<=limits[i])&&((i==0)||(exps[i]<exps[i-1])))	{
				long p=primes[i];
				++exps[i];
				divs.addFactor(p,1);
				examineRecursive(exps,number*p,divs);
				divs.removeFactor(p,1);
				--exps[i];
			}
		}
		public SortedMap<int[],Integer> getPatterns()	{
			return patterns;
		}
	}
	
	public static void main(String[] args)	{
		long limit=LongMath.pow(10l,12);
		SquarePatternExaminator examinator=new SquarePatternExaminator(limit,5,13,17,29,37,41,53,61);
		examinator.examine();
		for (Map.Entry<int[],Integer> pattern:examinator.getPatterns().entrySet())	{
			int numPatterns=pattern.getValue();
			String prefix=(((numPatterns%2)==1)?"\t":"");
			System.out.println(prefix+Arrays.toString(pattern.getKey())+" => "+numPatterns+".");
		}
	}
}
