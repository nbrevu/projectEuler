package com.euler;

import java.math.BigInteger;
import java.util.BitSet;

import com.koloboke.collect.map.ObjDoubleMap;
import com.koloboke.collect.map.hash.HashObjDoubleMaps;

public class Euler765 {
	private final static int N=1000;
	private final static long GOAL=1_000_000_000_000l;
	private final static double P=0.6;
	private final static double Q=1-P;
	
	private static class ValueCache	{
		private final ObjDoubleMap<BitSet> cache;
		private static boolean isAllOnes(BitSet b,int size)	{
			return b.nextClearBit(0)==size;
		}
		private static BitSet addOne(BitSet value)	{
			BitSet result=(BitSet)(value.clone());
			for (int i=0;;++i) if (result.get(i)) result.clear(i);
			else	{
				result.set(i);
				break;
			}
			return result;
		}
		public ValueCache()	{
			cache=HashObjDoubleMaps.newMutableMap();
			cache.put(BitSet.valueOf(new long[]{0}),0d);
			cache.put(BitSet.valueOf(new long[]{1}),P);
		}
		private static String toString(BitSet bits,int size)	{
			StringBuilder builder=new StringBuilder(N);
			for (int i=size;i<N;++i) builder.append(' ');
			for (int i=size-1;i>=0;--i) builder.append(bits.get(i)?'1':'0');
			return builder.toString();
		}
		public double check(BitSet bits,int size)	{
			if (cache.containsKey(bits)) return cache.getDouble(bits);
			//System.out.println(toString(bits,size));
			double result;
			boolean isLastBitSet=bits.get(0);
			BitSet parent=bits.get(1,size);
			if (!isLastBitSet) result=check(parent,size-1);
			else if (isAllOnes(parent,size-1)) result=P+Q*check(parent,size-1);
			else	{
				BitSet otherParent=addOne(parent);
				result=P*check(otherParent,size-1)+Q*check(parent,size-1);
			}
			System.out.println(toString(bits,size)+": "+result+".");
			cache.put(bits,result);
			return result;
		}
		public int size()	{
			return cache.size();
		}
	}
	
	private static void reverse(byte[] array)	{
		int len=array.length;
		int mid=len>>1;
		for (int i=0;i<mid;++i)	{
			int j=len-1-i;
			byte swap=array[i];
			array[i]=array[j];
			array[j]=swap;
		}
	}
	
	private static BitSet getInitialBitSet()	{
		BigInteger num=BigInteger.TWO.pow(N);
		BigInteger den=BigInteger.valueOf(GOAL);
		BigInteger quot=num.divide(den);
		byte[] bytes=quot.toByteArray();
		reverse(bytes);
		return BitSet.valueOf(bytes);
	}
	
	public static void main(String[] args)	{
		ValueCache cache=new ValueCache();
		double result=cache.check(getInitialBitSet(),N);
		System.out.println(result);
		System.out.println(cache.size());
	}
}
