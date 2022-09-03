package com.euler;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler725 {
	private final static int N=2020;
	private final static long MOD=LongMath.pow(10l,16);
	
	private final static int MAX_DIGIT=9;
	
	private static IntObjMap<Set<IntIntMap>> generatePartitions(int maxSize)	{
		IntObjMap<Set<IntIntMap>> result=HashIntObjMaps.newMutableMap();
		for (int i=0;i<=maxSize;++i) result.put(i,new HashSet<>());
		result.get(0).add(HashIntIntMaps.newMutableMap());
		for (int i=0;i<maxSize;++i)	{
			int maxJ=maxSize-i;
			for (int j=1;j<=maxJ;++j)	{
				for (IntIntMap previousPartition:result.get(i))	{
					IntIntMap newPartition=HashIntIntMaps.newMutableMap(previousPartition);
					newPartition.addValue(j,1);
					result.get(i+j).add(newPartition);
				}
			}
		}
		result.remove(0);
		return result;
	}
	
	private static BigInteger[] getFactorials(int n)	{
		BigInteger[] result=new BigInteger[n+1];
		result[0]=BigInteger.ONE;
		for (int i=1;i<=n;++i) result[i]=result[i-1].multiply(BigInteger.valueOf(i));
		return result;
	}
	
	private static BigInteger[] generateVariations(int n,int upTo)	{
		// Puts in the nth element the result of the first N numbers (downward), i.e.: 1, 2020, 2020*2019, 2020*2019*2018...
		BigInteger[] result=new BigInteger[1+upTo];
		result[0]=BigInteger.ONE;
		int toMultiply=n;
		for (int i=1;i<=upTo;++i)	{
			result[i]=result[i-1].multiply(BigInteger.valueOf(toMultiply));
			--toMultiply;
		}
		return result;
	}
	
	private static BigInteger getRepunit(int size)	{
		return new BigInteger("1".repeat(size));
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		IntObjMap<Set<IntIntMap>> partitions=generatePartitions(MAX_DIGIT);
		BigInteger[] factorials=getFactorials(MAX_DIGIT);
		BigInteger[] variations=generateVariations(N,1+MAX_DIGIT);
		BigInteger result=BigInteger.ZERO;
		for (IntObjCursor<Set<IntIntMap>> cursor=partitions.cursor();cursor.moveNext();)	{
			int sum=cursor.key();
			for (IntIntMap partition:cursor.value())	{
				partition.addValue(sum,1);
				int nonZeroValues=0;
				for (int digitCount:partition.values()) nonZeroValues+=digitCount;
				BigInteger combinations=variations[nonZeroValues];
				for (int digitCount:partition.values()) if (digitCount>1) combinations=combinations.divide(factorials[digitCount]);
				int internalCounter=0;
				for (IntIntCursor cursor2=partition.cursor();cursor2.moveNext();) internalCounter+=cursor2.key()*cursor2.value();
				result=result.add(combinations.multiply(BigInteger.valueOf(internalCounter)));
			}
		}
		result=result.divide(BigInteger.valueOf(N)).multiply(getRepunit(N)).mod(BigInteger.valueOf(MOD));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
