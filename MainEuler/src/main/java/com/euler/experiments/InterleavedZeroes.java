package com.euler.experiments;

import java.util.TreeSet;
import java.util.function.LongConsumer;
import java.util.stream.IntStream;

import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class InterleavedZeroes {
	private final static long PRIME=37;
	private final static int DIGITS=5;
	
	private static class FixedDigitGenerator implements LongCursor	{
		private final long increment;
		private long currentValue;
		private final long limit;
		public FixedDigitGenerator(long prime,int digits)	{
			increment=prime;
			long firstPower=LongMath.pow(10l,digits-1);
			currentValue=firstPower-(firstPower%prime);
			limit=firstPower*10;
		}
		@Override
		public boolean moveNext() {
			currentValue+=increment;
			return currentValue<limit;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public void forEachForward(LongConsumer action) {
			while (moveNext()) action.accept(elem());
		}
		@Override
		public long elem() {
			return currentValue;
		}
	}
	
	private static class Decomposer	{
		private final int[] digitPlaceholder;
		public Decomposer(int size)	{
			digitPlaceholder=new int[size];
		}
		public int[] decompose(long value)	{
			for (int i=0;i<digitPlaceholder.length;++i)	{
				digitPlaceholder[i]=(int)(value%10);
				value/=10;
			}
			return digitPlaceholder;
		}
		public long pivot(int position)	{
			long result=0l;
			for (int i=position-1;i>=0;--i)	{
				result*=10l;
				result+=digitPlaceholder[i];
			}
			result*=10l;
			for (int i=digitPlaceholder.length-1;i>position;--i)	{
				result*=10l;
				result+=digitPlaceholder[i];
			}
			return result;
		}
	}
	
	private static int[] getZeroPositions(int[] digits)	{
		// I know I shouldn't ignore the zero, but it's just trivial.
		IntStream.Builder builder=IntStream.builder();
		for (int i=1;i<digits.length;++i) if ((digits[i]==0)&&(digits[i-1]!=0)) builder.accept(i);
		return builder.build().toArray();
	}
	
	public static void main(String[] args)	{
		IntIntMap digitCounter=HashIntIntMaps.newMutableMap();
		Decomposer decomp=new Decomposer(DIGITS);
		for (LongCursor cursor=new FixedDigitGenerator(PRIME,DIGITS);cursor.moveNext();)	{
			long original=cursor.elem();
			int[] zeroPos=getZeroPositions(decomp.decompose(original));
			digitCounter.addValue(zeroPos.length,1,0);
			if (zeroPos.length==0) continue;
			StringBuilder builder=new StringBuilder();
			builder.append(original);
			for (int pos:zeroPos)	{
				long newValue=decomp.pivot(pos);
				if ((newValue%PRIME)!=0) throw new RuntimeException("Invalid case detected: "+original+" <=> "+newValue+".");
				builder.append(" <=> ").append(newValue);
			}
			System.out.println(builder.toString());
		}
		System.out.println("All cases are correct.");
		for (int i:new TreeSet<>(digitCounter.keySet())) System.out.println("Found "+digitCounter.get(i)+" cases with exactly "+i+" groups.");
	}
}
