package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler326_4 {
	private final static int M=IntMath.pow(10,6);
	private final static long N=LongMath.pow(10l,12);
	private final static int CYCLE_LENGTH=6*M;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long lastResult=1;	// <- Yes, this must be a long, not an int :D. Stupid overflows...
		int moddedSum=1;
		IntStream.Builder[] sumsHolder=new IntStream.Builder[M];
		for (int i=0;i<M;++i) sumsHolder[i]=IntStream.builder();
		sumsHolder[1].accept(0);
		BigInteger sum=BigInteger.ZERO;
		for (int i=1;i<CYCLE_LENGTH;++i)	{
			sum=sum.add(BigInteger.valueOf(lastResult*i));
			lastResult=sum.mod(BigInteger.valueOf(i+1)).intValueExact();
			moddedSum+=lastResult;
			moddedSum%=M;
			sumsHolder[moddedSum].accept(i);
		}
		long result=0;
		long fullCycles=N/CYCLE_LENGTH;
		int lastCycleLength=(int)(N%CYCLE_LENGTH);
		for (int i=0;i<M;++i)	{
			int[] array=sumsHolder[i].build().toArray();
			if (array.length==0) continue;
			int extraPos=Arrays.binarySearch(array,lastCycleLength);
			long elements=array.length*fullCycles;
			if (extraPos>=0) elements+=extraPos;
			else elements+=-1-extraPos;
			if (i==0) ++elements;
			result+=(elements*(elements-1))/2;
		}
		System.out.println(result);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);System.out.println("Elapsed "+seconds+" seconds.");
	}
}
