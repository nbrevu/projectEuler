package com.euler;

import java.util.HashMap;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.math.LongMath;

public class Euler498 {
	private final static long N=LongMath.pow(10l,13);
	private final static long M=LongMath.pow(10l,12);
	private final static long D=LongMath.pow(10l,4);
	private final static long MOD=999999937l;
	
	private static class FactorsHolder	{
		private final RangeMap<Long,Integer> ranges;
		private final long mod;
		public FactorsHolder(long maxValue,int originalValue)	{
			ranges=TreeRangeMap.create();
			ranges.put(Range.closedOpen(0l,maxValue),originalValue);
			this.mod=maxValue;
		}
		public void increaseSingleNumber(long number,int amount)	{
			long position=number%mod;
			int currentValue=ranges.get(position);
			ranges.put(Range.closedOpen(position,position+1),currentValue+amount);
		}
		public void increaseRange(long start,long end,int amount)	{
			Range<Long> range=Range.closedOpen(start,end);
			RangeMap<Long,Integer> intersect=ranges.subRangeMap(range);
			Map<Range<Long>,Integer> toPut=new HashMap<>();
			for (Map.Entry<Range<Long>,Integer> entry:intersect.asMapOfRanges().entrySet())	{
				int newValue=entry.getValue()+amount;
				toPut.put(entry.getKey(),newValue);
			}
			for (Map.Entry<Range<Long>,Integer> entry:toPut.entrySet()) ranges.put(entry.getKey(),entry.getValue());
		}
		public void increaseFactorial(long in,boolean isDenominator)	{
			long position=in%mod;
			int howManyZeroes=(int)(in/mod);
			increaseRange(1,1+(in/mod),isDenominator?-1:1);	// Adding the factors that multiply the "zeroes" as well...
			if (position>0) increaseRange(1,position+1,isDenominator?-1:1);
			increaseRange(0,1,isDenominator?-howManyZeroes:howManyZeroes);
		}
		public boolean isZero()	{
			return ranges.get(0l).intValue()!=0;
		}
		public long getCombinatorialNumber()	{
			if (isZero()) return 0l;	// Incidentally this also takes care of the influence of Wilson's theorem... I think.
			long result=1l;
			for (Map.Entry<Range<Long>,Integer> entry:ranges.asMapOfRanges().entrySet())	{
				int power=entry.getValue();
				if (power==0) continue;
				Range<Long> range=entry.getKey();
				long product=getStringProduct(range.lowerEndpoint(),range.upperEndpoint());
				long factor=convert(product,power);
				result=(result*factor)%mod;
			}
			return result;
		}
		private long getStringProduct(long start,long end)	{
			long result=1l;
			for (long i=start;i<end;++i) result=(result*i)%mod;
			return result;
		}
		private long convert(long product,int power)	{
			if (power==0) return 1l;
			else if (power==1) return product;
			else if (power>1) return EulerUtils.expMod(product,power,mod);
			else if (power==-1) return EulerUtils.modulusInverse(product,mod);
			else return EulerUtils.modulusInverse(EulerUtils.expMod(product,-power,mod),mod);
		}
	}
	
	public static long C(long n,long m,long d,long mod)	{
		FactorsHolder factorsRange=new FactorsHolder(mod,0);
		factorsRange.increaseFactorial(n,false);
		factorsRange.increaseFactorial(n-m,true);
		factorsRange.increaseFactorial(d,true);
		factorsRange.increaseFactorial(m-d-1,true);
		factorsRange.increaseSingleNumber(n-d,-1);
		return factorsRange.getCombinatorialNumber();
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=C(N,M,D,MOD);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
