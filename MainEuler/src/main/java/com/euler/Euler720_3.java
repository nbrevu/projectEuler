package com.euler;

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.math.LongMath;

public class Euler720_3 {
	private final static int MAX_POWER=25;
	private final static long MOD=LongMath.pow(10l,9)+7l;
	
	private final static int[] CASE_FOR_2=new int[] {1,3,2,4};
	
	private static int[] getNextCase(int[] previous)	{
		int N=previous.length;
		int[] result=new int[2*N];
		for (int i=0;i<N-1;++i) result[i]=2*previous[i]-1;
		result[N-1]=2;
		int offset1=2*N+1;
		int offset2=2*N-1;
		for (int i=N;i<result.length;++i) result[i]=offset1-result[offset2-i];
		return result;
	}
	
	private static int[] getFactoradicRepresentation(int[] array)	{
		int N=array.length;
		int[] result=new int[N];
		SortedSet<Integer> remaining=new TreeSet<>();
		for (int i=1;i<=N;++i) remaining.add(i);
		for (int i=0;i<array.length;++i)	{
			Integer element=Integer.valueOf(array[i]);
			result[i]=remaining.headSet(element).size();
			remaining.remove(element);
		}
		return result;
	}
	
	private static long calculateFactoradicIndex(int[] array,long mod)	{
		int[] factoradic=getFactoradicRepresentation(array);
		long currentValue=1;	// The first index is 1, not 0.
		long currentFactorial=1;
		int N=array.length;
		int offset=N-1;
		for (int i=1;i<N;++i)	{
			currentFactorial*=i;
			currentFactorial%=mod;
			currentValue+=factoradic[offset-i]*currentFactorial;
			currentValue%=mod;
		}
		return currentValue;
	}
	
	public static void main(String[] args) 	{
		long tic=System.nanoTime();
		int currentSize=2;
		int[] currentArray=CASE_FOR_2;
		for (++currentSize;currentSize<=MAX_POWER;++currentSize) currentArray=getNextCase(currentArray);
		long result=calculateFactoradicIndex(currentArray,MOD);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
