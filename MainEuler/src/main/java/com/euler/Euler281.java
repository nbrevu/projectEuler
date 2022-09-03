package com.euler;

import static com.euler.common.BigIntegerUtils.pow;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.BigIntegerUtils.BigFactorialCache;
import com.euler.common.EulerUtils;
import com.euler.common.LexicographicalArrayComparator;
import com.google.common.math.LongMath;

public class Euler281 {
	private static class Permutation	{
		public int[] values;
		public Permutation(int[] values)	{
			this.values=getCanonicalRepresentation(values);
		}
		private static int[] getCanonicalRepresentation(int[] array)	{
			NavigableSet<int[]> result=new TreeSet<>(LexicographicalArrayComparator.INSTANCE);
			for (int i=0;i<array.length;++i) result.add(copyWithCycling(array,i));
			return result.first();
		}
		private static int[] copyWithCycling(int[] array,int i)	{
			int len=array.length;
			int[] result=new int[len];
			System.arraycopy(array,0,result,i,len-i);
			System.arraycopy(array,len-i,result,0,i);
			return result;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(values);
		}
		@Override
		public boolean equals(Object other)	{
			return Arrays.equals(values,((Permutation)other).values);
		}
	}

	private static int bruteForce(int m,int n)	{
		int[] array=new int[m*n];
		for (int i=0;i<m;++i) Arrays.fill(array,n*i,n*(i+1),i+1);
		Set<Permutation> perms=new HashSet<>();
		do perms.add(new Permutation(array)); while (EulerUtils.nextPermutation(array));
		return perms.size();
	}
	
	private static BigInteger theoretical(int m,int n)	{
		BigFactorialCache cache=new BigFactorialCache(m*n);
		if (n==1) return cache.get(m-1);
		if (n!=2) throw new RuntimeException("AÚN NO POPOCH.");
		BigInteger result1=cache.get(m*n).divide(pow(cache.get(n),m));
		BigInteger result2=cache.get(m);
		return (result1.subtract(result2)).divide(BigInteger.valueOf(m*n)).add(result2.divide(BigInteger.valueOf(m)));
	}
	
	private static void printF(int m,int n)	{
		System.out.println("f("+m+","+n+")="+bruteForce(m,n)+".");
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=5;++i)	{
			System.out.println(bruteForce(i,2)+" <==> "+theoretical(i,2));
		}
		for (int x=6;;++x)	{
			BigInteger result=theoretical(x,2);
			System.out.println("x="+x+": result="+result+".");
			if (result.compareTo(BigInteger.valueOf(LongMath.pow(10l,15)))>0) break;
		}
		
		for (int i=1;i<=7;++i)	{
			System.out.println(bruteForce(i,1)+" <==> "+theoretical(i,1));
		}
		for (int x=8;;++x)	{
			BigInteger result=theoretical(x,1);
			System.out.println("x="+x+": result="+result+".");
			if (result.compareTo(BigInteger.valueOf(LongMath.pow(10l,15)))>0) break;
		}
		printF(2,3);
		printF(2,4);
		printF(2,5);
		printF(2,6);
		printF(2,7);
		printF(2,8);
		
		printF(3,3);
		printF(4,3);
		// printF(5,3);

		printF(2,1);
		printF(3,1);
		printF(4,3);
		printF(1,3);
		printF(1,6);
		printF(3,5);
		printF(2,4);
		printF(3,4);
		// printF(4,4);
		printF(2,6);
		printF(2,7);
		printF(2,8);
		printF(2,9);
		printF(2,10);
		printF(2,11);
		printF(2,12);
	}
}
