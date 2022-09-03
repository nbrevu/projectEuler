package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.koloboke.collect.set.LongSet;

public class Euler797_7 {
	public final static int N=10_000_000;
	public final static long MOD=1_000_000_007l;
	
	private static class DivisorsCounters	{
		public final long[] divisors;
		private final long[] counters;
		public DivisorsCounters(long[] divisors)	{
			this.divisors=divisors;
			counters=new long[divisors.length];
		}
		public long getValue(long upperLimit)	{
			if (upperLimit>divisors[divisors.length-1]) return counters[counters.length-1];
			int index=Arrays.binarySearch(divisors,upperLimit);
			if (index==0) return 0l;
			else if (index>0) --index;
			else index=-index-2;
			return counters[index];
		}
		public void setValue(long divisor,long value)	{
			int index=Arrays.binarySearch(divisors,divisor);
			counters[index]=(counters[index]+value)%MOD;
		}
	}
	private static class DivisorsCountersGenerator	{
		private final long[] primes;
		public DivisorsCountersGenerator(long upper)	{
			primes=Primes.firstPrimeSieve(upper);
		}
		public DivisorsCounters generateFor(long n)	{
			DivisorHolder decomposition=DivisorHolder.getFromFirstPrimes(n,primes);
			LongSet divisors=decomposition.getUnsortedListOfDivisors();
			long[] divisorsArray=divisors.toLongArray();
			Arrays.sort(divisorsArray);
			return new DivisorsCounters(divisorsArray);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		DivisorsCounters[] data=new DivisorsCounters[1+N];
		DivisorsCountersGenerator gen=new DivisorsCountersGenerator(N);
		for (int i=1;i<=N;++i) data[i]=gen.generateFor(i);
		long[] primitives=new long[1+N];
		long[] primitiveInverses=new long[1+N];
		data[1].setValue(1l,2l);
		primitives[1]=1l;
		primitiveInverses[1]=1l;
		long lastPower=1l;
		long result=2l;
		for (int i=2;i<=N;++i)	{
			lastPower=(2*lastPower+1)%MOD;
			long primitive=lastPower;
			for (long div:data[i].divisors) if (div!=i) primitive=(primitive*primitiveInverses[(int)div])%MOD;
			primitives[i]=primitive;
			primitiveInverses[i]=EulerUtils.modulusInverse(primitive,MOD);
			long lastValue=0l;
			for (int j=1;j<data[i].divisors.length;++j)	{
				long internalFactor=0l;
				long div=data[i].divisors[j];
				for (int k=0;k<data[i].divisors.length;++k)	{
					long otherDiv=data[i].divisors[k];
					if (EulerUtils.lcm(div,otherDiv)==i) internalFactor+=data[(int)otherDiv].getValue(div);
				}
				internalFactor%=MOD;
				lastValue=(lastValue+internalFactor*primitives[(int)div])%MOD;
				data[i].counters[j]=lastValue;
			}
			result=(result+lastValue)%MOD;
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
