package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.LongFenwickTree;
import com.google.common.math.LongMath;

public class Euler755_2 {
	private final static long LIMIT=LongMath.pow(10l,13);
	
	private static long[] listFibosUnder(long limit)	{
		List<Long> result=new ArrayList<>();
		long fPrev=0l;
		long fCurr=1l;
		for (;;)	{
			long fNext=fPrev+fCurr;
			if (fNext>limit) break;
			result.add(fNext);
			fPrev=fCurr;
			fCurr=fNext;
		}
		return result.stream().mapToLong(Long::longValue).toArray();
	}
	
	private static long bitsetStyleSum(long bits,long[] fibos,int offset)	{
		long result=0;
		long mask=1;
		for (int i=0;mask<=bits;++i,mask<<=1) if ((bits&mask)!=0) result+=fibos[i+offset];
		return result;
	}
	
	private static LongFenwickTree initFenwickTree(long[] fibos,int howMany,int fenwickSize)	{
		LongFenwickTree result=new LongFenwickTree(fenwickSize);
		long maxBits=1l<<howMany;
		for (long i=0;i<maxBits;++i)	{
			int sum=(int)bitsetStyleSum(i,fibos,0);	// We know this fits in an int.
			result.putData(1+sum,1l);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] fibos=listFibosUnder(LIMIT);
		int howManyFibos=fibos.length;
		int firstPartFibos=(howManyFibos+1)/2;
		/*
		 * The sum of the first N fibos equals the N+2'th fibo minus 2.
		 * Also, since fibos grow exponentially, the max fibo involved in this kind of sum is expected to be of the order of 10^6.5, so the
		 * Fenwick tree shouldn't grow much.
		 */
		int firstPartMaxSum=(int)fibos[firstPartFibos+2];
		LongFenwickTree fenwickTree=initFenwickTree(fibos,firstPartFibos,firstPartMaxSum);
		int secondPartSize=howManyFibos-firstPartFibos;
		long maxBits=1l<<secondPartSize;
		long result=0l;
		for (long i=0;i<maxBits;++i)	{
			long sum=bitsetStyleSum(i,fibos,firstPartFibos);
			long remaining=LIMIT-sum;
			if (remaining<0) continue;
			else if (remaining>firstPartMaxSum) result+=fenwickTree.getTotal();
			else result+=fenwickTree.readData((int)remaining+1);
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
