package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class Euler586 {
	private final static int N=IntMath.pow(10,5);
	
	private static class IntPair	{
		public final int a;
		public final int b;
		public IntPair(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		@Override
		public String toString()	{
			return "<"+a+","+b+">";
		}
	}
	
	private static class SquareSumInformation	{
		private final LongIntMap decomp;
		private final int a0;
		private final int[] as;
		private final int[] bs;
		private final List<IntPair> validPairs;
		private final List<IntPair> invalidPairs;
		private SquareSumInformation(LongIntMap decomp,int a0,int[] as,int[] bs,List<IntPair> validPairs,List<IntPair> invalidPairs)	{
			this.decomp=decomp;
			this.a0=a0;
			this.as=as;
			this.bs=bs;
			this.validPairs=validPairs;
			this.invalidPairs=invalidPairs;
		}
		public int getExpectedResults()	{
			for (int a:as) if ((a%2)==1) return 0;
			int result=1;
			for (int b:bs) result*=b+1;
			if ((result%2)==0) return result/2;
			else return (result+1)/2;
			//return (((a0%2)==0)?(result+1):(result-1))/2;
		}
	}
	private static class SquareSumInformationGenerator	{
		private final int[] lastPrimeSieve;
		private final int[] squaresCache;
		public SquareSumInformationGenerator(int limit)	{
			lastPrimeSieve=Primes.lastPrimeSieve(limit);
			int s=IntMath.sqrt(limit,RoundingMode.UP);
			squaresCache=new int[s+1];
			for (int i=0;i<=s;++i) squaresCache[i]=i*i;
		}
		public SquareSumInformation getFor(int n)	{
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(n,lastPrimeSieve);
			LongIntMap factors=divs.getFactorMap();
			int a0=0;
			IntStream.Builder as=IntStream.builder();
			IntStream.Builder bs=IntStream.builder();
			for (LongIntCursor cursor=factors.cursor();cursor.moveNext();)	{
				long primeMod=cursor.key()%4;
				int exp=cursor.value();
				if (primeMod==1) bs.accept(exp);
				else if (primeMod==2) a0=exp;
				else if (primeMod==3) as.accept(exp);
				else throw new RuntimeException("No puedo entender algo como esto.");
			}
			List<IntPair> validPairs=new ArrayList<>();
			List<IntPair> invalidPairs=new ArrayList<>();
			for (int i=0;;++i)	{
				int sq=squaresCache[i];
				int diff=n-sq;
				if (diff<sq) break;
				int j=Arrays.binarySearch(squaresCache,diff);
				if (j>=0)	{
					IntPair pair=new IntPair(i,j);
					List<IntPair> targetList=(j>2*i)?validPairs:invalidPairs;
					targetList.add(pair);
				}
			}
			return new SquareSumInformation(factors,a0,as.build().toArray(),bs.build().toArray(),validPairs,invalidPairs);
		}
	}
	
	public static void main(String[] args)	{
		SquareSumInformationGenerator gen=new SquareSumInformationGenerator(N);
		int counter=0;
		for (int i=2;i<=N;++i)	{
			SquareSumInformation info=gen.getFor(i);
			int results=info.validPairs.size()+info.invalidPairs.size();
			if (results==0) continue;
			else if (info.validPairs.size()==4) ++counter;
			System.out.println(i+":");
			System.out.println("\tPrime decomposition: "+info.decomp+".");
			System.out.println("\ta0="+info.a0+".");
			System.out.println("\tas="+Arrays.toString(info.as)+".");
			System.out.println("\tbs="+Arrays.toString(info.bs)+".");
			System.out.println("\tExpected results="+info.getExpectedResults()+".");
			System.out.println("\tValid results="+info.validPairs.size()+": "+info.validPairs+".");
			System.out.println("\tInvalid results="+info.invalidPairs.size()+": "+info.invalidPairs+".");
		}
		System.out.println(counter);
	}
}
