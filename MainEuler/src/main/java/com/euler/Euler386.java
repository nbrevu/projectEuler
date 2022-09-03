package com.euler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler386 {
	private final static int LIMIT=IntMath.pow(10,8);
	private final static int[] FIRST_PRIMES=Primes.firstPrimeSieve(LIMIT);
	
	private static void addFactor(IntIntMap factors,int prime)	{
		factors.compute(prime,(int unusedKey,int oldValue)->1+oldValue);
	}
	
	private static IntIntMap getFactors(int in)	{
		IntIntMap result=HashIntIntMaps.newMutableMap();
		if (in==1) return result;
		for (;;)	{
			int prime=FIRST_PRIMES[in];
			if (prime==0)	{
				addFactor(result,in);
				return result;
			}
			addFactor(result,prime);
			in/=prime;
		}
	}
	
	private static class ArrayWrapper	{
		private final int[] array;
		public ArrayWrapper(int[] array)	{
			this.array=array;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(array);
		}
		@Override
		public boolean equals(Object other)	{
			return Arrays.equals(array,((ArrayWrapper)other).array);
		}
	}
	
	private static class Polynomial	{
		private final static IntObjMap<Polynomial> REPEATED_UNITS_CACHE=HashIntObjMaps.newMutableMap();
		private final int[] coefficients;	// Position indicates degree of x.
		private Polynomial(int[] coefficients)	{
			this.coefficients=coefficients;
		}
		public static Polynomial getRepeatedOnes(int size)	{
			return REPEATED_UNITS_CACHE.computeIfAbsent(size,(int s)->{
				int[] array=new int[s];
				Arrays.fill(array,1);
				return new Polynomial(array);
			});
		}
		public static Polynomial product(Polynomial a,Polynomial b)	{
			int N=a.coefficients.length+b.coefficients.length-1;
			int[] coeffs=new int[N];
			for (int i=0;i<a.coefficients.length;++i) for (int j=0;j<b.coefficients.length;++j) coeffs[i+j]+=a.coefficients[i]*b.coefficients[j];
			return new Polynomial(coeffs);
		}
		public int getMiddleCoeff()	{
			return coefficients[(coefficients.length-1)/2];
		}
	}
	
	private static class FactorsSummary	{
		private final static Map<ArrayWrapper,FactorsSummary> CACHE=new HashMap<>();
		private final int[] factors;
		private FactorsSummary(int[] factors)	{
			this.factors=factors;
		}
		public static FactorsSummary of(int n)	{
			IntIntMap factors=getFactors(n);
			int[] primePowers=factors.values().toIntArray();
			Arrays.sort(primePowers);
			return CACHE.computeIfAbsent(new ArrayWrapper(primePowers),(ArrayWrapper unused)->new FactorsSummary(primePowers));
		}
		public int getLongestAntichain()	{
			// https://oeis.org/A096825.
			Polynomial poly=Polynomial.getRepeatedOnes(1);
			for (int f:factors) poly=Polynomial.product(poly,Polynomial.getRepeatedOnes(1+f));
			return poly.getMiddleCoeff();
		}
		@Override
		public String toString()	{
			return Arrays.toString(factors);
		}
	}
	
	private static void increase(ObjIntMap<FactorsSummary> map,FactorsSummary key)	{
		map.compute(key,(FactorsSummary unused,int oldValue)->1+oldValue);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ObjIntMap<FactorsSummary> ordered=HashObjIntMaps.newMutableMap();
		for (int i=1;i<=LIMIT;++i)	{
			FactorsSummary summary=FactorsSummary.of(i);
			increase(ordered,summary);
		}
		long sum=0;
		ObjIntCursor<FactorsSummary> cursor=ordered.cursor();
		while (cursor.moveNext()) sum+=((long)cursor.value())*((long)cursor.key().getLongestAntichain());
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(sum+".");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
