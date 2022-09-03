package com.euler;

import java.io.IOException;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler467_3 {
	private final static long MOD=LongMath.pow(10l,9)+7;
	private final static int SIZE=10000;
	
	/*
	 * Builds the array in reversed order because that's convenient for the backtrack method, which rebuilds the string in reverse order.
	 * I could also build the array normally and modify the ModAppender class but this is good enough.
	 */
	private static int[] buildDigitalRoots(boolean[] composites,boolean valueToMatch,int length)	{
		int[] result=new int[length];
		int currentPos=length-1;
		for (int i=2;currentPos>=0;++i) if (composites[i]==valueToMatch)	{
			int digit=i%9;
			result[currentPos]=(digit==0)?9:digit;
			--currentPos;
		}
		return result;
	}

	private static int[][] getMinLengths(int[] s1,int[] s2)	{
		int N=s1.length;
		int M=s2.length;
		int[][] result=new int[N+1][M+1];
		for (int i=1;i<=N;++i) result[i][0]=i;
		for (int j=1;j<=M;++j) result[0][j]=j;
		for (int i=1;i<=N;++i) for (int j=1;j<=M;++j) if (s1[i-1]==s2[j-1]) result[i][j]=1+result[i-1][j-1];
		else result[i][j]=1+Math.min(result[i][j-1],result[i-1][j]);
		return result;
	}
	
	private static class ModAppender	{
		private final long mod;
		private long currentValue;
		public ModAppender(long mod)	{
			this.mod=mod;
			currentValue=0;
		}
		public void append(long digit)	{
			currentValue=((currentValue*10)+digit)%mod;
		}
		public long getValue()	{
			return currentValue;
		}
	}
	
	private static long backtrack(int[][] minLengths,int[] s1,int[] s2,long mod)	{
		ModAppender appender=new ModAppender(mod);
		int i=s1.length;
		int j=s2.length;
		while ((i>0)&&(j>0)) if (s1[i-1]==s2[j-1])	{
			appender.append(s1[i-1]);
			--i;
			--j;
		}	else if (minLengths[i-1][j]>minLengths[i][j-1])	{
			appender.append(s2[j-1]);
			--j;
		}	else if (minLengths[i-1][j]<minLengths[i][j-1])	{
			appender.append(s1[i-1]);
			--i;
		}	else if (s1[i-1]<s2[j-1])	{
			/*
			 * From this point on, we need to choose between the two substrings; we always proceed with the smallest digit.
			 * The original algorithm I used in Rosalind didn't need this particular level of finesse because any minimal length solution
			 * was accepted. Here we also need it to be the numerically smallest.
			 */
			appender.append(s1[i-1]);
			--i;
		}	else	{
			appender.append(s2[j-1]);
			--j;
		}
		// Leftovers from either one of the strings...
		while (i>0)	{
			appender.append(s1[i-1]);
			--i;
		}
		while (j>0)	{
			appender.append(s2[j-1]);
			--j;
		}
		return appender.getValue();
	}
	
	public static void main(String[] args) throws IOException	{
		// High enough limit for the prime limit. I'm probably overdoing it but this is good enough.
		long tic=System.nanoTime();
		int sieveLimit=(int)(Math.ceil(1.2*(SIZE+1)*Math.log(SIZE+1)));
		boolean[] composites=Primes.sieve(sieveLimit);
		int[] primeDRs=buildDigitalRoots(composites,false,SIZE);
		int[] compositeDRs=buildDigitalRoots(composites,true,SIZE);
		int[][] minLengths=getMinLengths(primeDRs,compositeDRs);
		long result=backtrack(minLengths,primeDRs,compositeDRs,MOD);
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
