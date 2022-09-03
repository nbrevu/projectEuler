package com.euler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;

public class Euler282 {
	// This doesn't work and I think that the only missing detail is some extra treatment for powers of 2.
	private static class NumberWithFactors	{
		// This is DIRTY but it works. Also I still hate Java for not having arrays of generics.
		private final static List<Map<Integer,Integer>> FACTORS;
		static	{
			FACTORS=new ArrayList<>();
			FACTORS.add(null);	//0
			FACTORS.add(ImmutableMap.of());	//1
			FACTORS.add(ImmutableMap.of(2,1));	//2
			FACTORS.add(ImmutableMap.of(3,1));	//3
			FACTORS.add(ImmutableMap.of(2,2));	//4
			FACTORS.add(ImmutableMap.of(5,1));	//5
			FACTORS.add(ImmutableMap.of(2,1,3,1));	//6
		}
		private final Map<Integer,Integer> factors;
		private final long number;
		private NumberWithFactors phi;
		public NumberWithFactors(Map<Integer,Integer> factors)	{
			this.factors=factors;
			this.number=calculateNumber(factors);
		}
		private static long calculateNumber(Map<Integer,Integer> in)	{
			long result=1;
			for (Map.Entry<Integer,Integer> entry:in.entrySet()) result*=LongMath.pow(entry.getKey(),entry.getValue());
			return result;
		}
		private NumberWithFactors getPhi()	{
			if (phi==null)	{
				Map<Integer,Integer> result=new HashMap<>();
				for (Map.Entry<Integer,Integer> factor:factors.entrySet())	{
					Integer prime=factor.getKey();
					Integer exponent=factor.getValue();
					if (exponent>1) EulerUtils.increaseCounter(result,prime,exponent-1);
					EulerUtils.increaseCounter(result,FACTORS.get(prime-1));
				}
				phi=new NumberWithFactors(result);
			}
			return phi;
		}
		public long getNumber()	{
			return number;
		}
		public boolean isTrivial()	{
			return factors.isEmpty();
		}
		public Pair<Integer,NumberWithFactors> separateIntoPowersOfTwoAndTheRest()	{
			if (factors.containsKey(2))	{
				if (factors.size()==1) return new Pair<>(factors.get(2),null); // Only powers of two.
				else	{
					// Both powers of two and odd primes, we need to divide.
					Map<Integer,Integer> factorsOdd=new HashMap<>(factors);
					factorsOdd.remove(2);
					return new Pair<>(factors.get(2),new NumberWithFactors(factorsOdd));
				}
			}	else return new Pair<>(null,this);	// Only odd powers.
		}
	}

	private static long knuthArrowsForBase2(long b,long n,NumberWithFactors mod)	{
		// n is assumed to be >=1.
		if (mod.isTrivial()) return 0l;	// This is a KEY aspect, it ends recursion.
		else if (n==1) return EulerUtils.expMod(2l,b,mod.getNumber());
		else if (b==0) return 1l;
		else	{
			Pair<Integer,NumberWithFactors> separation=mod.separateIntoPowersOfTwoAndTheRest();
			Integer evenExponent=separation.first;
			NumberWithFactors odd=separation.second;
			if (evenExponent==null) return knuthArrowsForBase2OddPowers(b,n,odd);
			else if (odd==null) return knuthArrowsForBase2PowerOf2(b,n,evenExponent);
			else	{
				long n1=knuthArrowsForBase2PowerOf2(b,n,evenExponent);
				long n2=knuthArrowsForBase2OddPowers(b,n,odd);
				return EulerUtils.solveChineseRemainder(n1,LongMath.pow(2l,evenExponent),n2,odd.getNumber());
			}
		}
	}
	
	private static long knuthArrowsForBase2OddPowers(long b,long n,NumberWithFactors mod)	{
		if (mod.isTrivial()) return 0l;	// This ends the recursion as soon as we can.
		else if (n==1) return EulerUtils.expMod(2l,b,mod.getNumber());
		else if (b==0) return 1l;
		else return knuthArrowsForBase2OddPowers(knuthArrowsForBase2(b-1,n,mod.getPhi()),n-1,mod);
	}
	
	private static long knuthArrowsForBase2PowerOf2(long b,long n,int exponentOf2Mod)	{
		if (exponentOf2Mod<=b) return 0l;
		if (b==0) return 1;
		else if (b==1) return 2;
		else if (b==2) return 4;
		else if (n==1) return LongMath.pow(2l,(int)b);
		else if (n==2)	{
			if (b==3) return (exponentOf2Mod>4)?0:16;
			else if (b==4) return (exponentOf2Mod>16)?0:65536;
			else return 0;
		}	else if (n==3)	{
			if (b==3) return (exponentOf2Mod>16)?0:65536;
			else return 0;
		}	else return 0;
	}
	
	// A(0,0)=1.
	// A(1,1)=3.
	// A(2,2)=7.
	// Para otros valores, A(n,n)=2^{n-2}(n+3)-3
	// Consideramos también que a^{x}(b) = a^{x-1}(a^{x}(b-1)).
	private static long ackermann(long n,NumberWithFactors mod)	{
		if (n==0) return 1;
		else if (n==1) return 3;
		else if (n==2) return 7;
		long modNumber=mod.getNumber();
		return (knuthArrowsForBase2(n+3,n-2,mod)+(modNumber-3))%modNumber;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sum=0;
		Map<Integer,Integer> mod=ImmutableMap.of(2,8,7,8);	// 2^8*7^8 = 14^8.
		NumberWithFactors modObject=new NumberWithFactors(mod);
		for (int i=0;i<=6;++i) sum+=ackermann(i,modObject);
		sum%=modObject.getNumber();
		long tac=System.nanoTime();
		System.out.println(sum);
		double seconds=(tac-tic)/1e9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
