package com.euler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler467_2 {
	private final static long MOD=LongMath.pow(10l,9)+7;
	private final static int SIZE=10000;
	
	// This is a shameless adaptation of my solution for Rosalind's SCSP. Details have changed but the algorithm itself is the same one.
	private static List<Integer> buildDigitalRoots(boolean[] composites,boolean valueToMatch,int length)	{
		List<Integer> result=new ArrayList<>(length);
		int currentPos=0;
		for (int i=2;currentPos<length;++i) if (composites[i]==valueToMatch)	{
			int digit=i%9;
			result.add((digit==0)?9:digit);
			++currentPos;
		}
		return result;
	}

	private static int[][] getMinLengths(List<Integer> s1,List<Integer> s2)	{
		int N=s1.size();
		int M=s2.size();
		int[][] result=new int[N+1][M+1];
		for (int i=1;i<=N;++i) result[i][0]=i;
		for (int j=1;j<=M;++j) result[0][j]=j;
		for (int i=1;i<=N;++i) for (int j=1;j<=M;++j) if (s1.get(i-1)==s2.get(j-1)) result[i][j]=1+result[i-1][j-1];
		else result[i][j]=1+Math.min(result[i][j-1],result[i-1][j]);
		return result;
	}
	
	private static class ModAppender	{
		private final long mod;
		private long currentValue;
		private long appending;
		public ModAppender(long mod)	{
			this.mod=mod;
			currentValue=0;
			appending=1;
		}
		public void append(long digit)	{
			currentValue+=appending*digit;
			currentValue%=mod;
			appending*=10;
			appending%=mod;
		}
		public long getValue()	{
			return currentValue;
		}
	}
	
	private static long backtrack(int[][] minLengths,List<Integer> s1,List<Integer> s2,long mod)	{
		/*
		 * This method must be fixed. I can think of a way to improve this, but it's not good computationally.
		 * HOWEVER, if we reverse the strings we can create a greedy algorithm, I think. As a bonus, the ModAppender class will be less awkward.
		 */
		ModAppender result=new ModAppender(mod);
		int i=s1.size();
		int j=s2.size();
		while ((i>0)&&(j>0)) if (s1.get(i-1)==s2.get(j-1))	{
			result.append(s1.get(i-1));
			--i;
			--j;
		}	else if (minLengths[i-1][j]>minLengths[i][j-1])	{
			result.append(s2.get(j-1));
			--j;
		}	else	{
			result.append(s1.get(i-1));
			--i;
		}
		while (i>0)	{
			result.append(s1.get(i-1));
			--i;
		}
		while (j>0)	{
			result.append(s2.get(j-1));
			--j;
		}
		return result.getValue();
	}
	
	public static void main(String[] args) throws IOException	{
		int sieveLimit=(int)(Math.ceil(1.2*(SIZE+1)*Math.log(SIZE+1)));
		boolean[] composites=Primes.sieve(sieveLimit);
		List<Integer> primeDRs=buildDigitalRoots(composites,false,SIZE);
		List<Integer> compositeDRs=buildDigitalRoots(composites,true,SIZE);
		int[][] minLengths=getMinLengths(primeDRs,compositeDRs);
		long result=backtrack(minLengths,primeDRs,compositeDRs,MOD);
		System.out.println(result);
	}
}
